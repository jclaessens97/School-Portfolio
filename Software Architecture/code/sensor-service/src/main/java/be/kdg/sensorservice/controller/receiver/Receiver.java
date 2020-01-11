package be.kdg.sensorservice.controller.receiver;

import org.springframework.amqp.core.Message;

/**
 * Receives messages from a queue specified in the implementation
 */
public interface Receiver {
    public void receive(Message msg);
}
