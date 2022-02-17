package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Conestoga extends SingleUse implements Playable {

    public Conestoga(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    public boolean activate(Player currentPlayer, List<Player> players,
                            UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {

        Player otherPlayer = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        if (Figure.APACHEKID.equals(otherPlayer.getAbility()) && this.getSuit() == Card.DIAMONDS) {
            userInterface.printInfo(otherPlayer.getName() + " is unaffected by diamond " + this.getName());
            return true;
        }
        if (!(otherPlayer instanceof CancelPlayer)) {
            int chosenCard = -3;
            while (chosenCard < -2 || chosenCard > otherPlayer.getInPlay().size() - 1) {
                chosenCard = userInterface.askOthersCard(currentPlayer, otherPlayer.getInPlay(), otherPlayer.getHand().size() > 0);
            }
            Hand hand = currentPlayer.getHand();
            if (chosenCard == -1) {
                hand.add(otherPlayer.getHand().removeRandom());
                userInterface.printInfo(currentPlayer.getName() + " takes a card from " + otherPlayer.getName() + "'s hand with a " + this.getName());
            } else if (chosenCard == -2) {
                Card card = otherPlayer.getInPlay().removeGun();
                hand.add(card);
                userInterface.printInfo(currentPlayer.getName() + " takes a " + card.getName() + " from " + otherPlayer.getName() + " with a " + this.getName());
            } else {
                Card card = otherPlayer.getInPlay().remove(chosenCard);
                hand.add(card);
                userInterface.printInfo(currentPlayer.getName() + " takes a " + card.getName() + " from " + otherPlayer.getName() + " with a " + this.getName());
            }
            removeFromInPlay(currentPlayer);
            discardPile.add(this);
            return true;
        } else {
            return false;
        }
    }

}
