package com.chriscarr;

import com.chriscarr.bang.CardsInPlay;
import com.chriscarr.bang.cards.*;
import junit.framework.TestCase;

public class CardsInPlayTest extends TestCase {
  public void testDoesNotHaveGun() {
    CardsInPlay cardsInPlay = new CardsInPlay();
    assertFalse(cardsInPlay.hasGun());
  }

  public void testDoesHaveGun() {
    CardsInPlay cardsInPlay = new CardsInPlay();
    cardsInPlay.setGun(new Gun(CardName.VOLCANIC, CardSuit.SPADES, CardValue.ACE, CardType.GUN));
    assertTrue(cardsInPlay.hasGun());
  }

  public void testRemoveGun() {
    CardsInPlay cardsInPlay = new CardsInPlay();
    Gun gun = new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.KING, CardType.GUN);
    cardsInPlay.setGun(gun);
    Gun removedGun = cardsInPlay.removeGun();
    assertEquals(removedGun, gun);
  }

  public void testRemovedGun() {
    CardsInPlay cardsInPlay = new CardsInPlay();
    Gun gun = new Gun(CardName.REMINGTON, CardSuit.DIAMONDS, CardValue.QUEEN, CardType.GUN);
    cardsInPlay.setGun(gun);
    cardsInPlay.removeGun();
    assertFalse(cardsInPlay.hasGun());
  }

  public void testAddCard() {
    CardsInPlay cardsInPlay = new CardsInPlay();
    Card toAdd = new Card();
    cardsInPlay.add(toAdd);
    Card peeked = cardsInPlay.getFirst();
    assertEquals(toAdd, peeked);
  }

  public void testRemoveCard() {
    CardsInPlay cardsInPlay = new CardsInPlay();
    Card toAdd = new Card();
    cardsInPlay.add(toAdd);
    Card removed = cardsInPlay.removeFirst();
    assertEquals(toAdd, removed);
  }

  public void testCountInPlay() {
    assertEquals(0, new CardsInPlay().size());
  }

  public void testCountInPlayAdd() {
    CardsInPlay cardsInPlay = new CardsInPlay();
    cardsInPlay.add(new Card());
    assertEquals(1, cardsInPlay.size());
  }

  public void testSameNameReject() {
    CardsInPlay cardsInPlay = new CardsInPlay();
    assertFalse(cardsInPlay.hasItem(CardName.BARREL));
    cardsInPlay.add(new Card(CardName.BARREL, CardSuit.SPADES, CardValue.QUEEN, CardType.ITEM));
    assertTrue(cardsInPlay.hasItem(CardName.BARREL));
    assertFalse(cardsInPlay.hasItem(CardName.SCOPE));
  }
}
