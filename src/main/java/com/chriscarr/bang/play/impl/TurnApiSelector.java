package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardType;
import com.chriscarr.bang.play.CardSelector;
import com.chriscarr.bang.turn.TurnContext;
import com.chriscarr.bang.userinterface.UserInterface;

public final class TurnApiSelector implements CardSelector {
    @Override
    public int chooseEquipFromHand(Player player, TurnContext ctx) {
        if (player == null || ctx == null) return -1;
        UserInterface ui = ctx.ui();
        if (ui == null) {return -1;}

        int choice = ui.askPlay(player);
        if (choice < 0) return -1;

        int handSize = player.getHand().size();
        if (choice >= handSize) return -1;

        Card card = player.getHand().get(choice);
        CardType type = card.getType() != null ? card.getType() : card.getName().defaultType();
        return type == CardType.ITEM ? choice : -1;
    }
}
