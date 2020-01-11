package be.kdg.simulator.commands;

import be.kdg.simulator.domain.model.ride.BikeType;
import be.kdg.simulator.domain.model.sensor.SensorType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class handles the parameters and return values of the commands.
 * This could be a component (bound to the request/session context) but because
 * we run the simulations in seperate threads, we can't access the WebContext from there.
 * Singleton is no option because then the context would be shared across all users.
 * One new instance is created at the start of each simulation and can be safely garbage collected after.
 */
@Getter
@Setter
public class SimulatorCommandContext {
    private int userId;
    private short stationId;
    private short lockId;
    private short vehicleId;
    private LocalDateTime timeStamp;
    private double xCoord;
    private double yCoord;
    private BikeType bikeType;
    private long delay;
    private SensorType sensorType;
    private double sensorValue;
    private List<Short> freeLockIds;
}
