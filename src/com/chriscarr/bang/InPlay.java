package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.gamestate.GameStateCard;

import java.util.ArrayList;
import java.util.List;

public class InPlay {

    Card gun = null;
    List<Card> cards = new ArrayList<>();

    public boolean hasGun() {
        return gun != null;
    }

    public void setGun(Card object) {
        gun = object;
    }

    public void add(Card toAdd) {
        cards.add(toAdd);
    }

    public Card peek(int i) {
        return cards.get(i);
    }

    public Card remove(int i) {
        return cards.remove(i);
    }

    public int count() {
        return cards.size();
    }

    public Card removeGun() { //refactor
        Card tempGun = gun;
        gun = new Card();
        gun = null;
        return tempGun;
    }

    public boolean hasItem(String cardName) {
        for (Card card : cards) {
            if (card.getName().equals(cardName)) {
                return true;
            }
        }
        return false;
    }

    public int getGunRange() {
        if (hasGun()) {
            return Card.getRange(gun.getName());
        } else {
            return 1;
        }
    }

    public boolean isGunVolcanic() {
        return Card.CARDVOLCANIC.equals(gun.getName());
    }

    public Card removeDynamite() {
        for (Card card : cards) {
            if (Card.CARDDYNAMITE.equals(card.getName())) {
                cards.remove(card);
                return card;
            }
        }
        return null;
    }

    public Card removeJail() {
        for (Card card : cards) {
            if (Card.CARDJAIL.equals(card.getName())) {
                cards.remove(card);
                return card;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public int size() {
        return cards.size();
    }

    public Card get(int i) {
        return cards.get(i);
    }

    public String getGunName() {
        if (hasGun()) {
            return gun.getName();
        } else {
            return "Colt .45";
        }
    }

    public Card getGun() {
        return gun;
    }

    public List<GameStateCard> getGameStateInPlay() {
        List<GameStateCard> gameStateCards = new ArrayList<>();
        for (Card card : cards) {
            gameStateCards.add(Turn.cardToGameStateCard(card));
        }
        return gameStateCards;
    }
}
