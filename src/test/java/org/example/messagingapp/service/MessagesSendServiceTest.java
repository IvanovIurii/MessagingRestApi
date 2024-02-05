package org.example.messagingapp.service;

import org.example.messagingapp.exception.UserNotFoundException;
import org.example.messagingapp.messaging.MessagesPublisher;
import org.example.messagingapp.model.Message;
import org.example.messagingapp.model.User;
import org.example.messagingapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessagesSendServiceTest {

    private MessagesSendService sut;

    @Mock
    private MessagesPublisher messagesPublisherMock;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        sut = new MessagesSendService(messagesPublisherMock, userRepository);
    }

    @Test
    public void shouldSendMessage() {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();

        when(userRepository.findById(senderId)).thenReturn(Optional.of(mock(User.class)));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(mock(User.class)));

        Message message = new Message(
                null,
                senderId,
                recipientId,
                "test"
        );
        sut.sendMessage(message);

        verify(messagesPublisherMock).publishMessage(message);
        verifyNoMoreInteractions(messagesPublisherMock);
    }

    @Test
    public void shouldThrowSenderNotFoundException() {
        Message message = mock(Message.class);
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.sendMessage(message)
        );
        assertEquals("Sender does not exist", exception.getMessage());
    }

    @Test
    public void shouldThrowRecipientNotFoundException() {
        Message message = mock(Message.class);
        UUID senderId = UUID.randomUUID();
        when(message.senderId()).thenReturn(senderId);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(mock(User.class)));

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> sut.sendMessage(message)
        );
        assertEquals("Recipient does not exist", exception.getMessage());
    }
}