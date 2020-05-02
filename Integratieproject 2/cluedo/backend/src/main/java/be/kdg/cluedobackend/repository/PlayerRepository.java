package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.users.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Player findByCluedo_CluedoIdAndPlayerId(int cluedoId, int playerId);
    Player findByPlayerId(Integer playerId);

    List<Player> findAllByCluedo_CluedoId(int gameId);
}
