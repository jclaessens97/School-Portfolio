package be.kdg.cluedobackend.controllers.messagehandlers;

import be.kdg.cluedobackend.dto.LobbyDetailsDto;
import be.kdg.cluedobackend.model.game.Cluedo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LobbyMessageHandler extends MessageHandler<Cluedo> {
    @Autowired
    public LobbyMessageHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/lobby/");
    }

    @Override
    public void sendMessage(Integer cluedoId, Cluedo cluedo, UUID requestingUser) {
        LobbyDetailsDto dto = new LobbyDetailsDto(cluedo, 0);
        webSocket.convertAndSend(CHANNEL + cluedoId, dto);
    }
}
