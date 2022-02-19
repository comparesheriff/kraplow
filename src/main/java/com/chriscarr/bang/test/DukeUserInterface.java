package com.chriscarr.bang.test;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;

import java.util.List;

public class DukeUserInterface extends TestUserInterface {
    boolean returnedInvalidLess = false;
    boolean returnedInvalidGreat = false;

    public int chooseDrawCard(Player player, List<Card> cards) {
        if (!returnedInvalidLess) {
            returnedInvalidLess = true;
            return -1;
        } else if (!returnedInvalidGreat) {
            returnedInvalidGreat = true;
            return 3;
        } else {
            return 0;
        }

    }
}
