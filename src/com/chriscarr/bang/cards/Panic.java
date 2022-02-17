package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Panic extends Card implements Playable {
    public Panic(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#canPlay(com.chriscarr.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        List<Player> others = targets(player, players);
        return others.size() > 1;
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#targets(com.chriscarr.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.getPlayersWithCards(Turn.getPlayersWithinRange(player, players, 1));
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#play(com.chriscarr.bang.Player, java.util.List, com.chriscarr.bang.UserInterface, com.chriscarr.bang.Deck, com.chriscarr.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {
        Player otherPlayer = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        if (Figure.APACHEKID.equals(otherPlayer.getAbility()) && this.getSuit() == Card.DIAMONDS) {
            userInterface.printInfo(otherPlayer.getName() + " is unaffected by diamond Panic!");
            return true;
        }
        if (!(otherPlayer instanceof CancelPlayer)) {
            discardPile.add(this);
            int chosenCard = -3;
            while (chosenCard < -2 || chosenCard > otherPlayer.getInPlay().size() - 1) {
                chosenCard = userInterface.askOthersCard(currentPlayer, otherPlayer.getInPlay(), otherPlayer.getHand().size() > 0);
            }
            Hand hand = currentPlayer.getHand();
            if (chosenCard == -1) {
                hand.add(otherPlayer.getHand().removeRandom());
                userInterface.printInfo(currentPlayer.getName() + " takes a card from " + otherPlayer.getName() + "'s hand with a Panic!");
            } else if (chosenCard == -2) {
                Card card = otherPlayer.getInPlay().removeGun();
                hand.add(card);
                userInterface.printInfo(currentPlayer.getName() + " takes a " + card.getName() + " from " + otherPlayer.getName() + " with a Panic!");
            } else {
                Card card = otherPlayer.getInPlay().remove(chosenCard);
                hand.add(card);
                userInterface.printInfo(currentPlayer.getName() + " takes a " + card.getName() + " from " + otherPlayer.getName() + " with a Panic!");
            }
            return true;
        } else {
            currentPlayer.getHand().add(this);
            return false;
        }
    }
}
