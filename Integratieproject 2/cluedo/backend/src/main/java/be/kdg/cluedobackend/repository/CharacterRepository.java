package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.gameboard.Character;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Set;

@RepositoryRestResource(collectionResourceRel = "Characters", path = "Characters")
@Repository
public interface CharacterRepository extends Neo4jRepository<Character, Long> {
    @Query("MATCH p=(:Tile)-[:STANDS_ON]-(:Character {gameId: $id}) RETURN p")
    Set<Character> findCharactersByGameId(@Param("id") int gameId);
}
