package com.chriscarr.bang.play;

import com.chriscarr.bang.turn.TurnContext;

public interface PlayParser {
    PlayCommand parse(TurnContext ctx);
}
