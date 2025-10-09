package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.infra.Rng;

import java.util.ArrayList;

public class Deck extends ArrayList<Card> {
    private Discard discard;

    public Card pull() {
        if (isEmpty()) {
            while (!discard.isEmpty()) {
                add(discard.removeLast());
            }
            shuffle();
        }
        return removeLast();
    }

    public void shuffle() {
        Rng.shuffle(this);
    }

    public void setDiscard(Discard discard) {
        this.discard = discard;
    }

}
