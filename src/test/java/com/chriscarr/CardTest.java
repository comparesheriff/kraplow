package com.chriscarr;

import com.chriscarr.bang.cards.*;
import junit.framework.TestCase;

public class CardTest extends TestCase {
    public void testSetName() {
        Card card = new Card();
        CardName setName = CardName.JAIL;
        card.setName(setName);
        CardName gotName = card.getName();
        assertEquals(setName, gotName);
    }

    public void testSetOtherName() {
        Card card = new Card();
        CardName setName = CardName.VOLCANIC;
        card.setName(setName);
        CardName gotName = card.getName();
        assertEquals(setName, gotName);
    }

    public void testSetGetSuit() {
        Card card = new Card();
        CardSuit setSuit = CardSuit.HEARTS;
        card.setSuit(setSuit);
        CardSuit gotSuit = card.getSuit();
        assertEquals(setSuit, gotSuit);
    }

    public void testSetGetSuitClubs() {
        Card card = new Card();
        CardSuit setSuit = CardSuit.CLUBS;
        card.setSuit(setSuit);
        CardSuit gotSuit = card.getSuit();
        assertEquals(setSuit, gotSuit);
    }

    public void testSetGetSuitSpades() {
        Card card = new Card();
        CardSuit setSuit = CardSuit.SPADES;
        card.setSuit(setSuit);
        CardSuit gotSuit = card.getSuit();
        assertEquals(setSuit, gotSuit);
    }

    public void testSetGetSuitDiamonds() {
        Card card = new Card();
        CardSuit setSuit = CardSuit.DIAMONDS;
        card.setSuit(setSuit);
        CardSuit gotSuit = card.getSuit();
        assertEquals(setSuit, gotSuit);
    }

    public void testSetGetValue() {
        Card card = new Card();
        card.setValue(CardValue.TWO);
        card.setValue(CardValue.THREE);
        card.setValue(CardValue.FOUR);
        card.setValue(CardValue.FIVE);
        card.setValue(CardValue.SIX);
        card.setValue(CardValue.SEVEN);
        card.setValue(CardValue.EIGHT);
        card.setValue(CardValue.NINE);
        card.setValue(CardValue.TEN);
        card.setValue(CardValue.JACK);
        card.setValue(CardValue.QUEEN);
        card.setValue(CardValue.KING);
        card.setValue(CardValue.ACE);
        CardValue valueGot = card.getValue();
        assertEquals(CardValue.ACE, valueGot);
    }

    public void testSetCardType() {
        Card card = new Card();
        card.setType(CardType.GUN);
        card.setType(CardType.ITEM);
        card.setType(CardType.PLAY);
        CardType gotType = card.getType();
        assertEquals(CardType.PLAY, gotType);
    }

    public void testGunRange() {
        assertEquals(1, new Gun(CardName.VOLCANIC, CardSuit.SPADES, CardValue.TWO, CardType.GUN).getRange());
        assertEquals(2, new Gun(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.TWO, CardType.GUN).getRange());
        assertEquals(3, new Gun(CardName.REMINGTON, CardSuit.SPADES, CardValue.TWO, CardType.GUN).getRange());
        assertEquals(4, new Gun(CardName.WINCHESTER, CardSuit.SPADES, CardValue.TWO, CardType.GUN).getRange());
        assertEquals(5,  new Gun(CardName.REV_CARBINE, CardSuit.SPADES, CardValue.TWO, CardType.GUN).getRange());
    }

    public void testGunMultiBang() {
        assertTrue(Card.multiBang(CardName.VOLCANIC));
        assertFalse(Card.multiBang(CardName.SCHOFIELD));
    }
}
