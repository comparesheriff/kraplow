package com.chriscarr.bang.cards;

import com.chriscarr.bang.Player;

import java.util.ArrayList;
import java.util.List;

public class SingleUseMissed extends SingleUse implements Playable {

    public SingleUseMissed(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        if (readyToPlay) {
            return false;
        }
        return !player.isInPlay(this.getName());
    }

    public List<Player> targets(Player player, List<Player> players) {
        List<Player> targets = new ArrayList<>();
        targets.add(player);
        return targets;
    }

}
