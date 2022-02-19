package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.DiscardPile;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Punch extends Card implements Playable {
    public Punch(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#canPlay(com.chriscarr.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return targets(player, players).size() > 1;
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#targets(com.chriscarr.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.getPlayersWithCards(Turn.getPlayersWithinRange(player, players, 1));
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#play(com.chriscarr.bang.Player, java.util.List, com.chriscarr.bang.UserInterface, com.chriscarr.bang.Deck, com.chriscarr.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {
        return this.shoot(currentPlayer, players, userInterface, deck, discardPile, turn, false);
    }
}
