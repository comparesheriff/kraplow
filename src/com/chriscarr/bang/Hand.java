package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.SingleUse;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    List<Object> cards = new ArrayList<>();

    public void add(Object object) {
        if (object instanceof SingleUse) {
            SingleUse card = (SingleUse) object;
            card.setReadyToPlay(false);
        }
        cards.add(object);
    }

    public Object get(int i) {
        return cards.get(i);
    }

    public int size() {
        return cards.size();
    }

    public Object remove(int card) {
        return cards.remove(card);
    }

    public int countBangs() {
        int bangs = 0;
        for (Object card : cards) {
            if (((Card) card).getName().equals(Card.CARDBANG)) {
                bangs = bangs + 1;
            }
        }
        return bangs;
    }

    public int countMisses() {
        int bangs = 0;
        for (Object card : cards) {
            if (((Card) card).getName().equals(Card.CARDMISSED)) {
                bangs = bangs + 1;
            }
        }
        return bangs;
    }

    public Object removeMiss() {
        for (Object card : cards) {
            if (((Card) card).getName().equals(Card.CARDMISSED)) {
                cards.remove(card);
                return card;
            }
        }
        return null;
    }

    public Object removeRandom() {
        return cards.remove((int) (Math.random() * cards.size()));
    }

    public int countBeers() {
        int beers = 0;
        for (Object card : cards) {
            if (((Card) card).getName().equals(Card.CARDBEER)) {
                beers = beers + 1;
            }
        }
        return beers;
    }

    public Object removeBeer() {
        for (Object card : cards) {
            if (((Card) card).getName().equals(Card.CARDBEER)) {
                cards.remove(card);
                return card;
            }
        }
        return null;
    }

    public void remove(Object card) {
        cards.remove(card);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

}
