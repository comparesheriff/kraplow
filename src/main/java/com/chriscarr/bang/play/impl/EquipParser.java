package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.PlayerRefUtil;
import com.chriscarr.bang.play.CardSelector;
import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayParser;
import com.chriscarr.bang.turn.TurnContext;

import java.util.List;
import java.util.Map;

public final class EquipParser implements PlayParser {
    private final CardSelector selector;

    public EquipParser() {this(new FirstItemSelector());}

    public EquipParser(CardSelector selector) {this.selector = selector;}

    @Override
    public PlayCommand parse(TurnContext ctx) {
        Player player = ctx.currentPlayer();
        int i = selector.chooseEquipFromHand(player, ctx);
        if (i >= 0) {
            return new PlayCommand(PlayCommand.Type.PLAY_CARD,
                PlayerRefUtil.of(player),
                player.getHand().get(i),
                List.of(),
                Map.of("from", "hand", "index", Integer.toString(i)));
        }
        return PlayCommand.pass(PlayerRefUtil.of(player));
    }
}
