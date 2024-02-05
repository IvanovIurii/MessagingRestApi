package org.example.messagingapp.messaging;

import org.example.messagingapp.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static org.example.messagingapp.configuration.RabbitMqProperties.EXCHANGE_NAME;
import static org.example.messagingapp.configuration.RabbitMqProperties.QUEUE_NAME;

@Component
public class MessagesPublisher {
    private static final Logger logger = LoggerFactory.getLogger(MessagesPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public MessagesPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishMessage(Message message) {
        logger.info("Publishing message");
        rabbitTemplate.convertAndSend(
                EXCHANGE_NAME,
                QUEUE_NAME,
                message
        );
        logger.info("Publishing published");
    }
}
