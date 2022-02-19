package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;
import java.util.List;

public class ChatsResponseModel extends JsonResponseModel {
    @JsonUnwrapped
    private List<ChatMessageResponseModel> chatMessages;
    @JsonProperty("session")
    private List<String> sessions;

    public List<ChatMessageResponseModel> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessageResponseModel> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public List<String> getSessions() {
        return sessions;
    }

    public void setSessions(List<String> sessions) {
        this.sessions = sessions;
    }

    public void addSession(String sessionName){
        if (sessions == null) {
            sessions = new ArrayList<>();
        }
        sessions.add(sessionName);
    }

    public void addChatMessage(ChatMessageResponseModel chatMessageResponseModel) {
        if (chatMessages == null) {
            chatMessages = new ArrayList<>();
        }
        chatMessages.add(chatMessageResponseModel);
    }
}
