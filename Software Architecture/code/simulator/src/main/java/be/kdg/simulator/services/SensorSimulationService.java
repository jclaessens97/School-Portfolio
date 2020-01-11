package be.kdg.simulator.services;

import be.kdg.simulator.config.sensorservice.SensorGenerationProperties;

/**
 * Handles the sensor simulation
 */
public interface SensorSimulationService {
    SensorGenerationProperties getSensorGenerationProperties();
    void updateSensorGenerationProperties(SensorGenerationProperties properties);
    void startSimulationAsync();
    boolean isSimulatorRunning();
}
