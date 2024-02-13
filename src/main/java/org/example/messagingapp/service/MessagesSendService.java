package org.example.messagingapp.service;

import org.example.messagingapp.configuration.TimestampProvider;
import org.example.messagingapp.exception.UserNotFoundException;
import org.example.messagingapp.messaging.MessagesPublisher;
import org.example.messagingapp.model.Message;
import org.example.messagingapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MessagesSendService {
    private final MessagesPublisher messagesPublisher;
    private final UserRepository userRepository;
    private final TimestampProvider timestampProvider;

    public MessagesSendService(
            MessagesPublisher messagesPublisher,
            UserRepository userRepository,
            TimestampProvider timestampProvider
    ) {
        this.messagesPublisher = messagesPublisher;
        this.userRepository = userRepository;
        this.timestampProvider = timestampProvider;
    }

    public void sendMessage(Message message) {
        userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new UserNotFoundException("Sender does not exist"));

        userRepository.findById(message.getRecipientId())
                .orElseThrow(() -> new UserNotFoundException("Recipient does not exist"));

        message.setTimestamp(timestampProvider.getNow());
        messagesPublisher.publishMessage(message);
    }
}
