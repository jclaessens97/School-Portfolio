package be.kdg.cluedobackend.dto;

import be.kdg.cluedobackend.model.gameboard.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PossibilitiesDto {
    private boolean hasTurn;
    private boolean movesPossible;
    private Room roomWithPassage;
    private LocationDto currentLocation;
    private int thrownDice;
}
