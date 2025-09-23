package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class Beer extends Card implements Playable {

    public Beer(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    @Override
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return true;
    }

    @Override
    public boolean play(Player currentPlayer, List<Player> players,
                        UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        discard.add(this);
        if (Turn.isBeerGiveHealth(players)) {
            if (!Turn.isMaxHealth(currentPlayer)) {
                currentPlayer.addHealth(1);
            }
            if (Character.TEQUILAJOE.equals(currentPlayer.getCharacter())) {
                if (!Turn.isMaxHealth(currentPlayer)) {
                    currentPlayer.addHealth(1);
                }
            }
        }
        return true;
    }

    @Override
    public List<Player> targets(Player player, List<Player> players) {
        return List.of(player);
    }

}
