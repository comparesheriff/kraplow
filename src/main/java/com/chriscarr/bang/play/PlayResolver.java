package com.chriscarr.bang.play;

import com.chriscarr.bang.turn.TurnContext;

public interface PlayResolver {
    void resolve(TurnContext ctx, PlayCommand cmd);
}
