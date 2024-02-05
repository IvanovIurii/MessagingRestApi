package org.example.messagingapp.service;

import org.example.messagingapp.BaseIntegrationTest;
import org.example.messagingapp.exception.DuplicateEmailException;
import org.example.messagingapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    public void shouldCreateUser() {
        User user = new User(null, "test");
        userService.createUser(user);

        List<User> users = userService.getAllUsers();
        assertEquals("test", users.get(0).email());
    }

    @Test
    public void shouldGetAllUsers() {
        User testUser1 = new User(null, "test1");
        User testUser2 = new User(null, "test2");
        userService.createUser(testUser1);
        userService.createUser(testUser2);

        List<User> allUsers = userService.getAllUsers();
        assertEquals(2, allUsers.size());

        User user1 = userService.getAllUsers().get(0);
        User user2 = userService.getAllUsers().get(1);

        assertEquals("test1", user1.email());
        assertEquals("test2", user2.email());
    }

    @Test
    public void shouldRaiseDuplicateExceptionOnDuplicateEmail() {
        User user = new User(null, "test");
        userService.createUser(user);

        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class,
                () -> userService.createUser(user)
        );
        assertEquals("This email already exist: test", exception.getMessage());
    }
}