package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.PlayerRefUtil;
import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayParser;
import com.chriscarr.bang.turn.TurnContext;

public final class PassOnlyParser implements PlayParser {
    @Override
    public PlayCommand parse(TurnContext ctx) {
        return PlayCommand.pass(PlayerRefUtil.of(ctx.currentPlayer()));
    }
}
