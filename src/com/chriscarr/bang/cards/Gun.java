package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Gun extends Card {
    public Gun(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    public boolean play(Player currentPlayer, List<Player> players,
                        UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        if (currentPlayer.hasGun()) {
            discard.add(currentPlayer.removeGun());
        }
        if (Figure.JOHNNYKISCH.equals(currentPlayer.getAbility())) {
            for (Player player : players) {
                if (player.getInPlay().getGunName().equals(this.getName())) {
                    Object gun = player.getInPlay().removeGun();
                    discard.add(gun);
                    userInterface.printInfo(currentPlayer.getName() + " plays a " + this.getName() + " and forces " + player.getName() + " to discard one from play.");
                }
            }
        }
        currentPlayer.setGun(this);
        return true;
    }
}
