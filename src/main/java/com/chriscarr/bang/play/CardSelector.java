package com.chriscarr.bang.play;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.turn.TurnContext;

public interface CardSelector {
    int chooseEquipFromHand(Player player, TurnContext ctx);
}
