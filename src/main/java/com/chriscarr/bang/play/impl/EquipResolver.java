package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayResolver;
import com.chriscarr.bang.turn.TurnContext;

public final class EquipResolver implements PlayResolver {
    @Override
    public void resolve(TurnContext ctx, PlayCommand cmd) {
        Card card = cmd.playedCard();
        Player player = ctx.currentPlayer();
        player.getHand().remove(card);
        player.addInPlay(card);
        ctx.ui().printInfo(player.getName() + " equips " + card.getName());
    }
}
