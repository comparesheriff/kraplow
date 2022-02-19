package com.chriscarr.bang.models.servlet.json;


import java.util.ArrayList;
import java.util.List;

public class HandResponseModel extends JsonResponseModel {
    private List<String> card;

    public List<String> getCard() {
        return card;
    }

    public void setCard(List<String> card) {
        this.card = card;
    }

    public void addCard(String cardName){
        if(card == null){
            card = new ArrayList<>();
        }
        card.add(cardName);
    }
}
