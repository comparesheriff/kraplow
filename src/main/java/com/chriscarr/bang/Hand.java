package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.SingleUse;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.IntUnaryOperator;

public class Hand extends ArrayList<Card> {

    private IntUnaryOperator indexPicker = n -> java.util.concurrent.ThreadLocalRandom.current().nextInt(n);

    @Override
    public boolean add(Card card) {
        if (card instanceof SingleUse singleUseCard) {
            singleUseCard.setReadyToPlay(false);
        }
        return super.add(card);
    }

    public int countBangs() {
        int bangs = 0;
        for (Card card : this) {
            if (card.getName().equals(CardName.BANG)) {
                bangs = bangs + 1;
            }
        }
        return bangs;
    }

    public int countMisses() {
        int bangs = 0;
        for (Card card : this) {
            if (card.getName().equals(CardName.MISSED)) {
                bangs = bangs + 1;
            }
        }
        return bangs;
    }

    public Card removeMiss() {
        for (Card card : this) {
            if (card.getName().equals(CardName.MISSED)) {
                remove(card);
                return card;
            }
        }
        return null;
    }

    public Card removeRandom() {
        if (isEmpty()) {
            return null;
        }
        int idx = indexPicker.applyAsInt(size()); // 0..n-1
        return remove(idx);
    }

    public int countBeers() {
        int beers = 0;
        for (Card card : this) {
            if (card.getName().equals(CardName.BEER)) {
                beers = beers + 1;
            }
        }
        return beers;
    }

    public Card removeBeer() {
        for (Card card : this) {
            if (card.getName().equals(CardName.BEER)) {
                remove(card);
                return card;
            }
        }
        return null;
    }

    public void setIndexPicker(IntUnaryOperator picker) {
        this.indexPicker = Objects.requireNonNull(picker);
    }
}
