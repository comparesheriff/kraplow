package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Canteen extends SingleUse implements Playable {

    public Canteen(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    public boolean activate(Player currentPlayer, List<Player> players,
                            UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {

        if (Turn.canPlayerHeal(currentPlayer)) {
            currentPlayer.addHealth(1);
        }
        removeFromInPlay(currentPlayer);
        discardPile.add(this);
        return true;
    }

}
