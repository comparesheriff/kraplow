package com.chriscarr.game.ajax;

public enum MessageType {
    CHAT, GETCHAT, GETGAMESTATE;

    public static MessageType fromString(String string) {
        try {
            return string == null ? null : MessageType.valueOf(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
