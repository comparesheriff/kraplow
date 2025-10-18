package com.chriscarr.bang.turn.ports;

import com.chriscarr.bang.turn.TurnContext;

public interface PlayerControlPort {
    boolean checkDonePlaying();

    void setDonePlaying(boolean donePlaying);

    void play(TurnContext ctx);
}
