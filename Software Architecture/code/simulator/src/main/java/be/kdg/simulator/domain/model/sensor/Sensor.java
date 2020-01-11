package be.kdg.simulator.domain.model.sensor;

import lombok.Getter;
import lombok.Setter;

/**
 * Model class that represents a Sensor.
 */
@Getter
@Setter
public class Sensor {
    private SensorType sensorType;
    private int minValue;
    private int maxValue;
    private double value;
}
