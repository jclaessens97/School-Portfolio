package be.kdg.cluedobackend.controllers.messagehandlers;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.UUID;

public abstract class MessageHandler<T> {
    protected final SimpMessagingTemplate webSocket;
    protected final String CHANNEL;

    public MessageHandler(SimpMessagingTemplate webSocket, String channel) {
        this.webSocket = webSocket;
        this.CHANNEL = channel;
    }

    public abstract void sendMessage(Integer cluedoId, T t, UUID requestingUser);
}
