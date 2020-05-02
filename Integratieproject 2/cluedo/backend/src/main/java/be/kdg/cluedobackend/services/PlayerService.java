package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.model.users.Player;

public interface PlayerService {
    Player getPlayerByCluedoIdAndPlayerId(int cluedoId, int playerId);
}
