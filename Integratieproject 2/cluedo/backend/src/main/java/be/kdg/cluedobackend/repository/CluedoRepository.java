package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.game.Cluedo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CluedoRepository extends JpaRepository<Cluedo, Integer> {
    List<Cluedo> findAllByActiveIsFalse();
    List<Cluedo> findAllByActiveIsTrue();
    List<Cluedo> findAllByPlayers_User_UserIdAndActiveIsTrue(UUID userId);

}
