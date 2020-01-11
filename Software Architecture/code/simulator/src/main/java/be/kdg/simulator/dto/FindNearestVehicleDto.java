package be.kdg.simulator.dto;

import be.kdg.simulator.domain.model.ride.BikeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FindNearestVehicleDto {
    private double xCoord;
    private double yCoord;
    private BikeType bikeType;
}
