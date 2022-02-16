package com.chriscarr.bang.test;

import com.chriscarr.bang.Figure;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.InPlay;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.models.Role;
import junit.framework.TestCase;

public class PlayerTest extends TestCase {
    public void testPlayer() {
        Player player = new Player();
        Figure setFigure = new Figure();
        player.setFigure(setFigure);
        Role setRole = Role.SHERIFF;
        player.setRole(setRole);
        Hand setHand = new Hand();
        player.setHand(setHand);
        InPlay setInPlay = new InPlay();
        player.setInPlay(setInPlay);

        Figure gotFigure = player.getFigure();
        Role gotRole = player.getRole();
        Hand gotHand = player.getHand();
        InPlay gotInPlay = player.getInPlay();

        assertEquals(gotFigure, setFigure);
        assertEquals(gotRole, setRole);
        assertEquals(gotHand, setHand);
        assertEquals(gotInPlay, setInPlay);
    }
}
