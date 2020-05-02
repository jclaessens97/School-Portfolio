package be.kdg.cluedobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccusationDto {
    private boolean accusationOutcome;
    private PlayerDto winningPlayer;
    private boolean gameHasEnded;
}
