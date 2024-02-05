package org.example.messagingapp.messaging;

import org.example.messagingapp.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class MessagesPublisherTest {

    private MessagesPublisher sut;

    @Mock
    private RabbitTemplate rabbitTemplateMock;

    @BeforeEach
    public void setUp() {
        sut = new MessagesPublisher(rabbitTemplateMock);
    }

    @Test
    public void shouldPublishMessage() {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        Message message = new Message(null, senderId, recipientId, "test");

        sut.publishMessage(message);

        verify(rabbitTemplateMock).convertAndSend(
                "messaging.application.exchange",
                "messaging.application.queue",
                message
        );
        verifyNoMoreInteractions(rabbitTemplateMock);
    }
}