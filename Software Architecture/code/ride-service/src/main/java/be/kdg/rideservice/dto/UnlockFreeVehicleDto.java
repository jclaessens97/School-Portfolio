package be.kdg.rideservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnlockFreeVehicleDto {
    private int userId;
    private short vehicleId;
}
