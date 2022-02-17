package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.models.game.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Gun extends Card {
    public Gun(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public boolean play(Player currentPlayer, List<Player> players,
                        UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {
        if (currentPlayer.hasGun()) {
            discardPile.add(currentPlayer.removeGun());
        }
        if (Character.JOHNNY_KISCH.equals(currentPlayer.getCharacter())) {
            for (Player player : players) {
                if (player.getInPlay().getGunName().equals(this.getName())) {
                    Card gun = player.getInPlay().removeGun();
                    discardPile.add(gun);
                    userInterface.printInfo(currentPlayer.getName() + " plays a " + this.getName() + " and forces " + player.getName() + " to discard one from play.");
                }
            }
        }
        currentPlayer.setGun(this);
        return true;
    }
}
