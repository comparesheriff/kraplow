package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public interface Playable {

    boolean canPlay(Player player, List<Player> players,
                    int bangsPlayed);

    List<Player> targets(Player player, List<Player> players);

    boolean play(Player currentPlayer, List<Player> players,
                 UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn);

}