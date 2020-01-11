package be.kdg.simulator.domain.generators.impl;

import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.config.sensorservice.SensorGenerationProperties;
import be.kdg.simulator.domain.generators.ParameterGenerator;
import be.kdg.simulator.domain.model.sensor.Sensor;
import be.kdg.simulator.domain.model.util.CoordRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Generator class to generate parameters for sensor simulation based on given properties
 */
@Component
public class SensorParameterGenerator implements ParameterGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorParameterGenerator.class);
    private final SensorGenerationProperties properties;

    private SimulatorCommandContext ctx;
    private long currentCycle;
    private long numberOfCycles;

    @Autowired
    public SensorParameterGenerator(SensorGenerationProperties properties) {
        this.properties = properties;
    }

    @Override
    public void initialize() {
        numberOfCycles = TimeUnit.MINUTES.toMillis(properties.getTimeSpanInMinutes()) / properties.getAvgDelayInMs();
        currentCycle = 0;
    }

    @Override
    public void setContext(SimulatorCommandContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean hasNext() {
        return currentCycle < numberOfCycles;
    }

    @Override
    public SimulatorCommandContext generateNext() {
        ctx.setDelay(getRandomDelay());
        ctx.setTimeStamp(getRandomTimeStamp(properties.getAvgDelayInMs(), properties.getDelayVariance()));
        ctx.setXCoord(getRandomCoord(properties.getXCoordRange()));
        ctx.setYCoord(getRandomCoord(properties.getYCoordRange()));
        Sensor sensorWithValue = getRandomSensor(properties.getSensors());
        ctx.setSensorType(sensorWithValue.getSensorType());
        ctx.setSensorValue(sensorWithValue.getValue());

        currentCycle++;
        LOGGER.info(String.format("Generated parameters for %d of %d sensors", currentCycle, numberOfCycles));
        return ctx;
    }

    //#region Generation methods
    private long getRandomDelay() {
        return Math.round(
            getRandomValueWithBoundaries(
                properties.getAvgDelayInMs() - properties.getDelayVariance(),
                properties.getAvgDelayInMs() + properties.getDelayVariance()
            )
        );
    }

    private LocalDateTime getRandomTimeStamp(int avgDelay, int avgVariance) {
        final long minutesToRetract = currentCycle * (avgDelay + avgVariance);
        return LocalDateTime.now().minusMinutes(minutesToRetract);
    }

    private double getRandomCoord(CoordRange coordRange) {
        return getRandomValueWithBoundaries(
            coordRange.getMinValue(),
            coordRange.getMaxValue()
        );
    }

    private Sensor getRandomSensor(List<Sensor> sensors) {
        final int randomIndex = new Random().nextInt(sensors.size());
        final Sensor sensor = sensors.get(randomIndex);
        final double value = getRandomValueWithBoundaries(
            sensor.getMinValue(),
            sensor.getMaxValue()
        );

        sensor.setValue(value);
        return sensor;
    }
    //#endregion

    //#region Helper methods
    private double getRandomValueWithBoundaries(double min, double max) {
        return min + (max - min) * new Random().nextDouble();
    }
    //#endregion
}
