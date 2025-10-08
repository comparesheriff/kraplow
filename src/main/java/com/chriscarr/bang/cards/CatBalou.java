package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;
import java.util.Optional;

public class CatBalou extends Card implements Playable {
    public CatBalou(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#canPlay(main.chriscarr.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        List<Player> others = targets(player, players);
        return !others.isEmpty();
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#targets(main.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#play(main.chriscarr.bang.Player, java.util.List, main.chriscarr.bang.UserInterface, main.bang.Deck, main.chriscarr.bang.Discard)
     */
    public boolean play(
            Player currentPlayer,
            List<Player> players,
            UserInterface userInterface,
            Deck deck,
            Discard discard,
            Turn turn) {
        discard.add(this);
        Player other =
                Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        if (Character.APACHEKID.equals(other.getCharacter()) && this.getSuit() == CardSuit.DIAMONDS) {
            userInterface.printInfo(other.getName() + " is unaffected by diamond Cat Balou");
            return true;
        }
        int chosenCard = -3;
        while (chosenCard < -2 || chosenCard > other.getCardsInPlay().size() - 1) {
            chosenCard =
                    userInterface.askOthersCard(
                            currentPlayer, other.getCardsInPlay(), !other.getHand().isEmpty());
        }
        if (chosenCard == -1) {
            Optional<Card> cardOptional = other.getHand().removeRandom();
            if (cardOptional.isPresent()) {
                discard.add(cardOptional.get());
                userInterface.printInfo(
                        currentPlayer.getName()
                                + " discards a "
                                + cardOptional.get().getName()
                                + " from "
                                + other.getName()
                                + "'s hand with a Cat Balou");
            }
        } else if (chosenCard == -2) {
            Card card = other.getCardsInPlay().removeGun();
            discard.add(card);
            userInterface.printInfo(
                    currentPlayer.getName()
                            + " discards a "
                            + card.getName()
                            + " from "
                            + other.getName()
                            + " with a Cat Balou");
        } else {
            Card card = other.getCardsInPlay().remove(chosenCard);
            discard.add(card);
            userInterface.printInfo(
                    currentPlayer.getName()
                            + " discards a "
                            + card.getName()
                            + " from "
                            + other.getName()
                            + " with a Cat Balou");
        }
        return true;
    }
}
