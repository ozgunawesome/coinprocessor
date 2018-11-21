package services.ozzy.coinprocessor.tasks;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.account.DepositAddress;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.NewOrderResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import services.ozzy.coinprocessor.properties.BinanceProperties;
import services.ozzy.coinprocessor.properties.MoneroWalletProperties;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.binance.api.client.domain.account.NewOrder.marketSell;

/**
 * Sell all XMR in account directly to BTC, and withdraw BTC to hardware wallet if more than certain amount
 */
@Component
@Slf4j
public class BinanceTasks {

    private final BigDecimal btcWithdrawLimit;
    private final String btcWalletAddress, btcWalletName;
    private final MoneroWalletProperties moneroProperties;

    private final BinanceApiRestClient client;

    public BinanceTasks(BinanceApiRestClient client,
                        BinanceProperties binanceProperties,
                        MoneroWalletProperties moneroProperties) {

        this.btcWalletName = binanceProperties.getBtc().getWalletName();
        this.btcWalletAddress = binanceProperties.getBtc().getWalletAddress();
        this.btcWithdrawLimit = binanceProperties.getBtc().getWithdrawThreshold();
        this.moneroProperties = moneroProperties;

        this.client = client;
    }

    @Scheduled(fixedRate = 60000, initialDelay = 20000)
    void sellXmr() {
        BigDecimal xmrBalance = new BigDecimal(client.getAccount().getAssetBalance("XMR").getFree())
                .setScale(3, RoundingMode.DOWN);

        if (xmrBalance.compareTo(moneroProperties.getSell().getThreshold()) > 0) {
            log.info("Selling {} XMR at market price", xmrBalance.toPlainString());

            NewOrderResponse newOrderResponse = client.newOrder(marketSell("XMRBTC", xmrBalance.toPlainString())
                    .newOrderRespType(NewOrderResponseType.FULL));

            log.info("Market sell order: {}", newOrderResponse);
        }
    }

    @Scheduled(fixedRate = 60000, initialDelay = 30000)
    void withdrawBtc() {
        BigDecimal btcBalance = new BigDecimal(client.getAccount().getAssetBalance("BTC").getFree());

        if (btcBalance.compareTo(btcWithdrawLimit) > 0) {
            log.info("Processing withdraw for {} BTC", btcBalance.toPlainString());

            log.info("Withdraw result: {}",
                    client.withdraw("BTC", btcWalletAddress, btcBalance.toPlainString(), null, null));

            printBtcBalance();
        }
    }

    @Scheduled(fixedRate = 60000)
    void updateXmrWalletAddress() {
        DepositAddress address = client.getDepositAddress("XMR");
        if (address.isSuccess()) {
            moneroProperties.getSell().setWalletAddress(address.getAddress());
            moneroProperties.getSell().setPaymentId(address.getAddressTag());
        }
    }

    @Scheduled(fixedRate = 10 * 60000)
    void printBtcBalance() {
        AssetBalance assetBalance = client.getAccount().getAssetBalance("BTC");
        log.info("Bitcoin balance: {} BTC total, {} BTC free", assetBalance.getLocked(), assetBalance.getFree());
    }

}
