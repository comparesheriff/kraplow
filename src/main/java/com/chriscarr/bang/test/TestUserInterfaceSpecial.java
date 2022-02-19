package com.chriscarr.bang.test;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;

import java.util.ArrayList;
import java.util.List;

public class TestUserInterfaceSpecial extends TestUserInterface {


    @Override
    public int askPlay(Player player) {
        if (player.getHandSize() == 2) {
            return 2;
        } else {
            return -1;
        }
    }

    @Override
    public List<Card> chooseTwoDiscardForLife(Player player) {
        List<Card> cards = new ArrayList<>();
        Hand hand = player.getHand();
        cards.add(hand.get(0));
        cards.add(hand.get(1));
        return cards;
    }
}
