package be.kdg.rideservice.dto;

import be.kdg.rideservice.domain.model.vehicle.BikeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindNearestVehicleDto {
    private double xCoord;
    private double yCoord;
    private BikeType bikeType;
}
