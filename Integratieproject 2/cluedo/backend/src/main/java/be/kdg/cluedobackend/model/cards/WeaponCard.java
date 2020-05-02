package be.kdg.cluedobackend.model.cards;

import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.cards.types.WeaponType;
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
public class WeaponCard extends Card {
    @Enumerated
    private WeaponType weaponType;

    public WeaponCard(WeaponType type, String url) {
        super(type.getName(), CardType.WEAPON, url);
        this.weaponType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeaponCard)) return false;
        WeaponCard that = (WeaponCard) o;
        return weaponType == that.weaponType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(weaponType);
    }
}
