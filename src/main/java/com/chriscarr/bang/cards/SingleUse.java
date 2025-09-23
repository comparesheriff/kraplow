package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleUse extends Card implements Playable {

    protected boolean readyToPlay = false;

    public SingleUse(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        if (readyToPlay) {
            return true;
        }
        return !player.isInPlay(this.getName());
    }

    public boolean play(Player currentPlayer, List<Player> players,
                        UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        if (readyToPlay) {
            Logger logger = Logger.getLogger(SingleUse.class.getName());
            logger.log(Level.SEVERE, "Playing Ready To Play");
            return activate(currentPlayer, players, userInterface, deck, discard, turn);
        } else {
            currentPlayer.addInPlay(this);
            readyToPlay = false;
            return true;
        }
    }

    public void setReadyToPlay(boolean readyToPlay) {
        this.readyToPlay = readyToPlay;
    }

    public boolean activate(Player currentPlayer, List<Player> players,
                            UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        return false;
    }

    public void removeFromInPlay(Player currentPlayer) {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        for (int i = 0; i < currentCardsInPlay.size(); i++) {
            if (currentCardsInPlay.get(i) == this) {
                //REFACTORING clean this up
                currentCardsInPlay.remove(i);
            }
        }
    }

}
