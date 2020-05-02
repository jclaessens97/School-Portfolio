package be.kdg.cluedobackend.controllers.receivers.impl;

import be.kdg.cluedobackend.controllers.receivers.Receiver;
import be.kdg.cluedobackend.dto.messages.UserMessage;
import be.kdg.cluedobackend.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserMessageReceiver implements Receiver {
    private Logger LOGGER = LoggerFactory.getLogger(UserMessageReceiver.class);
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserMessageReceiver(
        UserService userService,
        ObjectMapper objectMapper
    ) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    @RabbitListener(queues = "userQueue")
    public void receive(Message message) {
        try {
            UserMessage userMessage = objectMapper.readValue(message.getBody(), UserMessage.class);

            switch (userMessage.getMessageType()) {
                case CREATE_USER:
                case UPDATE_USER:
                    this.userService.syncUser(userMessage.getUserId(), userMessage.getUsername(), userMessage.getRoles());
                    LOGGER.info("Synced user");
            }
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }
}
