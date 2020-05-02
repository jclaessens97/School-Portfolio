package be.kdg.cluedobackend.dto;

import be.kdg.cluedobackend.model.cards.types.CharacterType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceDto {
    private CharacterType type;
    private int gameId;
}
