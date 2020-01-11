package be.kdg.rideservice.config;

import be.kdg.rideservice.domain.model.ride.RideWithLocation;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class RideServiceConfig {
    @Bean
    GeometryFactory geometryFactory() {
        return new GeometryFactory();
    }

    @Bean
    Map<Short, RideWithLocation> currentRidesWithLocation() {
        return new LinkedHashMap<>();
    }
}
