package org.example.messagingapp.messaging;

import org.example.messagingapp.BaseIntegrationTest;
import org.example.messagingapp.model.Message;
import org.example.messagingapp.model.User;
import org.example.messagingapp.repository.MessageRepository;
import org.example.messagingapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;
import static org.example.messagingapp.configuration.RabbitMqProperties.DEAD_LETTER_QUEUE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesIntegrationTest extends BaseIntegrationTest {

    private final static LocalDateTime FIXED_TIMESTAMP = LocalDateTime.parse("2024-02-13T14:20:30");

    private static final int TIMEOUT = 3000;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessagesPublisher messagesPublisher;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    @Test
    public void shouldPublishAndReceiveMessage() {
        UUID senderId = userService.createUser(new User(null, "sender")).id();
        UUID recipientId = userService.createUser(new User(null, "recipient")).id();

        Message message = new Message(senderId, recipientId, "test");
        message.setTimestamp(FIXED_TIMESTAMP);

        messagesPublisher.publishMessage(message);

        await().atMost(TIMEOUT, TimeUnit.MILLISECONDS)
                .until(() -> messageRepository.findAllBySenderId(senderId).size() == 1);

        message = messageRepository.findAllBySenderId(senderId).get(0);
        assertEquals(recipientId, message.getRecipientId());
        assertEquals("test", message.getMessage());
    }

    @Test
    public void shouldPublishInvalidMessageToDLQ() {
        Message test = new Message(null, null, "test");
        messagesPublisher.publishMessage(test);

        AtomicReference<Message> messageAtomicReference = new AtomicReference<>();
        await().atMost(TIMEOUT, TimeUnit.MILLISECONDS).until(() -> {
                    messageAtomicReference.set((Message) rabbitTemplate.receiveAndConvert(DEAD_LETTER_QUEUE_NAME));
                    return messageAtomicReference.get() != null;
                }
        );

        Message message = messageAtomicReference.get();
        assertEquals("test", message.getMessage());
    }
}