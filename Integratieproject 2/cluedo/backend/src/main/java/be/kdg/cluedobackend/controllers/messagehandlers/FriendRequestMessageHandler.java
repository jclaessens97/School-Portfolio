package be.kdg.cluedobackend.controllers.messagehandlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FriendRequestMessageHandler extends MessageHandler<String> {
    @Autowired
    public FriendRequestMessageHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/friendRequests/");
    }


    @Override
    public void sendMessage(Integer cluedoId, String username, UUID requestingUser) {
        this.webSocket.convertAndSend(CHANNEL + username, username);
    }
}