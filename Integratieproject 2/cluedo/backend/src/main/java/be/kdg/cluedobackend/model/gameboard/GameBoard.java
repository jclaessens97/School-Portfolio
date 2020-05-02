package be.kdg.cluedobackend.model.gameboard;

import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
@NoArgsConstructor(force = true, access = AccessLevel.PUBLIC)
@Getter
@Setter
public class GameBoard {
    private @Id String name;
    private @Relationship(type = "HAS_TILES") Set<Tile> tiles = new HashSet<>();
    private @Relationship(type = "HAS_SPAWNTILES") Set<SpawnTile> spawnTiles = new HashSet<>();
    private @Relationship(type = "HAS_ROOMS") Set<Room> rooms = new HashSet<>();
    private @Relationship(type = "HAS_CHARACTERS") Set<Character> characters = new HashSet<>();

    public void createTile(int xCoord, int yCoord) {
        Tile tile = new Tile(xCoord, yCoord);
        tiles.stream()
                .filter(t -> Math.abs(t.getXCoord()-tile.getXCoord())+Math.abs(t.getYCoord()-tile.getYCoord())==1)
                .forEach(t -> link(t,tile));
        tiles.add(tile);
    }

    public void createSpawnTile(int xCoord, int yCoord, CharacterType characterType) {
        SpawnTile tile = new SpawnTile(xCoord, yCoord, characterType);
        tiles.stream()
                .filter(t -> Math.abs(t.getXCoord()-tile.getXCoord())+Math.abs(t.getYCoord()-tile.getYCoord())==1)
                .forEach(t -> link(t,tile));
        spawnTiles.add(tile);
    }

    public void createDoor(int xCoord, int yCoord, boolean horizontal, Room room) {
        tiles.stream()
                .filter(t -> horizontal ? Math.abs(t.getXCoord()-xCoord)==1&&t.getYCoord()==yCoord :
                        t.getXCoord()==xCoord&&Math.abs(t.getYCoord()-yCoord)==1)
                .forEach(t -> link(t,room));
    }

    public Room createRoom(int xCoord, int yCoord, int width, int height, RoomType roomType) {
        Room room = new Room(xCoord,yCoord,width,height, roomType);
        rooms.add(room);
        return room;
    }

    public void link(Tile tile1, Tile tile2){
        Neighbour neighbour = new Neighbour(tile1, tile2);
        tile1.getNeighbours().add(neighbour);
        tile2.getNeighbours().add(neighbour);
    }

    public void linkPassage(Room room1, Room room2){
        Passage passage = new Passage(room1,room2);
        room1.getPassages().add(passage);
        room2.getPassages().add(passage);
    }

    public void initialiseCharacters(int gameId) {
        spawnTiles.forEach(s -> characters.add(new Character(s, s.getCharacterType(), gameId)));
    }


}
