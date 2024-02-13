package org.example.messagingapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.messagingapp.BaseIntegrationTest;
import org.example.messagingapp.dto.SendMessageDto;
import org.example.messagingapp.model.Message;
import org.example.messagingapp.model.User;
import org.example.messagingapp.repository.MessageRepository;
import org.example.messagingapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
class MessagesControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void shouldThrowExceptionWhenSenderTheSameAsRecipient() throws Exception {
        UUID senderId = UUID.randomUUID();
        SendMessageDto sendMessageDto = new SendMessageDto(senderId, "test");
        String payload = objectMapper.writeValueAsString(sendMessageDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .header("USER_ID", senderId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Bad request")))
                .andExpect(jsonPath("$.error", is("Sender and recipient can not be te same")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    public void shouldThrowValidationErrorWhenNoMessage() throws Exception {
        UUID senderId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        SendMessageDto sendMessageDto = new SendMessageDto(recipientId, " ");
        String payload = objectMapper.writeValueAsString(sendMessageDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .header("USER_ID", senderId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Validation failed")))
                .andExpect(jsonPath("$.error", is("must not be blank")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    public void shouldThrowValidationErrorWhenRecipientIsNotSpecifiedOrInvalid() throws Exception {
        UUID senderId = UUID.randomUUID();
        SendMessageDto sendMessageDto = new SendMessageDto(null, "test");
        String payload = objectMapper.writeValueAsString(sendMessageDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
                        .header("USER_ID", senderId))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Validation failed")))
                .andExpect(jsonPath("$.error", is("must not be null")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    public void shouldGetAllSentMessages() throws Exception {
        User sender = createUser("sender");
        User recipient = createUser("recipient");

        LocalDateTime timestamp1 = LocalDateTime.parse("2024-02-13T14:20:30");
        LocalDateTime timestamp2 = LocalDateTime.parse("2024-02-13T15:30:30");

        Message message1 = createMessage(sender.id(), recipient.id(), "message1", timestamp1);
        Message message2 = createMessage(sender.id(), recipient.id(), "message2", timestamp2);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/sent")
                        .header("USER_ID", sender.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].message", is(message1.getMessage())))
                .andExpect(jsonPath("$[1].message", is(message2.getMessage())))
                .andExpect(jsonPath("$[0].timestamp", is(timestamp1.toString())))
                .andExpect(jsonPath("$[1].timestamp", is(timestamp2.toString())));
    }

    @Test
    public void shouldGetAllReceivedMessages() throws Exception {
        User sender = createUser("sender");
        User recipient = createUser("recipient");

        LocalDateTime timestamp1 = LocalDateTime.parse("2024-02-13T14:20:30");
        LocalDateTime timestamp2 = LocalDateTime.parse("2024-02-13T15:30:30");

        Message message1 = createMessage(sender.id(), recipient.id(), "message1", timestamp1);
        Message message2 = createMessage(sender.id(), recipient.id(), "message2", timestamp2);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/received")
                        .header("USER_ID", recipient.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].message", is(message1.getMessage())))
                .andExpect(jsonPath("$[1].message", is(message2.getMessage())))
                .andExpect(jsonPath("$[0].timestamp", is(timestamp1.toString())))
                .andExpect(jsonPath("$[1].timestamp", is(timestamp2.toString())));
    }

    @Test
    public void shouldGetAllReceivedMessagesFromSpecificSender() throws Exception {
        User sender = createUser("sender");
        User recipient = createUser("recipient");
        User anotherSender = createUser("anotherSender");

        LocalDateTime timestamp1 = LocalDateTime.parse("2024-02-13T14:20:30");
        LocalDateTime timestamp2 = LocalDateTime.parse("2024-02-13T15:30:30");
        LocalDateTime timestamp3 = LocalDateTime.parse("2024-02-13T16:30:30");

        Message message1 = createMessage(sender.id(), recipient.id(), "message1", timestamp1);
        Message message2 = createMessage(sender.id(), recipient.id(), "message2", timestamp2);
        Message message3 = createMessage(anotherSender.id(), recipient.id(), "message3", timestamp3);

        mockMvc.perform(MockMvcRequestBuilders.get("/messages/received/" + anotherSender.id())
                        .header("USER_ID", recipient.id()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is(message3.getMessage())))
                .andExpect(jsonPath("$[0].timestamp", is(timestamp3.toString())));
    }

    private User createUser(String email) {
        return userRepository.save(new User(null, email));
    }

    private Message createMessage(UUID senderId, UUID recipientId, String text, LocalDateTime timestamp) {
        Message message = new Message(senderId, recipientId, text);
        message.setTimestamp(timestamp);

        return messageRepository.save(message);
    }
}