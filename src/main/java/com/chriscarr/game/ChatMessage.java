package com.chriscarr.game;

import java.time.Instant;

public class ChatMessage {
  String message;
  Instant timestamp;

  public ChatMessage(String message) {
    this.message = message;
    timestamp = Instant.now();
  }
}
