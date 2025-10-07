package com.chriscarr;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import junit.framework.TestCase;

import static org.junit.Assert.assertThrows;

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

    public void testRemoveRandom_empty_returnsNull() {
        assertNull(new Hand().removeRandom());
    }

    public void testRemoveRandom_doesNotChangeSize_whenEmpty() {
        Hand h = new Hand();
        int before = h.size();
        h.removeRandom();
        assertEquals(before, h.size());
    }

    public void testRemoveRandom_isDeterministic_withPicker() {
        Hand h = new Hand();
        h.add(TestCardFactory.CLUBS()); h.add(TestCardFactory.DIAMONDS()); h.add(TestCardFactory.HEARTS());
        h.setIndexPicker(n -> 1);                  // immer Index 1
        Card removed = h.removeRandom();
        assertEquals(CardName.VOLCANIC, removed.getName());
        assertEquals(2, h.size());
    }
}
