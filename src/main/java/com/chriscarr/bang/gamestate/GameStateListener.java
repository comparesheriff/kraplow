package com.chriscarr.bang.gamestate;

import com.chriscarr.bang.turn.Turn;

public interface GameStateListener {
    void setTurn(Turn turn);
}
