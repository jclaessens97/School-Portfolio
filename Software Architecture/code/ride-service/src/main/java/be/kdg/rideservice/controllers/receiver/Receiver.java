package be.kdg.rideservice.controllers.receiver;

import org.springframework.amqp.core.Message;

public interface Receiver {
    public void receive(Message msg);
}
