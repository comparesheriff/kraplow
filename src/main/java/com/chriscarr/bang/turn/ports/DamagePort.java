package com.chriscarr.bang.turn.ports;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public interface DamagePort {
    void damagePlayer(Player currentPlayer, List<Player> players, Player target, int damage, Player damager, Deck deck, Discard discard, UserInterface ui);
}
