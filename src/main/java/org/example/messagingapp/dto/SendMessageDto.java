package org.example.messagingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SendMessageDto {
    @NotNull
    private UUID recipientId;
    @NotBlank
    private String message;

    public SendMessageDto() {
    }

    public SendMessageDto(UUID recipientUuid, String message) {
        this.recipientId = recipientUuid;
        this.message = message;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
