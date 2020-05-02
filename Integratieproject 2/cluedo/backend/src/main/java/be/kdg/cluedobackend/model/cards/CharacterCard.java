package be.kdg.cluedobackend.model.cards;

import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CharacterCard extends Card {
    @Enumerated
    private CharacterType characterType;

    public CharacterCard(CharacterType type,  String url) {
        super(type.name(), CardType.CHARACTER, url);
        this.characterType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterCard)) return false;
        CharacterCard that = (CharacterCard) o;
        return characterType == that.characterType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterType);
    }
}
