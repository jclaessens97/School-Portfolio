package be.kdg.cluedobackend.model.gameboard;

import be.kdg.cluedobackend.model.cards.types.CharacterType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label = "SpawnTile")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@Setter
public class SpawnTile extends Tile{
    private CharacterType characterType;

    public SpawnTile(int xCoord, int yCoord, CharacterType characterType) {
        super(xCoord, yCoord);
        this.characterType = characterType;
    }
}
