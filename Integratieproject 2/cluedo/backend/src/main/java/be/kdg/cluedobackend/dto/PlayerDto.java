package be.kdg.cluedobackend.dto;

import be.kdg.cluedobackend.model.cards.types.CharacterType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private String name;
    private CharacterType characterType;
    private Integer playerId;
}
