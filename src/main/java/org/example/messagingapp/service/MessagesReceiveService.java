package org.example.messagingapp.service;

import org.example.messagingapp.exception.UserNotFoundException;
import org.example.messagingapp.model.Message;
import org.example.messagingapp.repository.MessageRepository;
import org.example.messagingapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MessagesReceiveService {
    private static final Logger logger = LoggerFactory.getLogger(MessagesReceiveService.class);

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MessagesReceiveService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveMessage(Message message) {
        messageRepository.save(message);
        logger.info("Message saved to DB");
    }

    public List<Message> getAllReceivedMessages(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        return messageRepository.findAllByRecipientId(userId);
    }

    public List<Message> getAllSentMessages(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        return messageRepository.findAllBySenderId(userId);
    }

    public List<Message> getAllReceivedMessagesFromSpecificSender(UUID userId, UUID fromUserId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User does not exist"));

        userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException("Sender does not exist"));

        return messageRepository.findAllByRecipientIdAndSenderId(userId, fromUserId);
    }
}
