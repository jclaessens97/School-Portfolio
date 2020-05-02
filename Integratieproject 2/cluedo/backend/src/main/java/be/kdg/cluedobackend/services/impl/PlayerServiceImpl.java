package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.repository.PlayerRepository;
import be.kdg.cluedobackend.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player getPlayerByCluedoIdAndPlayerId(int cluedoId, int playerId) {
        return playerRepository.findByCluedo_CluedoIdAndPlayerId(cluedoId, playerId);
    }
}
