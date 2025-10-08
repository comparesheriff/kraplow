package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class RagTime extends Card implements Playable {
    public RagTime(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#canPlay(main.chriscarr.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return player.getHand().size() >= 2;
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#targets(main.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#play(main.bang.Player, java.util.List, main.bang.UserInterface, main.chriscarr.bang.Deck, main.chriscarr.bang.Discard)
     */
    public boolean play(
        Player currentPlayer,
        List<Player> players,
        UserInterface userInterface,
        Deck deck,
        Discard discard,
        Turn turn) {
        // Choose card to discard
        int cardDiscard = userInterface.askDiscard(currentPlayer);
        if (cardDiscard == -1) {
            return false;
        }
        // Choose player to take a card from
        Player otherPlayer =
            Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        // Steal from player
        if (!(otherPlayer instanceof CancelPlayer)) {
            int chosenCard = -3;
            while (chosenCard < -2 || chosenCard > otherPlayer.getCardsInPlay().size() - 1) {
                chosenCard =
                    userInterface.askOthersCard(
                        currentPlayer, otherPlayer.getCardsInPlay(), !otherPlayer.getHand().isEmpty());
            }
            Hand hand = currentPlayer.getHand();
            if (chosenCard == -1) {
                otherPlayer.getHand().removeRandom().ifPresent(hand::add);
                userInterface.printInfo(
                    currentPlayer.getName()
                        + " takes a card from "
                        + otherPlayer.getName()
                        + "'s hand with a "
                        + this.getName());
            } else if (chosenCard == -2) {
                Card card = otherPlayer.getCardsInPlay().removeGun();
                hand.add(card);
                userInterface.printInfo(
                    currentPlayer.getName()
                        + " takes a "
                        + card.getName()
                        + " from "
                        + otherPlayer.getName()
                        + " with a "
                        + this.getName());
            } else {
                Card card = otherPlayer.getCardsInPlay().remove(chosenCard);
                hand.add(card);
                userInterface.printInfo(
                    currentPlayer.getName()
                        + " takes a "
                        + card.getName()
                        + " from "
                        + otherPlayer.getName()
                        + " with a "
                        + this.getName());
            }
            // discard the card
            Hand currentHand = currentPlayer.getHand();
            Card card = currentHand.remove(cardDiscard);
            discard.add(card);
            discard.add(this);
            return true;
        } else {
            return false;
        }
    }
}
