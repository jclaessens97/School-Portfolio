package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.gameboard.Room;
import be.kdg.cluedobackend.model.gameboard.Tile;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@RepositoryRestResource(collectionResourceRel = "Tiles", path = "Tiles")
@Repository
public interface TileRepository extends Neo4jRepository<Tile, Long> {
    @Query("MATCH p=(:Character {characterType: $type, gameId: $gameId})-[]-(t:Tile )<-[:HAS_NEIGHBOUR*0..12]->(neigh:Tile) " +
            "WHERE [b in RANGE($step,0,-2) WHERE b = SIZE(NODES(p)[2..])] " +
            "OR SIZE(NODES(p)[2..]) <= $step AND [n in NODES(p)[2..] WHERE 'Room' IN LABELS(n)] " +
            "return NODES(p)[2..]")
    Set<List<Object>> findPositionsInRange(@Param("type") CharacterType type, @Param("step") int step, @Param("gameId") int gameId);

    @Query("MATCH (c:Character {characterType: $type, gameId: $gameId})-[s:STANDS_ON]->(:Tile), " +
            "(t:Tile {xCoord: $x, yCoord: $y })-[]-(:GameBoard)-[]-(c) " +
            "DELETE s  CREATE (c)-[:STANDS_ON]->(t)  RETURN t")
    Tile moveCharacter(@Param("type") CharacterType type, @Param("x") int x, @Param("y") int y, @Param("gameId") int gameId );

    @Query("MATCH (c:Character {characterType: $type, gameId: $gameId})-[s:STANDS_ON]->(:Tile), " +
            "(t:Room {roomtype: $roomType })-[]-(:GameBoard)-[]-(c) " +
            "DELETE s  CREATE (c)-[:STANDS_ON]->(t)  RETURN t")
    Tile moveToRoom(@Param("type") CharacterType type, @Param("x")RoomType roomType, @Param("gameId") int gameId );

    @Query("MATCH (c:Character {characterType: $type, gameId: $gameId})-[s:STANDS_ON]->(curr:Room), " +
            "(next:Room)-[:HAS_PASSAGE]-(curr) " +
            "DELETE s  CREATE (c)-[:STANDS_ON]->(next)  RETURN next")
    Room moveThroughPassage(@Param("type") CharacterType type, @Param("gameId") int gameId );

    @Query("MATCH (c:Character {characterType: $type, gameId: $gameId})-[s:STANDS_ON]->(curr:Room), " +
            "(next:Room)-[:HAS_PASSAGE]-(curr) RETURN next")
    Room getPassage(@Param("type") CharacterType type, @Param("gameId") int gameId);
}
