package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.turn.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Conestoga extends SingleUse implements Playable {

    public Conestoga(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    public boolean activate(
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
            userInterface.printInfo(
                otherPlayer.getName() + " is unaffected by diamond " + this.getName());
            return true;
        }
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
            removeFromInPlay(currentPlayer);
            discard.add(this);
            return true;
        } else {
            return false;
        }
    }
}
