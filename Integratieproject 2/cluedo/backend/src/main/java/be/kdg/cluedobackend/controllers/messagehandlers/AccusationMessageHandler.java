package be.kdg.cluedobackend.controllers.messagehandlers;

import be.kdg.cluedobackend.dto.AccusationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccusationMessageHandler extends MessageHandler<AccusationDto> {
    @Autowired
    public AccusationMessageHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/accusation/");
    }

    @Override
    public void sendMessage(Integer cluedoId, AccusationDto accusationDto, UUID requestingUser) {
        webSocket.convertAndSend(CHANNEL + cluedoId, accusationDto);
    }
}
