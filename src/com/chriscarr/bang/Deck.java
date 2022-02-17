package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private final List<Card> cards = new ArrayList<>();
    private DiscardPile discardPile;

    public void add(Card card) {
        cards.add(card);
    }

    public Card pull() {
        if (cards.size() == 0) {
            while (discardPile.canDrawFromDiscard()) {
                cards.add(discardPile.remove());
            }
            shuffle();
        }
        return cards.remove(cards.size() - 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void setDiscard(DiscardPile discardPile) {
        this.discardPile = discardPile;
    }

    public int size() {
        return cards.size();
    }

}
