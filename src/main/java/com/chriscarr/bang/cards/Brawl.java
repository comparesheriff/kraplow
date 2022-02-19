package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Brawl extends Card implements Playable {
    public Brawl(String name, int suit, int value, int type) {
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
        return Turn.others(player, players);
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

        //discard the card
        Hand currentHand = currentPlayer.getHand();
        Card discardCard = currentHand.remove(cardDiscard);
        discardPile.add(discardCard);
        discardPile.add(this);
        //Discard card from players
        Player brawlPlayer = Turn.getNextPlayer(currentPlayer, players);
        while (brawlPlayer != currentPlayer) {
            Player nextPlayer = Turn.getNextPlayer(brawlPlayer, players);

            if (brawlPlayer.getInPlay().count() == 0 && !brawlPlayer.getInPlay().hasGun() && brawlPlayer.getHand().size() == 0) {
                userInterface.printInfo(brawlPlayer.getName() + " has nothing to discard");
                brawlPlayer = nextPlayer;
                continue;
            }

            int chosenCard = -3;
            while (chosenCard < -2 || chosenCard > brawlPlayer.getInPlay().size() - 1) {
                chosenCard = userInterface.askOthersCard(currentPlayer, brawlPlayer.getInPlay(), brawlPlayer.getHand().size() > 0);
            }
            if (chosenCard == -1) {
                Card card = brawlPlayer.getHand().removeRandom();
                discardPile.add(card);
                userInterface.printInfo(currentPlayer.getName() + " discards a " + card.getName() + " from " + brawlPlayer.getName() + "'s hand with a Brawl");
            } else if (chosenCard == -2) {
                Card card = brawlPlayer.getInPlay().removeGun();
                discardPile.add(card);
                userInterface.printInfo(currentPlayer.getName() + " discards a " + card.getName() + " from " + brawlPlayer.getName() + " with a Brawl");
            } else {
                Card card = brawlPlayer.getInPlay().remove(chosenCard);
                discardPile.add(card);
                userInterface.printInfo(currentPlayer.getName() + " discards a " + card.getName() + " from " + brawlPlayer.getName() + " with a Brawl");
            }
            brawlPlayer = nextPlayer;
        }

        return true;
    }
}
