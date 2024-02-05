package org.example.messagingapp.controller;

import jakarta.validation.Valid;
import org.example.messagingapp.dto.CreateUserDto;
import org.example.messagingapp.model.User;
import org.example.messagingapp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/users", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> getUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/users", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        User user = new User(null, createUserDto.getEmail());
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
