package com.chriscarr.bang.cards;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.models.game.Character;

import java.util.List;

public class Missed extends Bang implements Playable {

    public Missed(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        if (!Character.CALAMITY_JANET.equals(player.getCharacter())) {
            return false;
        } else {
            return super.canPlay(player, players, bangsPlayed);
        }
    }
}
