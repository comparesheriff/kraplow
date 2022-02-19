package com.chriscarr.bang.test;

import com.chriscarr.bang.InPlay;
import com.chriscarr.bang.cards.Card;
import junit.framework.TestCase;

public class InPlayTest extends TestCase {
    public void testDoesNotHaveGun() {
        InPlay inPlay = new InPlay();
        assertFalse(inPlay.hasGun());
    }

    public void testDoesHaveGun() {
        InPlay inPlay = new InPlay();
        inPlay.setGun(new Card());
        assertTrue(inPlay.hasGun());
    }

    public void testRemoveGun() {
        InPlay inPlay = new InPlay();
        Card gun = new Card();
        inPlay.setGun(gun);
        Card removedGun = inPlay.removeGun();
        assertEquals(removedGun, gun);
    }

    public void testRemovedGun() {
        InPlay inPlay = new InPlay();
        Card gun = new Card();
        inPlay.setGun(gun);
        inPlay.removeGun();
        assertFalse(inPlay.hasGun());
    }

    public void testAddCard() {
        InPlay inPlay = new InPlay();
        Card toAdd = new Card();
        inPlay.add(toAdd);
        Card peeked = inPlay.peek(0);
        assertEquals(toAdd, peeked);
    }

    public void testRemoveCard() {
        InPlay inPlay = new InPlay();
        Card toAdd = new Card();
        inPlay.add(toAdd);
        Card removed = inPlay.remove(0);
        assertEquals(toAdd, removed);
    }

    public void testCountInPlay() {
        InPlay inPlay = new InPlay();
        assertEquals(0, inPlay.count());
    }

    public void testCountInPlayAdd() {
        InPlay inPlay = new InPlay();
        inPlay.add(new Card());
        assertEquals(1, inPlay.count());
    }

    public void testSameNameReject() {
        InPlay inPlay = new InPlay();
        assertFalse(inPlay.hasItem(Card.CARDBARREL));
        inPlay.add(new Card(Card.CARDBARREL, Card.SPADES, Card.VALUEQ, Card.TYPEITEM));
        assertTrue(inPlay.hasItem(Card.CARDBARREL));
        assertFalse(inPlay.hasItem(Card.CARDSCOPE));
    }
}
