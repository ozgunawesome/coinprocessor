package services.ozzy.coinprocessor.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Getter
@Component
@ConfigurationProperties("binance")
public class BinanceProperties {

    private ApiOptions api = new ApiOptions();
    private BtcWithdrawOptions btc = new BtcWithdrawOptions();

    @Data
    public static class ApiOptions {
        private String key;
        private String secret;
    }

    @Data
    public static class BtcWithdrawOptions {

        private String walletName, walletAddress;
        @Min(0)
        private BigDecimal withdrawThreshold;
    }

}
