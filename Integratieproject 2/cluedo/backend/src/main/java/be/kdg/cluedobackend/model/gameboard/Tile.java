package be.kdg.cluedobackend.model.gameboard;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NodeEntity(label = "Tile")
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@Getter
@Setter
public class Tile {
    private @Id @GeneratedValue Long id;
    private int xCoord;
    private int yCoord;
    @JsonIgnore
    private @Relationship(type = "HAS_NEIGHBOUR", direction = Relationship.UNDIRECTED)
    Set<Neighbour> neighbours = new HashSet<>();

    public Tile(int xCoord, int yCoord){
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public Tile(Integer xCoord, Integer yCoord, long id) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.id = id;
    }

    public boolean sameLocation(Tile tile){
        return this.xCoord == tile.xCoord && this.yCoord == tile.yCoord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tile)) return false;
        Tile tile = (Tile) o;
        return id.longValue() == tile.id.longValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, xCoord, yCoord);
    }
}
