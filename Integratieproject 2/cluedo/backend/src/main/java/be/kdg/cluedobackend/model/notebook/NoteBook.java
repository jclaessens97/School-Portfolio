package be.kdg.cluedobackend.model.notebook;

import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.users.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table
public class NoteBook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer noteBookId;

    @OneToOne(mappedBy = "noteBook")
    private Player player;

    @OneToMany
    @JoinColumn
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<NoteLine> characters;

    @OneToMany
    @JoinColumn
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<NoteLine> weapons;

    @OneToMany
    @JoinColumn
    @LazyCollection(LazyCollectionOption.FALSE)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<NoteLine> rooms;

    public NoteBook(Map<CardType, List<NoteLine>> noteLines) {
        this.characters = noteLines.get(CardType.CHARACTER);
        this.weapons = noteLines.get(CardType.WEAPON);
        this.rooms = noteLines.get(CardType.ROOM);
    }

    public int getNumberOfColumns() {
        return this.characters.get(0).getColumns().size();
    }
}
