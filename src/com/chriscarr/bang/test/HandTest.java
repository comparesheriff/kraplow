package com.chriscarr.bang.test;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.cards.Card;
import junit.framework.TestCase;

public class HandTest extends TestCase {
    public void testHandAddCard() {
        Hand hand = new Hand();
        Card card = new Card();
        hand.add(card);
        Card gotCard = hand.get(0);
        assertEquals(card, gotCard);
    }

    public void testHandSize() {
        Hand hand = new Hand();
        assertEquals(hand.size(), 0);
    }

    public void testHandSizeOne() {
        Hand hand = new Hand();
        hand.add(new Card());
        assertEquals(hand.size(), 1);
    }
}
