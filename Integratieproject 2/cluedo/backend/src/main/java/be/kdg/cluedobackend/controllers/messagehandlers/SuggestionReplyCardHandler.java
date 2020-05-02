package be.kdg.cluedobackend.controllers.messagehandlers;

import be.kdg.cluedobackend.dto.CardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SuggestionReplyCardHandler extends MessageHandler<CardDto> {
    @Autowired
    public SuggestionReplyCardHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/suggestionReply/");
    }
    @Override
    public void sendMessage(Integer cluedoId, CardDto cardDto, UUID requestingUser) {
        webSocket.convertAndSend(CHANNEL + cluedoId, cardDto);
    }
    public void sendMessageId(Integer cluedoId,Integer playerId, CardDto cardDto, UUID requestingUser) {
        webSocket.convertAndSend(CHANNEL + cluedoId + "/playerId/" +playerId, cardDto);
    }
}
