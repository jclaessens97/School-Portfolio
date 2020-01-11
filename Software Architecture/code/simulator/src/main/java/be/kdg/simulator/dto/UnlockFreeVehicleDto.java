package be.kdg.simulator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UnlockFreeVehicleDto {
    private int userId;
    private short vehicleId;
}
