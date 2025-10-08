package com.chriscarr.game.ajax;

public enum MessageType {
    CHAT, GETCHAT;

    public static MessageType fromString(String string) {
        try {
            return string == null ? null : MessageType.valueOf(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
