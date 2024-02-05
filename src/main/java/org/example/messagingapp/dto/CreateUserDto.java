package org.example.messagingapp.dto;

import jakarta.validation.constraints.Email;

public class CreateUserDto {
    @Email
    private String email;

    public CreateUserDto() {
    }

    public CreateUserDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
