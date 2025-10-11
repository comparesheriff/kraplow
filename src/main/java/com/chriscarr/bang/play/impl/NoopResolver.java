package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayResolver;
import com.chriscarr.bang.turn.TurnContext;

public class NoopResolver implements PlayResolver {
    @Override
    public void resolve(TurnContext ctx, PlayCommand cmd) {
        //no-op
    }
}
