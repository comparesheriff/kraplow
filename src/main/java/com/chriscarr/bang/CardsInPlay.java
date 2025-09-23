package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.Gun;
import com.chriscarr.bang.gamestate.GameStateCard;

import java.util.ArrayList;
import java.util.List;

public class CardsInPlay extends ArrayList<Card> {

    Gun gun = null;

    public boolean hasGun() {
        return gun != null;
    }

    public void setGun(Gun gun) {
        this.gun = gun;
    }

    public Gun removeGun() {
        Gun tempGun = gun;
        gun = null;
        return tempGun;
    }

    public boolean hasItem(CardName cardName) {
        for (Card card : this) {
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
        return gun.getName().equals(CardName.VOLCANIC);
    }

    public Card removeDynamite() {
        for (Card card : this) {
            if (card.getName().equals(CardName.DYNAMITE)) {
                remove(card);
                return card;
            }
        }
        return null;
    }

    public Card removeJail() {
        for (Card card : this) {
            if (card.getName().equals(CardName.JAIL)) {
                remove(card);
                return card;
            }
        }
        return null;
    }

    public CardName getGunName() {
        if (hasGun()) {
            return gun.getName();
        } else {
            return CardName.COLT;
        }
    }

    public Gun getGun() {
        return gun;
    }

    public List<GameStateCard> getGameStateInPlay() {
        List<GameStateCard> gameStateCards = new ArrayList<>();
        for (Card card : this) {
            gameStateCards.add(Turn.cardToGameStateCard(card));
        }
        return gameStateCards;
    }
}
