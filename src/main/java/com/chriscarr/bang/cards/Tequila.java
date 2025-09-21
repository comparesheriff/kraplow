package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Tequila extends Card implements Playable {
    public Tequila(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#canPlay(main.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return player.getHand().size() >= 2;
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#targets(main.chriscarr.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return players;
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#play(main.bang.Player, java.util.List, main.chriscarr.bang.UserInterface, main.bang.Deck, main.chriscarr.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        //Choose card to discard
        int cardDiscard = userInterface.askDiscard(currentPlayer);
        if (cardDiscard == -1) {
            return false;
        }
        //Choose player to give a draw to
        Player targetPlayer = Turn.getValidChosenPlayer(currentPlayer, players, userInterface);
        //discard the card
        Hand currentHand = currentPlayer.getHand();
        Card card = currentHand.remove(cardDiscard);
        discard.add(card);
        discard.add(this);
        //Draw and give to that player
        if (!Turn.isMaxHealth(targetPlayer)) {
            targetPlayer.addHealth(1);
        }
        return true;
    }
}
