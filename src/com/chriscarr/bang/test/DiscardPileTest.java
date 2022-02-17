package com.chriscarr.bang.test;

import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.cards.Card;
import junit.framework.TestCase;

public class DiscardPileTest extends TestCase {
    public void testDiscardCard() {
        DiscardPile discardPile = new DiscardPile();
        Card toAdd = new Card();
        discardPile.add(toAdd);
        Card peeked = discardPile.peek();
        assertEquals(toAdd, peeked);
    }

    public void testRemove() {
        DiscardPile discardPile = new DiscardPile();
        Card toAdd = new Card();
        discardPile.add(toAdd);
        Card removed = discardPile.remove();
        assertEquals(toAdd, removed);
    }

    public void testRemoveTwo() {
        DiscardPile discardPile = new DiscardPile();
        Card toAdd1 = new Card();
        Card toAdd2 = new Card();
        discardPile.add(toAdd1);
        discardPile.add(toAdd2);
        discardPile.remove();
        Card removed = discardPile.remove();
        assertEquals(toAdd1, removed);
    }
}
