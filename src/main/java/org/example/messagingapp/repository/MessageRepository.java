package org.example.messagingapp.repository;

import org.example.messagingapp.model.Message;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends ListCrudRepository<Message, UUID> {
    List<Message> findAllBySenderId(UUID senderId);

    List<Message> findAllByRecipientId(UUID recipientId);

    List<Message> findAllByRecipientIdAndSenderId(UUID recipientId, UUID senderId);
}
