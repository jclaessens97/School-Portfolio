package be.kdg.cluedoauth.controllers.senders.impl;

import be.kdg.cluedoauth.controllers.senders.MessageSender;
import be.kdg.cluedoauth.dto.messages.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMessageSender implements MessageSender<UserMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserMessageSender.class);
    private final RabbitTemplate template;

    @Autowired
    public UserMessageSender(RabbitTemplate template) {
        this.template = template;
    }

    @Override
    public void send(UserMessage message) {
        template.convertAndSend("userQueue", message);
        LOGGER.info(
            String.format("Message of type %s with id %s and username %s pushed on userQueue",
                message.getMessageType(),
                message.getUserId(),
                message.getUsername()
            )
        );
    }
}
