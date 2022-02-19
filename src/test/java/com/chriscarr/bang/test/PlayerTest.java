package com.chriscarr.bang.test;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.InPlay;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.models.game.Character;
import com.chriscarr.bang.models.game.Role;
import junit.framework.TestCase;

public class PlayerTest extends TestCase {
    public void testPlayer() {
        Player player = new Player();
        Character setCharacter = Character.BELLE_STAR;
        player.setCharacter(setCharacter);
        Role setRole = Role.SHERIFF;
        player.setRole(setRole);
        Hand setHand = new Hand();
        player.setHand(setHand);
        InPlay setInPlay = new InPlay();
        player.setInPlay(setInPlay);

        Character gotCharacter = player.getCharacter();
        Role gotRole = player.getRole();
        Hand gotHand = player.getHand();
        InPlay gotInPlay = player.getInPlay();

        assertEquals(gotCharacter, setCharacter);
        assertEquals(gotRole, setRole);
        assertEquals(gotHand, setHand);
        assertEquals(gotInPlay, setInPlay);
    }
}
