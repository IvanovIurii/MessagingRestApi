package org.example.messagingapp.service;

import org.example.messagingapp.exception.DuplicateEmailException;
import org.example.messagingapp.model.User;
import org.example.messagingapp.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception ex) {
            if (isDataIntegrityViolation(ex, "users_email_key")) {
                throw new DuplicateEmailException("This email already exist: " + user.email());
            }
            throw ex;
        }
    }

    private boolean isDataIntegrityViolation(Throwable throwable, String key) {
        Throwable cause = throwable.getCause();
        String message = String.format("duplicate key value violates unique constraint \"%s\"", key);
        return cause instanceof DuplicateKeyException
                && cause.getMessage().contains(message);
    }

}
