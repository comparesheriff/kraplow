package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayResolver;
import com.chriscarr.bang.turn.TurnContext;

public final class PassResolver implements PlayResolver {
    @Override
    public void resolve(TurnContext ctx, PlayCommand cmd) {
        ctx.api().setDonePlaying(true);
    }
}
