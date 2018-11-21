package services.ozzy.coinprocessor.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Getter
@Component
@ConfigurationProperties("monero")
public class MoneroWalletProperties {

    private RpcOptions rpc = new RpcOptions();
    private HodlOptions hodl = new HodlOptions();
    private SellOptions sell = new SellOptions();

    @Data
    public static class RpcOptions {
        private String address, username, password;
        @Min(1024)
        private int port;
    }

    @Data
    public static class HodlOptions {
        @NotBlank
        private String walletName, walletAddress;
        private BigDecimal withdrawThreshold;
    }

    @Data
    public static class SellOptions {
        private String walletAddress, paymentId;
        private BigDecimal threshold;
    }

}
