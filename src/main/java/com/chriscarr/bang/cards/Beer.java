package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.models.game.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class Beer extends Card implements Playable {

    public Beer(String name, int suit, int value, int type) {
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
        if (Turn.isBeerGiveHealth(players)) {
            if (Turn.canPlayerHeal(currentPlayer)) {
                currentPlayer.addHealth(1);
            }
            if (Character.TEQUILA_JOE.equals(currentPlayer.getCharacter())) {
                if (Turn.canPlayerHeal(currentPlayer)) {
                    currentPlayer.addHealth(1);
                }
            }
        }
        return true;
    }

    @Override
    public List<Player> targets(Player player, List<Player> players) {
        List<Player> targets = new ArrayList<>();
        targets.add(player);
        return targets;
    }

}
