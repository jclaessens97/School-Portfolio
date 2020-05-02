package be.kdg.cluedobackend.model.users;

import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.notebook.NoteBook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer playerId;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "notebook_id", referencedColumnName = "id")
    private NoteBook noteBook;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn
    private Cluedo cluedo;

    private CharacterType characterType;

    private Integer game_order;

    private boolean active = true;

    @ManyToMany
    @JoinColumn
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Card> cardsInHand;

    public Player() {
        this.cardsInHand = new ArrayList<>();
    }

    public Player(User user, Cluedo cluedo, CharacterType characterType) {
        this();
        this.user = user;
        this.cluedo = cluedo;
        this.characterType = characterType;
    }

    public UUID getUserId() {
        return user.getUserId();
    }

    public boolean hasCardInHand(Card card) {
        return cardsInHand
            .stream()
            .anyMatch(c -> c.equals(card));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(playerId, player.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}
