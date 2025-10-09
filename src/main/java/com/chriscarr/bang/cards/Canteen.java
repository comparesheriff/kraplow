package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.turn.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Canteen extends SingleUse implements Playable {

    public Canteen(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    public boolean activate(
        Player currentPlayer,
        List<Player> players,
        UserInterface userInterface,
        Deck deck,
        Discard discard,
        Turn turn) {

        if (!Turn.isMaxHealth(currentPlayer)) {
            currentPlayer.addHealth(1);
        }
        removeFromInPlay(currentPlayer);
        discard.add(this);
        return true;
    }
}
