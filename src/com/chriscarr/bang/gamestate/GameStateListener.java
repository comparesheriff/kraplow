package com.chriscarr.bang.gamestate;

import com.chriscarr.bang.Turn;

public interface GameStateListener {
    void setTurn(Turn turn);
}
