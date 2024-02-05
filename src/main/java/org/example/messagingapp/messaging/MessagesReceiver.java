package org.example.messagingapp.messaging;

import org.example.messagingapp.model.Message;
import org.example.messagingapp.service.MessagesReceiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static org.example.messagingapp.configuration.RabbitMqProperties.QUEUE_NAME;

@Component
public class MessagesReceiver {
    private static final Logger logger = LoggerFactory.getLogger(MessagesReceiver.class);

    private final MessagesReceiveService messagesReceiveService;

    public MessagesReceiver(MessagesReceiveService messagesReceiveService) {
        this.messagesReceiveService = messagesReceiveService;
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void onMessageReceived(Message message) {
        logger.info("Message received");
        messagesReceiveService.saveMessage(message);
    }
}
