package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.gameboard.GameBoard;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(collectionResourceRel = "Boards", path = "Boards")
@Repository
public interface BoardRepository extends Neo4jRepository<GameBoard, Long> {
    @Query("MATCH p=()-->() DELETE p")
    void removeAll();

    GameBoard findByName(@Param("name") String name);

    @Query("MATCH p=(g:GameBoard)-[:HAS_ROOMS|:HAS_TILES|:HAS_SPAWNTILES]-(t), " +
            "c=(g)-[:HAS_CHARACTERS]-(:Character {gameId: $id}) " +
            "RETURN p,c")
    GameBoard findByGameId( @Param("id") int id);
}
