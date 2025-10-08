package com.chriscarr.game.ajax;

public enum MessageType {
    CHAT,
    GETCHAT,
    GETGAMESTATE,
    JOIN,
    JOINAI,
    LEAVE,
    AVAILABLEGAMES,
    COUNTPLAYERS,
    GETGUESTCOUNTER,
    CANSTART;

    public static MessageType fromString(String string) {
        try {
            return string == null ? null : MessageType.valueOf(string);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
