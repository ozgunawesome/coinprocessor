package services.ozzy.coinprocessor.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;

@Data
@Component
@ConfigurationProperties("system.proxy")
public class SystemProxyProperties {

    private String host;

    @Min(1024)
    private int port;

    private boolean enabled;
}
