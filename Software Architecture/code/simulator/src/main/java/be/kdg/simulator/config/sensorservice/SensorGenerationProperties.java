package be.kdg.simulator.config.sensorservice;

import be.kdg.simulator.config.application.YamlPropertySourceFactory;
import be.kdg.simulator.domain.model.sensor.Sensor;
import be.kdg.simulator.domain.model.util.CoordRange;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * Configuration class for properties that are used for generating Sensor Messages
 */
@Configuration
@ConfigurationProperties(prefix = "sensor.generation")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:sensorgeneration.yml")
@Getter
@Setter
public class SensorGenerationProperties {
    private int timeSpanInMinutes;
    private int avgDelayInMs;
    private int delayVariance;
    private CoordRange xCoordRange;
    private CoordRange yCoordRange;
    private List<Sensor> sensors;
}
