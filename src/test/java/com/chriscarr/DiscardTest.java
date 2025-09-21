package com.chriscarr;

import com.chriscarr.bang.Discard;
import com.chriscarr.bang.cards.Card;
import junit.framework.TestCase;

public class DiscardTest extends TestCase{
	public void testDiscardCard(){
		Discard discard = new Discard();
		Card toAdd = new Card();
		discard.add(toAdd);
		Card peeked = discard.getLast();
		assertEquals(toAdd, peeked);
	}
	
	public void testRemove(){
		Discard discard = new Discard();
		Card toAdd = new Card();
		discard.add(toAdd);
		Card removed = discard.removeLast();
		assertEquals(toAdd, removed);
	}

	public void testRemoveTwo(){
		Discard discard = new Discard();
		Card toAdd1 = new Card();
		Card toAdd2 = new Card();
		discard.add(toAdd1);
		discard.add(toAdd2);
		discard.removeLast();
		Object removed = discard.removeLast();
		assertEquals(toAdd1, removed);
	}
}
