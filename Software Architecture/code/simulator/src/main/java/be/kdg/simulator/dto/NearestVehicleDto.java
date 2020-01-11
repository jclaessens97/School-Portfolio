package be.kdg.simulator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearestVehicleDto {
    private short vehicleId;
    private double xCoord;
    private double yCoord;
}
