package be.kdg.cluedobackend.controllers.messagehandlers;

import be.kdg.cluedobackend.dto.suggestion.SceneDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SuggestionMessageHandler extends MessageHandler<SceneDto> {
    @Autowired
    public SuggestionMessageHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/suggestionCards/");
    }

    @Override
    public void sendMessage(Integer cluedoId, SceneDto sceneDto, UUID requestingUser) {
        webSocket.convertAndSend(CHANNEL + cluedoId, sceneDto);
    }

    public void sendMessageId(Integer cluedoId,Integer playerId, SceneDto sceneDto, UUID requestingUser) {
        webSocket.convertAndSend(CHANNEL + cluedoId + "/playerId/" +playerId, sceneDto);
    }
}
