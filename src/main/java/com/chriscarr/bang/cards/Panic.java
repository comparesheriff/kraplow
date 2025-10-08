package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Panic extends Card implements Playable {
    public Panic(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#canPlay(main.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        List<Player> others = targets(player, players);
        return others.size() > 1;
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#targets(main.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.getPlayersWithCards(Turn.getPlayersWithinRange(player, players));
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
        Player otherPlayer =
                Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        if (Character.APACHEKID.equals(otherPlayer.getCharacter())
                && this.getSuit() == CardSuit.DIAMONDS) {
            userInterface.printInfo(otherPlayer.getName() + " is unaffected by diamond Panic!");
            return true;
        }
        if (!(otherPlayer instanceof CancelPlayer)) {
            discard.add(this);
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
                                + "'s hand with a Panic!");
            } else if (chosenCard == -2) {
                Card card = otherPlayer.getCardsInPlay().removeGun();
                hand.add(card);
                userInterface.printInfo(
                        currentPlayer.getName()
                                + " takes a "
                                + card.getName()
                                + " from "
                                + otherPlayer.getName()
                                + " with a Panic!");
            } else {
                Card card = otherPlayer.getCardsInPlay().remove(chosenCard);
                hand.add(card);
                userInterface.printInfo(
                        currentPlayer.getName()
                                + " takes a "
                                + card.getName()
                                + " from "
                                + otherPlayer.getName()
                                + " with a Panic!");
            }
            return true;
        } else {
            currentPlayer.getHand().add(this);
            return false;
        }
    }
}
