package be.kdg.rideservice.controllers.sender;

import be.kdg.rideservice.dto.messages.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class to define a MessageSender of type T
 * @param <T> Any type of Message
 */
public abstract class MessageSender<T extends Message> {
    private final String queueName;

    @Autowired
    private RabbitTemplate template;

    protected MessageSender(String queueName) {
        this.queueName = queueName;
    }

    public abstract void send(T message);

    protected String getQueueName() {
        return this.queueName;
    }

    protected RabbitTemplate getRabbitTemplate() {
        return this.template;
    }
}