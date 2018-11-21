package services.ozzy.coinprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CoinprocessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinprocessorApplication.class, args);
    }
}
