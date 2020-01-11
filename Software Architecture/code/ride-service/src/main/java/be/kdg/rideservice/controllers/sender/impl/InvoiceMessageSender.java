package be.kdg.rideservice.controllers.sender.impl;

import be.kdg.rideservice.controllers.sender.MessageSender;
import be.kdg.rideservice.domain.exceptions.ExternalRideServiceException;
import be.kdg.rideservice.dto.messages.impl.InvoiceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMessageSender extends MessageSender<InvoiceMessage> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceMessageSender.class);

    public InvoiceMessageSender() { super("invoiceQueue"); }

    @Override
    @Retryable(value = ExternalRideServiceException.class, maxAttempts = 10)
    public void send(InvoiceMessage message) {
        try {
            super.getRabbitTemplate().convertAndSend(super.getQueueName(), message);
        } catch (AmqpException ex) {
            LOGGER.error(
                String.format("Could not push the message on the %s. Will retry.", super.getQueueName())
            );
            throw new ExternalRideServiceException("Could not push the message on the queue.");
        }
    }
}
