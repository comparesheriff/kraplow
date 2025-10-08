package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;
import java.util.Optional;

public class Brawl extends Card implements Playable {
    public Brawl(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#canPlay(main.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return player.getHand().size() >= 2;
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#targets(main.chriscarr.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.others(player, players);
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#play(main.chriscarr.bang.Player, java.util.List, main.chriscarr.bang.UserInterface, main.chriscarr.bang.Deck, main.bang.Discard)
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

        // discard the card
        Hand currentHand = currentPlayer.getHand();
        Card discardCard = currentHand.remove(cardDiscard);
        discard.add(discardCard);
        discard.add(this);
        // Discard card from players
        Player brawlPlayer = Turn.getNextPlayer(currentPlayer, players);
        while (brawlPlayer != currentPlayer) {
            Player nextPlayer = Turn.getNextPlayer(brawlPlayer, players);

            if (brawlPlayer.getCardsInPlay().isEmpty()
                    && !brawlPlayer.getCardsInPlay().hasGun()
                    && brawlPlayer.getHand().isEmpty()) {
                userInterface.printInfo(brawlPlayer.getName() + " has nothing to discard");
                brawlPlayer = nextPlayer;
                continue;
            }

            int chosenCard = -3;
            while (chosenCard < -2 || chosenCard > brawlPlayer.getCardsInPlay().size() - 1) {
                chosenCard =
                        userInterface.askOthersCard(
                                currentPlayer, brawlPlayer.getCardsInPlay(), !brawlPlayer.getHand().isEmpty());
            }
            if (chosenCard == -1) {
                Optional<Card> cardOptional = brawlPlayer.getHand().removeRandom();
                if (cardOptional.isPresent()) {
                    discard.add(cardOptional.get());
                    userInterface.printInfo(
                            currentPlayer.getName()
                                    + " discards a "
                                    + cardOptional.get().getName()
                                    + " from "
                                    + brawlPlayer.getName()
                                    + "'s hand with a Brawl");
                }
            } else if (chosenCard == -2) {
                Card card = brawlPlayer.getCardsInPlay().removeGun();
                discard.add(card);
                userInterface.printInfo(
                        currentPlayer.getName()
                                + " discards a "
                                + card.getName()
                                + " from "
                                + brawlPlayer.getName()
                                + " with a Brawl");
            } else {
                Card card = brawlPlayer.getCardsInPlay().remove(chosenCard);
                discard.add(card);
                userInterface.printInfo(
                        currentPlayer.getName()
                                + " discards a "
                                + card.getName()
                                + " from "
                                + brawlPlayer.getName()
                                + " with a Brawl");
            }
            brawlPlayer = nextPlayer;
        }

        return true;
    }
}
