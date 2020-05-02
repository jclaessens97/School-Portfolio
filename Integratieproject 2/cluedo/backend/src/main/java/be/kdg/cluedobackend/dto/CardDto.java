package be.kdg.cluedobackend.dto;

import be.kdg.cluedobackend.model.cards.types.CardType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
    private Integer cardId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private CardType cardType;
    private String type;
    private String url;
}
