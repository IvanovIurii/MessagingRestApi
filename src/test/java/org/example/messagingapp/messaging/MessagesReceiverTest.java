package org.example.messagingapp.messaging;

import org.example.messagingapp.model.Message;
import org.example.messagingapp.service.MessagesReceiveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class MessagesReceiverTest {

    private MessagesReceiver sut;

    @Mock
    private MessagesReceiveService messagesReceiveServiceMock;

    @BeforeEach
    public void setUp() {
        sut = new MessagesReceiver(messagesReceiveServiceMock);
    }

    @Test
    public void shouldSaveMessageOnReceive() {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        Message message = new Message(null, senderId, recipientId, "test");

        sut.onMessageReceived(message);

        verify(messagesReceiveServiceMock).saveMessage(message);
        verifyNoMoreInteractions(messagesReceiveServiceMock);
    }
}