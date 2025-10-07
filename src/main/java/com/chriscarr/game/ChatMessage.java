package com.chriscarr.game;

import java.time.Instant;
import java.util.Date;

public class ChatMessage {
    String message;
    Instant timestamp;

    public ChatMessage(String message) {
        this.message = message;
        timestamp = Instant.now();
    }
}
