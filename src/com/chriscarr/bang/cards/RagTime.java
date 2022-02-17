package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class RagTime extends Card implements Playable {
    public RagTime(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#canPlay(com.chriscarr.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return player.getHand().size() >= 2;
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#targets(com.chriscarr.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#play(com.chriscarr.bang.Player, java.util.List, com.chriscarr.bang.UserInterface, com.chriscarr.bang.Deck, com.chriscarr.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {
        //Choose card to discard
        int cardDiscard = userInterface.askDiscard(currentPlayer);
        if (cardDiscard == -1) {
            return false;
        }
        //Choose player to take a card from
        Player otherPlayer = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        //Steal from player
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
            //discard the card
            Hand currentHand = currentPlayer.getHand();
            Card card = currentHand.remove(cardDiscard);
            discardPile.add(card);
            discardPile.add(this);
            return true;
        } else {
            return false;
        }
    }
}
