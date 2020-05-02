package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.model.chat.Message;
import be.kdg.cluedobackend.model.users.Player;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    List<Message> getMessages(Integer cluedoId);
    void sendMessage(Integer cluedoId,String msg, Integer playerId);
    void saveMessage(Message message, Player player);
    void sendSystemMessage(Integer cluedoId, UUID userId, String action);
}
