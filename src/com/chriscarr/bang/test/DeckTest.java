package com.chriscarr.bang.test;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.cards.Card;
import junit.framework.TestCase;

public class DeckTest extends TestCase {
    public void testDeckAdd() {
        Deck deck = new Deck();
        Card card = new Card();
        deck.add(card);
        Card outCard = deck.pull();
        assertEquals(card, outCard);
    }

    public void testDeckRunOut() {
        Deck deck = new Deck();
        assertTrue(deck.isEmpty());
    }

    public void testDeckAddRunOut() {
        Deck deck = new Deck();
        deck.add(new Card());
        assertFalse(deck.isEmpty());
    }

    public void testDeckAddPullRunOut() {
        Deck deck = new Deck();
        deck.add(new Card());
        deck.pull();
        assertTrue(deck.isEmpty());
    }

    public void testDeckPullOrder() {
        Deck deck = new Deck();
        Card card1 = new Card();
        Card card2 = new Card();
        deck.add(card1);
        deck.add(card2);
        Card pulled = deck.pull();
        assertEquals(pulled, card2);
    }

    public void testShuffle() {
        boolean sameOrder = false;
        boolean reverseOrder = false;
        for (int i = 0; i < 100 && (!sameOrder || !reverseOrder); i++) {
            Deck deck = new Deck();
            Card card1 = new Card();
            Card card2 = new Card();
            deck.add(card1);
            deck.add(card2);
            deck.shuffle();
            Card pulled1 = deck.pull();
            Card pulled2 = deck.pull();
            if (pulled1.equals(card1) && pulled2.equals(card2)) {
                sameOrder = true;
            } else {
                reverseOrder = true;
            }
        }
        assertTrue(sameOrder && reverseOrder);
    }

    public void testEmptyDeckDiscardShuffle() {
        Deck deck = new Deck();
        DiscardPile discardPile = new DiscardPile();
        Card discarded = new Card();
        discardPile.add(discarded);
        deck.setDiscard(discardPile);
        Card pulled = deck.pull();
        assertEquals(pulled, discarded);
    }
}
