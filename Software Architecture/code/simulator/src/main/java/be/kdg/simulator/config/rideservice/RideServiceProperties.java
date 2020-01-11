package be.kdg.simulator.config.rideservice;

import be.kdg.simulator.config.application.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "ride.service")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:rideservice.yml")
@Getter
@Setter
public class RideServiceProperties {
    private String baseUri;
    private Map<String, String> routes;
}
