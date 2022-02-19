package com.chriscarr.bang.models.servlet.json;


public class ChatMessageResponseModel extends JsonResponseModel {
    private final String chat;
    private final String timestamp;

    public ChatMessageResponseModel(String chat, String timestamp) {
        this.chat = chat;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getChat() {
        return chat;
    }
}
