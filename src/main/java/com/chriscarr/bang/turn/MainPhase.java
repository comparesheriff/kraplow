package com.chriscarr.bang.turn;

import com.chriscarr.bang.play.PlayLoop;
import com.chriscarr.bang.play.impl.PortsPlayLoop;

class MainPhase implements TurnPhase {
    private final PlayLoop loop;

    MainPhase() {
        this(new PortsPlayLoop());
    }

    MainPhase(PlayLoop loop) {
        this.loop = loop;
    }

    @Override
    public void carryOut(TurnContext ctx) {
        loop.run(ctx);
    }

}
