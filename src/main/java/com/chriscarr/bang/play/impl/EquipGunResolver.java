package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.Gun;
import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayResolver;
import com.chriscarr.bang.turn.TurnContext;

public class EquipGunResolver implements PlayResolver {
    @Override
    public void resolve(TurnContext ctx, PlayCommand cmd) {
        Card card = cmd.playedCard();
        Player currentPlayer = ctx.currentPlayer();

        Gun newGun = ((Gun) card);
        currentPlayer.getHand().remove(card);

        if (currentPlayer.hasGun()) {
            ctx.discard().add(currentPlayer.removeGun());
        }

        if (Character.JOHNNYKISCH.equals(currentPlayer.getCharacter())) {
            for (Player otherPlayer : ctx.players()) {
                if (otherPlayer.equals(currentPlayer)) {
                    continue;
                }
                CardName otherPlayersGun = otherPlayer.getCardsInPlay().getGunName();
                if (otherPlayersGun.equals(newGun.getName()) && otherPlayer.getCardsInPlay().hasGun()) {
                    Gun removedGun = otherPlayer.removeGun();
                    ctx.discard().add(removedGun);
                    ctx.ui().printInfo(currentPlayer.getName() + " plays a " + newGun.getName() + " and forces " + otherPlayer.getName() + " to discard one from play.");
                }
            }
        }

        currentPlayer.setGun(newGun);
        ctx.ui().printInfo(currentPlayer.getName() + " equips " + newGun.getName());
    }
}
