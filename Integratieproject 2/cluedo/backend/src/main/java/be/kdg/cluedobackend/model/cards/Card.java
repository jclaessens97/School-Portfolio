package be.kdg.cluedobackend.model.cards;

import be.kdg.cluedobackend.model.cards.types.CardType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
@Table
@Setter
public abstract class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cardId;
    private String text;
    private CardType cardType;
    private String url;

    public Card(String text, CardType cardType, String url) {
        this.text = text;
        this.cardType = cardType;
        this.url = url;
    }
}
