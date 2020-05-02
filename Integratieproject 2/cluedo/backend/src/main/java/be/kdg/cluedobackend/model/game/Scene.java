package be.kdg.cluedobackend.model.game;

import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.cards.CharacterCard;
import be.kdg.cluedobackend.model.cards.RoomCard;
import be.kdg.cluedobackend.model.cards.WeaponCard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table
public class Scene {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer caseFileId;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private CharacterCard characterCard;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private WeaponCard weaponCard;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private RoomCard roomCard;

    private SceneType sceneType;

    public Scene(CharacterCard characterCard, WeaponCard weaponCard, RoomCard roomCard, SceneType sceneType) {
        this.characterCard = characterCard;
        this.weaponCard = weaponCard;
        this.roomCard = roomCard;
        this.sceneType = sceneType;
    }

    public List<Card> getAllCards() {
        return List.of(
            characterCard,
            weaponCard,
            roomCard
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Scene)) return false;
        Scene oScene = (Scene) o;
        return oScene.characterCard.equals(this.characterCard)
                &&oScene.roomCard.equals(this.roomCard)
                &&oScene.weaponCard.equals(this.weaponCard);
    }
}
