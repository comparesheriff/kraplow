package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

public class Deck extends ArrayList<Card> {
    private Discard discard;
    private Random rng = new Random();

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
        Collections.shuffle(this, rng);
    }

    public void setDiscard(Discard discard) {
        this.discard = discard;
    }

    public void setRng(Random rng) {
        this.rng = Objects.requireNonNull(rng);
    }
}
