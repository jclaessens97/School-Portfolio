package be.kdg.cluedobackend.controllers.messagehandlers;

import be.kdg.cluedobackend.model.gameboard.GameBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshGameMessageHandler extends MessageHandler<GameBoard> {
    @Autowired
    public RefreshGameMessageHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/refresh/");
    }

    @Override
    public void sendMessage(Integer cluedoId, GameBoard gameBoard, UUID requestingUser) {
        webSocket.convertAndSend(CHANNEL+cluedoId, "Update game data");
    }
}
