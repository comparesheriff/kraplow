package com.chriscarr.bang.gamestate;

import java.util.List;
import java.util.Optional;

public interface GameState {

    List<GameStatePlayer> getPlayers();

    String getCurrentName();

    boolean isGameOver();

    int getDeckSize();

    Optional<GameStateCard> discardTopCard();

    String timeout();

}
