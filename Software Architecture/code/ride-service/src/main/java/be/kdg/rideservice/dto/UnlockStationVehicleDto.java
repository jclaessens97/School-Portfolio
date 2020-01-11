package be.kdg.rideservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnlockStationVehicleDto {
    private int userId;
    private short stationId;
}
