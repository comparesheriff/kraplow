package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.Gun;
import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayResolver;
import com.chriscarr.bang.turn.TurnContext;

public class EquipGunResolver implements PlayResolver {
    @Override
    public void resolve(TurnContext ctx, PlayCommand cmd) {
        Player player = ctx.currentPlayer();
        Card card = cmd.playedCard();
        Gun newGun = ((Gun) card);
        player.getHand().remove(card);
        if (player.hasGun()) {
            ctx.discard().add(player.removeGun());
        }
        player.setGun(newGun);

    }
}
