package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
