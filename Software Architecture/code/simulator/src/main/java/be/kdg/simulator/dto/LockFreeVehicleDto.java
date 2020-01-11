package be.kdg.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LockFreeVehicleDto {
    private int userId;
    private short vehicleId;
}
