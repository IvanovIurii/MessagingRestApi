package org.example.messagingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.messagingapp.dto.MessageDto;
import org.example.messagingapp.dto.SendMessageDto;
import org.example.messagingapp.model.Message;
import org.example.messagingapp.service.MessagesReceiveService;
import org.example.messagingapp.service.MessagesSendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class MessagesController {
    private final MessagesSendService messagesSendService;
    private final MessagesReceiveService messagesReceiveService;

    public MessagesController(MessagesSendService messagesSendService, MessagesReceiveService messagesReceiveService) {
        this.messagesSendService = messagesSendService;
        this.messagesReceiveService = messagesReceiveService;
    }

    @Operation(summary = "Send a message from one user to another")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/messages", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageSendResponse> sendMessage(@Valid @RequestBody SendMessageDto sendMessageDto, @RequestHeader("USER_ID") UUID senderId) {
        UUID recipientId = sendMessageDto.getRecipientId();
        if (recipientId.equals(senderId)) {
            throw new IllegalArgumentException("Sender and recipient can not be te same");
        }
        Message message = new Message(senderId, recipientId, sendMessageDto.getMessage());
        messagesSendService.sendMessage(message);

        MessageSendResponse response = new MessageSendResponse("Message was sent to recipient");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all received messages")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/messages/received", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessageDto>> getAllReceivedMessages(@RequestHeader("USER_ID") UUID userId) {
        List<MessageDto> messages = convertMessagesToMessageDtoList(messagesReceiveService.getAllReceivedMessages(userId));
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Get all sent messages")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/messages/sent", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessageDto>> getAllSentMessages(@RequestHeader("USER_ID") UUID userId) {
        List<MessageDto> messages = convertMessagesToMessageDtoList(messagesReceiveService.getAllSentMessages(userId));
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Get all received messages from an exact user")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/messages/received/{senderId}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MessageDto>> getAllReceivedMessagesFromSpecificSender(@PathVariable UUID senderId, @RequestHeader("USER_ID") UUID userId) {
        List<MessageDto> messages = convertMessagesToMessageDtoList(messagesReceiveService.getAllReceivedMessagesFromSpecificSender(userId, senderId));
        return ResponseEntity.ok(messages);
    }

    private List<MessageDto> convertMessagesToMessageDtoList(List<Message> messages) {
        return messages.stream()
                .map(message -> new MessageDto(
                        message.getMessage(),
                        message.getTimestamp().toString())
                )
                .toList();
    }

    private record MessageSendResponse(String statusMessage) {
    }
}
