package be.kdg.cluedobackend.dto;

import be.kdg.cluedobackend.model.game.Cluedo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LobbyDto {
    private String lobbyName;
    private int cluedoId;
    private int players;
    private int maxPlayers;
    private boolean joined;
    private String host;

    public LobbyDto(Cluedo cluedo, UUID userId) {
        this.lobbyName = cluedo.getLobbyName();
        this.cluedoId = cluedo.getCluedoId();
        this.players = cluedo.getPlayers().size();
        this.maxPlayers = cluedo.getMaxPlayers();
        this.joined = cluedo.getPlayers().stream().map(p -> p.getUser().getUserId()).collect(Collectors.toList()).contains(userId);
        this.host = cluedo.getHost().getUser().getUserName();
    }
}
