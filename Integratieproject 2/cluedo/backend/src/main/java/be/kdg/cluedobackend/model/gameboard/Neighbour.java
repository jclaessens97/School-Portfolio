package be.kdg.cluedobackend.model.gameboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "HAS_NEIGHBOUR")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@Setter
public class Neighbour {
    private @Id @GeneratedValue Long id;
    private @StartNode Tile tile1;
    private @EndNode Tile tile2;

    public Neighbour(Tile tile1, Tile tile2) {
        this.tile1 = tile1;
        this.tile2 = tile2;
    }
}
