package org.example.messagingapp.service;

import org.example.messagingapp.BaseIntegrationTest;
import org.example.messagingapp.exception.UserNotFoundException;
import org.example.messagingapp.model.Message;
import org.example.messagingapp.model.User;
import org.example.messagingapp.repository.MessageRepository;
import org.example.messagingapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagesReceiveServiceTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessagesReceiveService messagesReceiveService;

    @Autowired
    private MessageRepository messageRepository;

    private User sender1;
    private User sender2;
    private User recipient1;

    @BeforeEach
    public void beforeEach() {
        sender1 = userRepository.save(new User(null, "sender"));
        sender2 = userRepository.save(new User(null, "sender2"));
        recipient1 = userRepository.save(new User(null, "recipient1"));

        Message message1 = new Message(null, sender1.id(), recipient1.id(), "message1");
        Message message2 = new Message(null, sender1.id(), recipient1.id(), "message2");
        Message message3 = new Message(null, sender2.id(), recipient1.id(), "message3");
        Message message4 = new Message(null, recipient1.id(), sender2.id(), "message4");

        List.of(message1, message2, message3, message4).forEach(message ->
                messagesReceiveService.saveMessage(message)
        );
    }

    @Test
    public void shouldSaveMessage() {
        List<Message> messages = messageRepository.findAllBySenderId(sender1.id());
        assertEquals("message1", messages.get(0).message());
    }

    @Test
    public void shouldGetAllReceivedMessages() {
        List<Message> messages = messagesReceiveService.getAllReceivedMessages(recipient1.id());
        assertEquals(3, messages.size());
        assertTrue(messages.stream().map(Message::message).toList().containsAll(List.of("message1", "message2", "message3")));
    }

    @Test
    public void shouldGetAllSentMessages() {
        List<Message> messages = messagesReceiveService.getAllSentMessages(sender1.id());
        assertEquals(2, messages.size());
        assertTrue(messages.stream().map(Message::message).toList().containsAll(List.of("message1", "message2")));
    }

    @Test
    public void shouldThrowExceptionOnGetAllSentMessagesWhenUserDoesNotExist() {
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> messagesReceiveService.getAllSentMessages(UUID.randomUUID())
        );
        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnGetAllReceivedMessagesWhenUserDoesNotExist() {
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> messagesReceiveService.getAllReceivedMessages(UUID.randomUUID())
        );
        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    public void shouldGetAllReceivedMessagesFromSpecificUser() {
        List<Message> messages = messagesReceiveService.getAllReceivedMessagesFromSpecificSender(recipient1.id(), sender2.id());
        assertEquals(1, messages.size());
        assertTrue(messages.stream().map(Message::message).toList().contains("message3"));
    }

    @Test
    public void shouldThrowExceptionOnGetAllReceivedMessagesFromSpecificUserWhenUserDoesNotExist() {
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> messagesReceiveService.getAllReceivedMessagesFromSpecificSender(UUID.randomUUID(), sender1.id())
        );
        assertEquals("User does not exist", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnGetAllReceivedMessagesFromSpecificUserWhenSenderDoesNotExist() {
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> messagesReceiveService.getAllReceivedMessagesFromSpecificSender(recipient1.id(), UUID.randomUUID())
        );
        assertEquals("Sender does not exist", exception.getMessage());
    }
}