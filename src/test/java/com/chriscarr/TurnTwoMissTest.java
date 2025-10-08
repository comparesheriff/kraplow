package com.chriscarr;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.UserInterface;
import junit.framework.TestCase;

import java.util.List;

public class TurnTwoMissTest extends TestCase {

    static class NullTwoMissUi extends TestUserInterface implements UserInterface {
        @Override
        public List<Card> respondTwoMiss(Player player) {
            return null;
        }
    }

    public void testTurnTwoMiss() {
        var ui = new NullTwoMissUi();
        assertTrue(Turn.validRespondTwoMiss(new Player(), ui).isEmpty());
    }
}
