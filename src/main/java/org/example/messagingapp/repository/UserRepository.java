package org.example.messagingapp.repository;

import org.example.messagingapp.model.User;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface UserRepository extends ListCrudRepository<User, UUID> {
}
