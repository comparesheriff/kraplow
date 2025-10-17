package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.PlayerRefUtil;
import com.chriscarr.bang.play.CardSelector;
import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayParser;
import com.chriscarr.bang.turn.TurnContext;

import java.util.Collections;
import java.util.Map;

public class EquipGunParser implements PlayParser {
    private final CardSelector cardSelector;

    public EquipGunParser() {
        this(new FirstItemSelector());
    }

    public EquipGunParser(CardSelector cardSelector) {
        this.cardSelector = cardSelector;
    }

    @Override
    public PlayCommand parse(TurnContext ctx) {
        Player player = ctx.currentPlayer();
        int selectedIndex = cardSelector.chooseEquipFromHand(player, ctx);
        if (selectedIndex >= 0) {
            return new PlayCommand(
                PlayCommand.Type.PLAY_CARD,
                PlayerRefUtil.of(player),
                player.getHand().get(selectedIndex),
                Collections.emptyList(),
                Map.of("from", "hand", "index", Integer.toString(selectedIndex)));
        }
        return PlayCommand.pass(PlayerRefUtil.of(player));
    }


}
