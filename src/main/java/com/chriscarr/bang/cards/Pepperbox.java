package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.services.TargetingService;
import com.chriscarr.bang.turn.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Pepperbox extends SingleUse implements Playable {

    public Pepperbox(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    public List<Player> targets(Player player, List<Player> players) {
        return Turn.getPlayersWithCards(TargetingService.getPlayersWithinRange(player, players));
    }

    public boolean activate(
        Player currentPlayer,
        List<Player> players,
        UserInterface userInterface,
        Deck deck,
        Discard discard,
        Turn turn) {

        boolean result = this.shoot(currentPlayer, players, userInterface, deck, discard, turn, true);
        if (result) {
            removeFromInPlay(currentPlayer);
            discard.add(this);
        }
        return result;
    }
}
