package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardType;
import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayValidator;
import com.chriscarr.bang.play.ValidationResult;
import com.chriscarr.bang.turn.TurnContext;

public class EquipGunValidator implements PlayValidator {
    @Override
    public ValidationResult validate(TurnContext ctx, PlayCommand cmd) {
        if (cmd.type() != PlayCommand.Type.PLAY_CARD) {
            return ValidationResult.fail("unsupported command type: " + cmd.type());
        }

        Card card = cmd.playedCard();
        if (card == null) {
            return ValidationResult.fail("no card to equip");
        }

        CardType type = card.getType() != null ? card.getType() : card.getName().defaultType();
        if (type != CardType.GUN) {
            return ValidationResult.fail("card is not a gun");
        }

        Player player = ctx.currentPlayer();
        if (!player.getHand().contains(card)) {
            return ValidationResult.fail("card not in hand");
        }
        return ValidationResult.OK;
    }
}
