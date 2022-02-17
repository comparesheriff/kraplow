package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.models.game.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class CanCan extends SingleUse implements Playable {

    public CanCan(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    public boolean activate(Player currentPlayer, List<Player> players,
                            UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {

        Player otherPlayer = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        if (Character.APACHE_KID.equals(otherPlayer.getCharacter()) && this.getSuit() == Card.DIAMONDS) {
            userInterface.printInfo(otherPlayer.getName() + " is unaffected by diamond " + this.getName());
            return true;
        }
        if (!(otherPlayer instanceof CancelPlayer)) {
            int chosenCard = -3;
            while (chosenCard < -2 || chosenCard > otherPlayer.getInPlay().size() - 1) {
                chosenCard = userInterface.askOthersCard(currentPlayer, otherPlayer.getInPlay(), otherPlayer.getHand().size() > 0);
            }
            if (chosenCard == -1) {
                Card discardedCard = otherPlayer.getHand().removeRandom();
                discardPile.add(discardedCard);
                userInterface.printInfo(currentPlayer.getName() + " discards a " + discardedCard.getName() + " from " + otherPlayer.getName() + "'s hand with a " + this.getName());
            } else if (chosenCard == -2) {
                Card card = otherPlayer.getInPlay().removeGun();
                discardPile.add(card);
                userInterface.printInfo(currentPlayer.getName() + " discards a " + card.getName() + " from " + otherPlayer.getName() + " with a " + this.getName());
            } else {
                Card card = otherPlayer.getInPlay().remove(chosenCard);
                discardPile.add(card);
                userInterface.printInfo(currentPlayer.getName() + " discards a " + card.getName() + " from " + otherPlayer.getName() + " with a " + this.getName());
            }
            removeFromInPlay(currentPlayer);
            discardPile.add(this);
            return true;
        } else {
            return false;
        }
    }

}
