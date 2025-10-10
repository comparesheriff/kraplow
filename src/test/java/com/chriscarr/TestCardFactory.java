package com.chriscarr;

import com.chriscarr.bang.cards.*;

public final class TestCardFactory {
    private TestCardFactory() {}

    public static Card card(CardName name) {
        return new Card(name, CardSuit.SPADES, CardValue.SEVEN, CardType.PLAY);
    }

    public static Card card(CardName name, CardSuit suit) {
        return new Card(name, suit, CardValue.SEVEN, CardType.PLAY);
    }

    public static Card DIAMONDS() {
        return new Card(CardName.VOLCANIC, CardSuit.DIAMONDS, CardValue.TWO, CardType.GUN);
    }

    public static Card HEARTS() {
        return new Card(CardName.VOLCANIC, CardSuit.HEARTS, CardValue.TWO, CardType.GUN);
    }

    public static Card SPADES() {
        return new Card(CardName.VOLCANIC, CardSuit.SPADES, CardValue.TWO, CardType.GUN);
    }

    public static Card CLUBS() {
        return new Card(CardName.VOLCANIC, CardSuit.CLUBS, CardValue.TWO, CardType.GUN);
    }
}
