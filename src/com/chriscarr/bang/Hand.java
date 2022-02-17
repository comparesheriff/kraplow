package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.SingleUse;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    List<Card> cards = new ArrayList<>();

    public void add(Card object) {
        if (object instanceof SingleUse) {
            SingleUse card = (SingleUse) object;
            card.setReadyToPlay(false);
        }
        cards.add(object);
    }

    public Card get(int i) {
        return cards.get(i);
    }

    public int size() {
        return cards.size();
    }

    public Card remove(int card) {
        return cards.remove(card);
    }

    public int countBangs() {
        int bangs = 0;
        for (Card card : cards) {
            if (Card.CARDBANG.equals(card.getName())) {
                bangs = bangs + 1;
            }
        }
        return bangs;
    }

    public int countMisses() {
        int bangs = 0;
        for (Card card : cards) {
            if (Card.CARDMISSED.equals(card.getName())) {
                bangs = bangs + 1;
            }
        }
        return bangs;
    }

    public Card removeMiss() { //refactor
        for (Card card : cards) {
            if (Card.CARDMISSED.equals(card.getName())) {
                cards.remove(card);
                return card;
            }
        }
        return null;
    }

    public Card removeRandom() {
        return cards.remove((int) (Math.random() * cards.size()));
    }

    public int countBeers() {
        int beers = 0;
        for (Card card : cards) {
            if (Card.CARDBEER.equals(card.getName())) {
                beers = beers + 1;
            }
        }
        return beers;
    }

    public Card removeBeer() { //refactor
        for (Card card : cards) {
            if (Card.CARDBEER.equals(card.getName())) {
                cards.remove(card);
                return card;
            }
        }
        return null;
    }

    public void remove(Card card) {
        cards.remove(card);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

}
