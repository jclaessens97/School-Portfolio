package be.kdg.cluedobackend.model.gameboard;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "HAS_PASSAGE_TO")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@Setter
public class Passage {
    private @Id @GeneratedValue Long id;
    private @StartNode Room room1;
    private @EndNode Room room2;

    public Passage(Room room1, Room room2) {
        this.room1 = room1;
        this.room2 = room2;
    }
}
