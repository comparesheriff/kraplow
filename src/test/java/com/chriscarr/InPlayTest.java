package com.chriscarr;

import com.chriscarr.bang.InPlay;
import com.chriscarr.bang.cards.*;
import junit.framework.TestCase;

public class InPlayTest extends TestCase {
    public void testDoesNotHaveGun() {
        InPlay inPlay = new InPlay();
        assertFalse(inPlay.hasGun());
    }

    public void testDoesHaveGun() {
        InPlay inPlay = new InPlay();
        inPlay.setGun(new Gun(Card.CARDVOLCANIC, CardSuit.SPADES, CardValue.ACE, CardType.GUN));
        assertTrue(inPlay.hasGun());
    }

    public void testRemoveGun() {
        InPlay inPlay = new InPlay();
        Gun gun = new Gun(Card.CARDSCHOFIELD, CardSuit.HEARTS, CardValue.KING, CardType.GUN);
        inPlay.setGun(gun);
        Gun removedGun = inPlay.removeGun();
        assertEquals(removedGun, gun);
    }

    public void testRemovedGun() {
        InPlay inPlay = new InPlay();
        Gun gun = new Gun(Card.CARDREMINGTON, CardSuit.DIAMONDS, CardValue.QUEEN, CardType.GUN);
        inPlay.setGun(gun);
        inPlay.removeGun();
        assertFalse(inPlay.hasGun());
    }

    public void testAddCard() {
        InPlay inPlay = new InPlay();
        Card toAdd = new Card();
        inPlay.add(toAdd);
        Card peeked = inPlay.getFirst();
        assertEquals(toAdd, peeked);
    }

    public void testRemoveCard() {
        InPlay inPlay = new InPlay();
        Card toAdd = new Card();
        inPlay.add(toAdd);
        Card removed = inPlay.removeFirst();
        assertEquals(toAdd, removed);
    }

    public void testCountInPlay() {
        assertEquals(0, new InPlay().size());
    }

    public void testCountInPlayAdd() {
        InPlay inPlay = new InPlay();
        inPlay.add(new Card());
        assertEquals(1, inPlay.size());
    }

    public void testSameNameReject() {
        InPlay inPlay = new InPlay();
        assertFalse(inPlay.hasItem(Card.CARDBARREL));
        inPlay.add(new Card(Card.CARDBARREL, CardSuit.SPADES, CardValue.QUEEN, CardType.ITEM));
        assertTrue(inPlay.hasItem(Card.CARDBARREL));
        assertFalse(inPlay.hasItem(Card.CARDSCOPE));
    }
}
