package com.chriscarr.bang.turn.ports;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public interface TargetingPort {
    Player validChosenPlayer(Player player, List<Player> players, UserInterface ui);
}
