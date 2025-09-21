package com.chriscarr;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.cards.Card;
import junit.framework.TestCase;

public class HandTest extends TestCase {
    public void testHandAddCard() {
        Hand hand = new Hand();
        Card card = new Card();
        hand.add(card);
        Card gotCard = hand.getFirst();
        assertEquals(card, gotCard);
    }

    public void testHandSize() {
        assertEquals(0, new Hand().size());
    }

    public void testHandSizeOne() {
        Hand hand = new Hand();
        hand.add(new Card());
        assertEquals(1, hand.size());
    }
}
