package be.kdg.cluedobackend.controllers.receivers;

import org.springframework.amqp.core.Message;

public interface Receiver {
    void receive(Message message);
}
