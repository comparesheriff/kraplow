package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class Whisky extends Card implements Playable {
    public Whisky(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#canPlay(main.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return player.getHand().size() >= 2;
    }

    @Override
    public List<Player> targets(Player player, List<Player> players) {
        List<Player> targets = new ArrayList<>();
        targets.add(player);
        return targets;
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#play(main.chriscarr.bang.Player, java.util.List, main.bang.UserInterface, main.bang.Deck, main.chriscarr.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        int cardDiscard = userInterface.askDiscard(currentPlayer);
        if (cardDiscard == -1) {
            return false;
        }
        Hand currentHand = currentPlayer.getHand();
        Card card = currentHand.remove(cardDiscard);
        discard.add(card);
        discard.add(this);
        if (!Turn.isMaxHealth(currentPlayer)) {
            currentPlayer.addHealth(1);
        }
        if (!Turn.isMaxHealth(currentPlayer)) {
            currentPlayer.addHealth(1);
        }
        return true;
    }
}
