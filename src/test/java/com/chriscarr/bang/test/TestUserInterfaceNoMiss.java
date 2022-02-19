package com.chriscarr.bang.test;

import com.chriscarr.bang.InPlay;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class TestUserInterfaceNoMiss extends TestUserInterface implements
        UserInterface {

    @Override
    public int askDiscard(Player player) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int chooseGeneralStoreCard(Player generalPlayer,
                                      List<Card> generalStoreCards) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int askPlayer(Player player, List<String> otherPlayers) {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public int askOthersCard(Player player, InPlay inPlay, boolean hasHand) {
        if (!inPlay.isEmpty()) {
            return 0;
        } else if (inPlay.hasGun()) {
            return -2;
        } else {
            return -1;
        }
    }

    @Override
    public List<Card> chooseTwoDiscardForLife(Player sidKetchum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int askPlay(Player player) {
        if (player.getHand().size() > 0) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public boolean chooseDiscard(Player player, Card card) {
        // TODO Auto-generated method stub
        return false;
    }

    public void printInfo(String info) {
        //do nothing
    }

    @Override
    public boolean chooseFromPlayer(Player player) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int chooseDrawCard(Player player, List<Card> cards) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int chooseCardToPutBack(Player player, List<Card> cards) {
        // TODO Auto-generated method stub
        return 0;
    }
}