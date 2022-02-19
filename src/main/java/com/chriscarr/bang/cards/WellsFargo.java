package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class WellsFargo extends Card implements Playable {

    public WellsFargo(String name, int suit, int value, int type) {
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
        Turn.deckToHand(currentPlayer.getHand(), deck, 3, userInterface);
        return true;
    }

    @Override
    public List<Player> targets(Player player, List<Player> players) {
        List<Player> targets = new ArrayList<>();
        targets.add(player);
        return targets;
    }

}
