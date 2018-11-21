package services.ozzy.coinprocessor.tasks;

import lombok.extern.slf4j.Slf4j;
import monero.wallet.MoneroWallet;
import monero.wallet.MoneroWalletRpc;
import monero.wallet.model.MoneroTx;
import monero.wallet.model.MoneroTxConfig;
import org.atteo.evo.inflector.English;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import services.ozzy.coinprocessor.properties.MoneroWalletProperties;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Hodl a certain percentage of XMR coming into the system
 * Transfer the rest to Binance (will be sold for BTC)
 */
@Component
@Slf4j
@EnableScheduling
public class MoneroRpcWalletTasks {

    private final MoneroWallet wallet;
    private final MoneroWalletProperties.SellOptions sellOptions;

    @Autowired
    public MoneroRpcWalletTasks(MoneroWalletProperties properties) throws URISyntaxException {

        this.sellOptions = properties.getSell();

        MoneroWalletProperties.RpcOptions rpc = properties.getRpc();

        if (StringUtils.isEmpty(rpc.getUsername())) {
            this.wallet = new MoneroWalletRpc(rpc.getAddress(), rpc.getPort());
        } else {
            this.wallet = new MoneroWalletRpc(rpc.getAddress(), rpc.getPort(), rpc.getUsername(), rpc.getPassword());
        }
    }

    // TODO implement percentage hodl
    @Scheduled(fixedRate = 60000, initialDelay = 10000)
    public void transferMoneroToBinanceWallet() {

        BigDecimal balance = BigDecimal.valueOf(wallet.getUnlockedBalance().longValueExact(), 12);

        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            log.info("Sweeping {} XMR to wallet {}", balance.toPlainString(), sellOptions.getWalletAddress());

            List<MoneroTx> moneroTxList = wallet.sweepAll(new MoneroTxConfig(
                    sellOptions.getWalletAddress(), sellOptions.getPaymentId(), null));

            if (!moneroTxList.isEmpty()) {
                moneroTxList.forEach(j -> log.info("Created new Monero tx: {}", j.getId()));
                log.info("Sent {} {} to XMR network",
                        moneroTxList.size(), English.plural("transaction", moneroTxList.size()));
            }

            printBalanceToLog();
        }
    }

    @Scheduled(fixedRate = 60000 * 10)
    public void printBalanceToLog() {

        log.info("Monero wallet balance: {} XMR total, {} XMR unlocked",
                BigDecimal.valueOf(wallet.getBalance().longValueExact(), 12).toPlainString(),
                BigDecimal.valueOf(wallet.getUnlockedBalance().longValueExact(), 12).toPlainString());
    }
}
