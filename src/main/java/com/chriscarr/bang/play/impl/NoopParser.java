package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.PlayerRef;
import com.chriscarr.bang.play.PlayCommand;
import com.chriscarr.bang.play.PlayParser;
import com.chriscarr.bang.turn.TurnContext;

public class NoopParser implements PlayParser {

    @Override
    public PlayCommand parse(TurnContext ctx) {
        // Context bewusst ignorieren: stabiler Dummy-Ref (0) für Smoke-Tests.
        return PlayCommand.pass(new PlayerRef(0));
    }
}
