package com.chriscarr;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.cards.Card;
import junit.framework.TestCase;

public class DeckTest extends TestCase {
	public void testDeckAdd(){
		Deck deck = new Deck();
		Card card = new Card();
		deck.add(card);
		Card outCard = deck.pull();
		assertEquals(card, outCard);
	}
	
	public void testDeckRunOut(){
		assertTrue(new Deck().isEmpty());
	}
	
	public void testDeckAddRunOut(){
		Deck deck = new Deck();
		deck.add(new Card());
		assertFalse(deck.isEmpty());
	}
	
	public void testDeckAddPullRunOut(){
		Deck deck = new Deck();
		deck.add(new Card());
		deck.pull();
		assertTrue(deck.isEmpty());
	}
	
	public void testDeckPullOrder(){
		Deck deck = new Deck();
		Card card1 = new Card();
		Card card2 = new Card();
		deck.add(card1);
		deck.add(card2);
		Card pulled = deck.pull();
		assertEquals(pulled, card2);
	}
	
	public void testShuffle(){	
		boolean sameOrder = false;
		boolean reverseOrder = false;
		for(int i = 0; i < 100 && (!sameOrder || !reverseOrder); i ++){
			Deck deck = new Deck();
			Card card1 = new Card();
			Card card2 = new Card();
			deck.add(card1);
			deck.add(card2);		
			deck.shuffle();
			Card pulled1 = deck.pull();
			Card pulled2 = deck.pull();
			if(pulled1.equals(card1) && pulled2.equals(card2)){
				sameOrder = true;
			} else {
				reverseOrder = true;
			}
		}
		assertTrue(sameOrder && reverseOrder);
	}
	
	public void testEmptyDeckDiscardShuffle(){
		Deck deck = new Deck();
		Discard discard = new Discard();
		Card discarded = new Card();
		discard.add(discarded);
		deck.setDiscard(discard);
		Card pulled = deck.pull();
		assertEquals(pulled, discarded);
	}
}
