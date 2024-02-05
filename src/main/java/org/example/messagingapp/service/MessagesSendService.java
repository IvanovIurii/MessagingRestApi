package org.example.messagingapp.service;

import org.example.messagingapp.exception.UserNotFoundException;
import org.example.messagingapp.messaging.MessagesPublisher;
import org.example.messagingapp.model.Message;
import org.example.messagingapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MessagesSendService {
    private final MessagesPublisher messagesPublisher;
    private final UserRepository userRepository;

    public MessagesSendService(MessagesPublisher messagesPublisher, UserRepository userRepository) {
        this.messagesPublisher = messagesPublisher;
        this.userRepository = userRepository;
    }

    public void sendMessage(Message message) {
        userRepository.findById(message.senderId())
                .orElseThrow(() -> new UserNotFoundException("Sender does not exist"));

        userRepository.findById(message.recipientId())
                .orElseThrow(() -> new UserNotFoundException("Recipient does not exist"));

        messagesPublisher.publishMessage(message);
    }
}
