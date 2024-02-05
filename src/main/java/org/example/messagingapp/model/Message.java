package org.example.messagingapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("messages")
public record Message(@Id UUID id, UUID senderId, UUID recipientId, String message) {
}
