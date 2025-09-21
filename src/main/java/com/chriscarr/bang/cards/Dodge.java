package com.chriscarr.bang.cards;

import com.chriscarr.bang.Player;

import java.util.List;

public class Dodge extends Bang implements Playable {

    public Dodge(String name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return false;
    }
}
