package com.chriscarr.bang.models.servlet.json;

public class MessageResponseModel extends JsonResponseModel {
    private Integer id;
    private String text;
    private HandResponseModel hand;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HandResponseModel getHand() {
        return hand;
    }

    public void setHand(HandResponseModel hand) {
        this.hand = hand;
    }
}
