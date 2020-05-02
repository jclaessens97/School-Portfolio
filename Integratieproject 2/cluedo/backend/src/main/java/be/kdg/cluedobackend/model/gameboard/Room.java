package be.kdg.cluedobackend.model.gameboard;

import be.kdg.cluedobackend.model.cards.types.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity(label = "Room")
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@Setter
public class Room extends Tile{
    private RoomType roomType;
    private int width;
    private int height;

    public Room(int xCoord, int yCoord, RoomType roomType, long id) {
        super(xCoord, yCoord, id);
        this.roomType = roomType;
    }

    @JsonIgnore
    private @Relationship(type = "HAS_PASSAGE", direction = Relationship.UNDIRECTED) Set<Passage> passages = new HashSet<>();

    public Room(int xCoord, int yCoord, int width, int height, RoomType roomType) {
        super(xCoord,yCoord);
        this.width = width;
        this.height = height;
        this.roomType = roomType;
    }
}
