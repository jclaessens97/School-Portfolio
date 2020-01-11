package be.kdg.simulator.services.impl;

import be.kdg.simulator.commands.Command;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.commands.impl.SensorCommand;
import be.kdg.simulator.config.sensorservice.SensorGenerationProperties;
import be.kdg.simulator.domain.generators.ParameterGenerator;
import be.kdg.simulator.services.SensorSimulationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SensorSimulationServiceImpl implements SensorSimulationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorSimulationService.class);
    private final ParameterGenerator generator;
    private final Command command;
    private final SensorGenerationProperties sensorGenerationProperties;

    private boolean running;

    @Autowired
    public SensorSimulationServiceImpl(ParameterGenerator generator, SensorCommand command, SensorGenerationProperties sensorGenerationProperties) {
        this.generator = generator;
        this.command = command;
        this.sensorGenerationProperties = sensorGenerationProperties;
    }

    @Override
    public SensorGenerationProperties getSensorGenerationProperties() {
        return sensorGenerationProperties;
    }

    @Override
    public void updateSensorGenerationProperties(SensorGenerationProperties properties) {
        this.sensorGenerationProperties.setTimeSpanInMinutes(properties.getTimeSpanInMinutes());
        this.sensorGenerationProperties.setAvgDelayInMs(properties.getAvgDelayInMs());
        this.sensorGenerationProperties.setDelayVariance(properties.getDelayVariance());
        this.sensorGenerationProperties.setXCoordRange(properties.getXCoordRange());
        this.sensorGenerationProperties.setXCoordRange(properties.getYCoordRange());
        this.sensorGenerationProperties.setSensors(properties.getSensors());
    }

    @Override
    @Async
    public void startSimulationAsync() {
        LOGGER.info("Running sensor simulation");
        running = true;
        generator.initialize();
        SimulatorCommandContext ctx = new SimulatorCommandContext();
        generator.setContext(ctx);

        while (generator.hasNext()) {
            ctx = generator.generateNext();
            command.delay(ctx.getDelay());
            command.execute(ctx);
            LOGGER.info("Sensor command executed");
        }

        running = false;
        LOGGER.info("Sensor simulation finished executing");
    }

    @Override
    public boolean isSimulatorRunning() {
        return running;
    }
}
