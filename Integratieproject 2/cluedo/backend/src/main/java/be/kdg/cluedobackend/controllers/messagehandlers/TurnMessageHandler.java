package be.kdg.cluedobackend.controllers.messagehandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TurnMessageHandler extends MessageHandler<String> {
    @Autowired
    public TurnMessageHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/newTurn/");
    }

    @Override
    public void sendMessage(Integer cluedoId, String s, UUID requestingUser) {
        webSocket.convertAndSend(CHANNEL+cluedoId,s);
    }
}
