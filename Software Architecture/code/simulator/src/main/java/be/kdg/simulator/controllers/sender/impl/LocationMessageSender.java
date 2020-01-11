package be.kdg.simulator.controllers.sender.impl;

import be.kdg.simulator.controllers.sender.MessageSender;
import be.kdg.simulator.dto.messages.impl.LocationMessage;
import org.springframework.stereotype.Component;

@Component
public class LocationMessageSender extends MessageSender<LocationMessage> {
    public LocationMessageSender() {
        super("locationQueue");
    }

    @Override
    public void send(LocationMessage message) {
        super.getTemplate().convertAndSend(super.getQueueName(), message);
    }
}
