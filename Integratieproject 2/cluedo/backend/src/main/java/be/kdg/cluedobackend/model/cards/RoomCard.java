package be.kdg.cluedobackend.model.cards;

import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
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
public class RoomCard extends Card {
    @Enumerated
    private RoomType roomType;

    public RoomCard(RoomType type, String url) {
        super(type.getName(), CardType.ROOM,url);
        this.roomType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomCard)) return false;
        RoomCard roomCard = (RoomCard) o;
        return roomType == roomCard.roomType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomType);
    }
}
