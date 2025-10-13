package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardType;
import com.chriscarr.bang.play.CardSelector;
import com.chriscarr.bang.turn.TurnContext;

public class FirstGunSelector implements CardSelector {
    @Override
    public int chooseEquipFromHand(Player player, TurnContext ctx) {
        for (int i = 0; i < player.getHand().size(); i++) {
            Card card = player.getHand().get(i);
            CardType type = card.getType() != null ? card.getType() : card.getName().defaultType();
            if (type == CardType.GUN) return i;
        }
        return -1;
    }
}
