package org.example.messagingapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("users")
public record User(@Id UUID id, String email) {
}
