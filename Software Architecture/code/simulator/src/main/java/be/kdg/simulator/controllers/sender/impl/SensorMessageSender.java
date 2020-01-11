package be.kdg.simulator.controllers.sender.impl;

import be.kdg.simulator.controllers.sender.MessageSender;
import be.kdg.simulator.dto.messages.impl.SensorMessage;
import org.springframework.stereotype.Component;

/**
 * Sender implementation to push a message on the Sensor Queue
 */
@Component
public class SensorMessageSender extends MessageSender<SensorMessage> {
    public SensorMessageSender() {
        super("sensorQueue");
    }

    @Override
    public void send(SensorMessage message) {
        super.getTemplate().convertAndSend(super.getQueueName(), message);
    }
}
