package be.kdg.cluedobackend.controllers.messagehandlers;

import be.kdg.cluedobackend.dto.InviteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InviteMessageHandler extends MessageHandler<String> {
    @Autowired
    public InviteMessageHandler(SimpMessagingTemplate webSocket) {
        super(webSocket, "/invite/");
    }

    public void sendMessage(Integer cluedoId, String invitedUser, String inviter){
        InviteDto inviteDto = new InviteDto(cluedoId,invitedUser,inviter);
        webSocket.convertAndSend(CHANNEL + invitedUser, inviteDto);
    }

    @Override
    public void sendMessage(Integer cluedoId, String userName, UUID requestingUser) {
//        InviteDto inviteDto = new InviteDto(cluedoId, userName);
//        webSocket.convertAndSend(CHANNEL + requestingUser.toString(), inviteDto);
    }
}
