package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Pepperbox extends SingleUse implements Playable {

    public Pepperbox(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public List<Player> targets(Player player, List<Player> players) {
        return Turn.getPlayersWithCards(Turn.getPlayersWithinRange(player, players, player.getGunRange()));
    }

    public boolean activate(Player currentPlayer, List<Player> players,
                            UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {

        boolean result = this.shoot(currentPlayer, players, userInterface, deck, discardPile, turn, true);
        if (result) {
            removeFromInPlay(currentPlayer);
            discardPile.add(this);
        }
        return result;
    }

}
