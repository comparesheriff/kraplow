package com.chriscarr.game;

import java.time.Instant;

public class ChatMessage {
    private final String message;
    private final Instant timestamp = Instant.now();

    public ChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
