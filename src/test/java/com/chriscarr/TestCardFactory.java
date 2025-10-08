package com.chriscarr;

import com.chriscarr.bang.cards.*;

public class TestCardFactory {

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
