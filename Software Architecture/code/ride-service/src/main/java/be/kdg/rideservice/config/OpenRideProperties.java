package be.kdg.rideservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "openride.settings")
@PropertySource("classpath:openride.properties")
@Getter
@Setter
public class OpenRideProperties {
    private int timeBasedTreshold;
    private int locationBasedDistanceTreshold;
    private int locationBasedTimeTreshold;
}
