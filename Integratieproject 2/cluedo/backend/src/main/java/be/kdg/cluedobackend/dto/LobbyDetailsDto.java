package be.kdg.cluedobackend.dto;

import be.kdg.cluedobackend.model.game.Cluedo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LobbyDetailsDto {
    private int cluedoId;
    private String lobbyName;
    private List<PlayerDto> players;
    private int maxPlayers;
    private int turnDuration;
    private int playerId; // Your own playerId
    private int hostPlayerId;

    public LobbyDetailsDto(Cluedo cluedo, int playerId) {
        this.cluedoId = cluedo.getCluedoId();
        this.lobbyName = cluedo.getLobbyName();
        this.maxPlayers = cluedo.getMaxPlayers();
        this.turnDuration = cluedo.getTurnDuration();
        this.players = new ArrayList<>();
        this.playerId = playerId;
        this.hostPlayerId = cluedo.getHost().getPlayerId();
        cluedo.getPlayers().forEach(p -> this.players.add(new PlayerDto(p.getUser().getUserName(), p.getCharacterType(), p.getPlayerId())));
    }
}
