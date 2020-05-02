package be.kdg.cluedobackend.controllers.messagehandlers;

import be.kdg.cluedobackend.dto.MessageDto;
import be.kdg.cluedobackend.model.chat.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatMessageHandler extends MessageHandler<Message> {
    @Autowired
    public ChatMessageHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/chat/");
    }

    @Override
    public void sendMessage(Integer cluedoId, Message message, UUID requestingUser) {
        MessageDto messageDto = new MessageDto(message);
        webSocket.convertAndSend(CHANNEL + cluedoId, messageDto);
    }
}
