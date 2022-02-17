package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Saloon extends Card implements Playable {

    public Saloon(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    @Override
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return true;
    }

    @Override
    public boolean play(Player currentPlayer, List<Player> players,
                        UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {
        discardPile.add(this);
        for (Player player : players) {
            if (Turn.canPlayerHeal(player)) {
                player.addHealth(1);
            }
        }
        return true;
    }

    @Override
    public List<Player> targets(Player player, List<Player> players) {
        return players;
    }

}
