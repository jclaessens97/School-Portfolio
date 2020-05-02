package be.kdg.cluedobackend.model.gameboard;

import be.kdg.cluedobackend.model.cards.types.CharacterType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "Character")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@Setter
public class Character {
    private @Id @GeneratedValue Long id;
    private CharacterType characterType;
    private int gameId;
    private @Relationship(type = "STANDS_ON") Tile position;

    public Character(Tile tile, CharacterType characterType, int gameId) {
        this.characterType = characterType;
        this.position = tile;
        this.gameId = gameId;
    }
}
