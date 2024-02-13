package org.example.messagingapp.configuration;

import java.time.LocalDateTime;

public interface TimestampProvider {
    LocalDateTime getNow();
}
