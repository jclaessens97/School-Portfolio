package be.kdg.cluedoauth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqConfig.class);

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        declareQueuesIfNotExists(connectionFactory);
        return rabbitTemplate;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    private void declareQueuesIfNotExists(ConnectionFactory connectionFactory) {
        final String[] queueNames = {"userQueue"};
        final AmqpAdmin admin = amqpAdmin(connectionFactory);

        for (String queueName : queueNames) {
            var props = admin.getQueueProperties(queueName);
            if (props == null) {
                admin.declareQueue(
                    new Queue(queueName)
                );

                LOGGER.info(String.format("Queue '%s' created...", queueName));
            }
        }
    }
}
