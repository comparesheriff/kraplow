package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class GeneralStore extends Card implements Playable {
    public GeneralStore(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#canPlay(com.chriscarr.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return true;
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#targets(com.chriscarr.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return players;
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#play(com.chriscarr.bang.Player, java.util.List, com.chriscarr.bang.UserInterface, com.chriscarr.bang.Deck, com.chriscarr.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {
        discardPile.add(this);
        List<Card> generalStoreCards = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if (deck.size() == 0) {
                userInterface.printInfo("Shuffling the deck");
            }
            generalStoreCards.add(deck.pull());
        }
        Player generalPlayer = currentPlayer;
        while (!generalStoreCards.isEmpty()) {
            int chosenCard = -1;
            while (chosenCard < 0 || chosenCard > generalStoreCards.size() - 1) {
                chosenCard = userInterface.chooseGeneralStoreCard(generalPlayer, generalStoreCards);
            }
            Card card = generalStoreCards.remove(chosenCard);
            userInterface.printInfo(generalPlayer.getName() + " chooses " + card.getName() + " from " + Card.CARDGENERALSTORE);
            generalPlayer.getHand().add(card);
            generalPlayer = Turn.getNextPlayer(generalPlayer, players);
        }
        return true;
    }
}
