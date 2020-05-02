package be.kdg.cluedobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TurnDto {
    private PlayerDto player;
    private int timeRemaining;
}
