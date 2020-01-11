package be.kdg.simulator.services;

import org.springframework.web.multipart.MultipartFile;

/**
 * Handles the ride simulation
 */
public interface RideSimulationService {
    void startSimulationAsync(MultipartFile file);
}
