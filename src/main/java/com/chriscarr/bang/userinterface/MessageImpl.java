package com.chriscarr.bang.userinterface;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageImpl implements Message {

    private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
    private String message;
    private final int id;

    public MessageImpl(String info) {
        setMessage(info);
        this.id = NEXT_ID.getAndIncrement();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Message with id '" + id + "': '" + message + "'";
    }
}
