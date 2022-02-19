package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;

import java.util.ArrayList;
import java.util.List;

public class DiscardPile {

    List<Card> cards = new ArrayList<>();

    public void add(Card object) {
        cards.add(object);
    }

    public Card peek() {
        return cards.get(cards.size() - 1);
    }

    public Card remove() {
        return cards.remove(cards.size() - 1);
    }

    public boolean canDrawFromDiscard() {
        return !cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

}
