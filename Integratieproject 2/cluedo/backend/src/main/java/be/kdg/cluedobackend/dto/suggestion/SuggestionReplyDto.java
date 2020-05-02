package be.kdg.cluedobackend.dto.suggestion;

import be.kdg.cluedobackend.dto.CardDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionReplyDto {
    private int gameId;
    private CardDto card;
}
