package be.kdg.cluedobackend.dto.suggestion;

import be.kdg.cluedobackend.dto.CardDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivingSuggestionDto {
    private int gameId;
    private List<CardDto> suggestionCards;
}
