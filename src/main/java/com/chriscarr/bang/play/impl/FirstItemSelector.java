package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardType;
import com.chriscarr.bang.play.CardSelector;
import com.chriscarr.bang.turn.TurnContext;

public final class FirstItemSelector implements CardSelector {
    @Override
    public int chooseEquipFromHand(Player player, TurnContext ctx) {
        for (int i = 0; i < player.getHand().size(); i++) {
            Card c = player.getHand().get(i);
            CardType t = c.getType() != null ? c.getType() : c.getName().defaultType();
            if (t == CardType.ITEM) return i;
        }
        return -1;
    }
}
