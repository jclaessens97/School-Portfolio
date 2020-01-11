package be.kdg.simulator.controllers.sender;

import be.kdg.simulator.dto.messages.Message;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class to define a MessageSender of type T
 * @param <T> Any type of Message
 */
public abstract class MessageSender<T extends Message> {
    @Getter(AccessLevel.PROTECTED)
    private final String queueName;

    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private RabbitTemplate template;

    protected MessageSender(String queueName) {
        this.queueName = queueName;
    }

    public abstract void send(T message);
}
