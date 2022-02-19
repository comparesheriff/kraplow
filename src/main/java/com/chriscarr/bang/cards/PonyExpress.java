package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class PonyExpress extends SingleUse implements Playable {

    public PonyExpress(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    public boolean activate(Player currentPlayer, List<Player> players,
                            UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {

        Hand currentHand = currentPlayer.getHand();
        currentHand.add(deck.pull());
        currentHand.add(deck.pull());
        currentHand.add(deck.pull());
        userInterface.printInfo(currentPlayer.getName() + " draws 3 cards");
        removeFromInPlay(currentPlayer);
        discardPile.add(this);
        return true;
    }

}