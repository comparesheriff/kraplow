package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Jail extends Card implements Playable {

    public Jail(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    @Override
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return targets(player, players).size() > 1;
    }

    @Override
    public boolean play(Player currentPlayer, List<Player> players,
                        UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        Player target = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        if (!(target instanceof CancelPlayer)) {
            if (Figure.JOHNNYKISCH.equals(currentPlayer.getAbility())) {
                for (Player player : players) {
                    int inPlayCount = player.getInPlay().count();
                    for (int inPlayIndex = 0; inPlayIndex < inPlayCount; inPlayIndex++) {
                        Card peeked = (Card) player.getInPlay().peek(inPlayIndex);
                        if (peeked.getName().equals(this.getName())) {
                            Card removed = (Card) player.getInPlay().remove(inPlayIndex);
                            discard.add(removed);
                            userInterface.printInfo(currentPlayer.getName() + " plays a " + this.getName() + " and forces " + player.getName() + " to discard one from play.");
                        }
                    }
                }
            }

            target.addInPlay(this);
            userInterface.printInfo(currentPlayer.getName() + " put " + target.getName() + " in jail.");
            return true;
        } else {
            currentPlayer.getHand().add(this);
            return false;
        }
    }

    @Override
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.getJailablePlayers(player, players);
    }

}
