package java.com.chriscarr;

import java.com.chriscarr.bang.cards.Card;

import junit.framework.TestCase;

public class CardTest extends TestCase {
	public void testSetName(){
		Card card = new Card();
		String setName = "Banana";
		card.setName(setName);
		String gotName = card.getName();
		assertEquals(setName, gotName);
	}
	
	public void testSetOtherName(){
		Card card = new Card();
		String setName = "Goat";
		card.setName(setName);
		String gotName = card.getName();
		assertEquals(setName, gotName);
	}
	
	public void testSetGetSuit(){
		Card card = new Card();
		int setSuit = Card.HEARTS;
		card.setSuit(setSuit);
		int gotSuit = card.getSuit();
		assertEquals(setSuit, gotSuit);
	}
	
	public void testSetGetSuitClubs(){
		Card card = new Card();
		int setSuit = Card.CLUBS;
		card.setSuit(setSuit);
		int gotSuit = card.getSuit();
		assertEquals(setSuit, gotSuit);
	}

	public void testSetGetSuitSpades(){
		Card card = new Card();
		int setSuit = Card.SPADES;
		card.setSuit(setSuit);
		int gotSuit = card.getSuit();
		assertEquals(setSuit, gotSuit);
	}
	
	public void testSetGetSuitDiamonds(){
		Card card = new Card();
		int setSuit = Card.DIAMONDS;
		card.setSuit(setSuit);
		int gotSuit = card.getSuit();
		assertEquals(setSuit, gotSuit);
	}
	
	public void testSetGetValue(){
		Card card = new Card();
		card.setValue(Card.VALUE2);		
		card.setValue(Card.VALUE3);
		card.setValue(Card.VALUE4);
		card.setValue(Card.VALUE5);
		card.setValue(Card.VALUE6);
		card.setValue(Card.VALUE7);
		card.setValue(Card.VALUE8);
		card.setValue(Card.VALUE9);
		card.setValue(Card.VALUE10);
		card.setValue(Card.VALUEJ);
		card.setValue(Card.VALUEQ);
		card.setValue(Card.VALUEK);
		card.setValue(Card.VALUEA);
		int valueGot = card.getValue();
		assertEquals(Card.VALUEA, valueGot);
	}
	
	public void testSetCardType(){
		Card card = new Card();
		card.setType(Card.TYPEGUN);
		card.setType(Card.TYPEITEM);
		card.setType(Card.TYPEPLAY);
		int gotType = card.getType();
		assertEquals(Card.TYPEPLAY, gotType);
	}
	
	public void testGunRange(){		
		assertEquals(1, Card.getRange(Card.CARDVOLCANIC));
		assertEquals(2, Card.getRange(Card.CARDSCHOFIELD));
		assertEquals(3, Card.getRange(Card.CARDREMINGTON));
		assertEquals(4, Card.getRange(Card.CARDWINCHESTER));
		assertEquals(5, Card.getRange(Card.CARDREVCARBINE));
	}
	
	public void testGunMultiBang(){
		assertTrue(Card.multiBang(Card.CARDVOLCANIC));
		assertFalse(Card.multiBang(Card.CARDSCHOFIELD));
	}
}
