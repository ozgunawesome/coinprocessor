package services.ozzy.coinprocessor.configuration;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import services.ozzy.coinprocessor.properties.BinanceProperties;

@Configuration
@EnableScheduling
public class BinanceConfiguration {

    private final BinanceApiRestClient syncClient;
    private final BinanceApiAsyncRestClient asyncClient;

    public BinanceConfiguration(BinanceProperties properties) {

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
                properties.getApi().getKey(), properties.getApi().getSecret());

        this.syncClient = factory.newRestClient();
        this.asyncClient = factory.newAsyncRestClient();
    }

    @Bean
    public BinanceApiRestClient synchronousClient() {
        return this.syncClient;
    }

    @Bean
    public BinanceApiAsyncRestClient asyncClient() {
        return this.asyncClient;
    }

    @Scheduled(fixedRate = 60000)
    private void keepClientAlive() {
        this.syncClient.ping();
        this.asyncClient.ping(response -> {});
    }

}
