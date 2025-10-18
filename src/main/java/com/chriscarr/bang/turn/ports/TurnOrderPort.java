package com.chriscarr.bang.turn.ports;

import com.chriscarr.bang.Player;

import java.util.List;

public interface TurnOrderPort {
    Player nextPlayer(Player player, List<Player> players);
}
