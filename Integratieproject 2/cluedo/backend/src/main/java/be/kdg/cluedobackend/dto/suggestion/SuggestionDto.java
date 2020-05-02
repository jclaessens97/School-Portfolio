package be.kdg.cluedobackend.dto.suggestion;

import be.kdg.cluedobackend.dto.CardDto;
import be.kdg.cluedobackend.model.users.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionDto {
    private Player askingPlayer;
    private Player respondingPlayer;
    private List<CardDto> cards;
}
