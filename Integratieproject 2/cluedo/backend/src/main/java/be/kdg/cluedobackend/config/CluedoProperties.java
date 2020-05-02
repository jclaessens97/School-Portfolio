package be.kdg.cluedobackend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "default")
@PropertySource("classpath:cluedo.properties")
@Getter
@Setter
public class CluedoProperties {
    private int turnDuration;
    private int maxPlayers;
}
