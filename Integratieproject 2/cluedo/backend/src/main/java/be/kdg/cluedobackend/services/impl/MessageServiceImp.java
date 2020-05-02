package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.controllers.messagehandlers.MessageHandler;
import be.kdg.cluedobackend.model.chat.Message;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.CluedoRepository;
import be.kdg.cluedobackend.repository.MessageRepository;
import be.kdg.cluedobackend.repository.PlayerRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import be.kdg.cluedobackend.services.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MessageServiceImp implements MessageService {
    private final CluedoRepository cluedoRepository;
    private final MessageRepository messageRepository;
    private final PlayerRepository playerRepository;
    private final MessageHandler<Message> chatMessageHandler;
    private final UserRepository userRepository;

    public MessageServiceImp(CluedoRepository cluedoRepository, MessageRepository messageRepository, PlayerRepository playerRepository, MessageHandler<Message> chatMessageHandler, UserRepository userRepository) {
        this.cluedoRepository = cluedoRepository;
        this.messageRepository = messageRepository;
        this.playerRepository = playerRepository;
        this.chatMessageHandler = chatMessageHandler;
        this.userRepository = userRepository;
    }

    @Override
    public List<Message> getMessages(Integer cluedoId) {
        Cluedo cluedo = cluedoRepository.findById(cluedoId).get();
        return cluedo.getMessages();
    }

    @Override
    public void sendMessage(Integer cluedoId,String msg, Integer playerId) {
        Player player = playerRepository.findById(playerId).get();
        User user = player.getUser();
        Date now = new Date();
        Message message = new Message(msg,user,now);
        saveMessage(message,player);
        chatMessageHandler.sendMessage(cluedoId, message, null);
    }
    @Override
    public void sendSystemMessage(Integer cluedoId, UUID userId, String action){
        User user = userRepository.findById(userId).get();
        Message message = new Message( String.format("%s has %s the lobby",user.getUserName(),action));
        chatMessageHandler.sendMessage(cluedoId, message, null);
    }

    @Override
    public void saveMessage(Message message, Player player){
        player.getCluedo().getMessages().add(message);
        messageRepository.save(message);
    }

}
