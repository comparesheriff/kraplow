package com.chriscarr;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import junit.framework.TestCase;

public class PlayerTest extends TestCase {
  public void testPlayer() {
    Player player = new Player();
    Character setCharacter = Character.RANDOM;
    player.setCharacter(setCharacter);
    Role setRole = Role.SHERIFF;
    player.setRole(setRole);
    Hand setHand = new Hand();
    player.setHand(setHand);
    CardsInPlay setCardsInPlay = new CardsInPlay();
    player.setInPlay(setCardsInPlay);

    Character gotCharacter = player.getCharacter();
    Role gotRole = player.getRole();
    Hand gotHand = player.getHand();
    CardsInPlay gotCardsInPlay = player.getCardsInPlay();

    assertEquals(setCharacter, gotCharacter);
    assertEquals(setRole, gotRole);
    assertEquals(gotHand, setHand);
    assertEquals(gotCardsInPlay, setCardsInPlay);
  }
}
