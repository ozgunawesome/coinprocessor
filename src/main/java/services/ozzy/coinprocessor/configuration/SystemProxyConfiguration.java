package services.ozzy.coinprocessor.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import services.ozzy.coinprocessor.properties.SystemProxyProperties;

@Configuration
public class SystemProxyConfiguration {

    @Autowired
    public SystemProxyConfiguration(SystemProxyProperties properties) {

        if (properties.isEnabled()) {
            System.setProperty("socksProxyHost", properties.getHost());
            System.setProperty("socksProxyPort", String.valueOf(properties.getPort()));
        }
    }
}
