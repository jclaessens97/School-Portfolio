package be.kdg.cluedoauth.controllers.senders;

import be.kdg.cluedoauth.dto.messages.MessageMarker;


public interface MessageSender<T extends MessageMarker> {
    void send(T message);
}
