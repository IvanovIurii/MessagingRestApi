package org.example.messagingapp.controller;

import org.example.messagingapp.BaseIntegrationTest;
import org.example.messagingapp.model.User;
import org.example.messagingapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
class UsersControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldCreateUser() throws Exception {
        String jsonString = "{\"email\":\"test@example.com\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(jsonPath("$.email", is("test@example.com")));

        List<User> users = userRepository.findAll();
        assertEquals("test@example.com", users.get(0).email());
    }

    @Test
    public void shouldThrowErrorOnInvalidEmail() throws Exception {
        String jsonString = "{\"email\":\"test\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Validation failed")))
                .andExpect(jsonPath("$.error", is("must be a well-formed email address")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        User user = new User(null, "test@example.com");
        userRepository.save(user);

        UUID id = userRepository.findAll().get(0).id();

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id.toString())))
                .andExpect(jsonPath("$[0].email", is("test@example.com")));
    }
}