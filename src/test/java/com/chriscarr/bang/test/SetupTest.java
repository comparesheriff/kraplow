package com.chriscarr.bang.test;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Setup;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.models.game.Role;
import junit.framework.TestCase;

import java.util.List;

public class SetupTest extends TestCase {
    public void testDeck() {
        Deck deck = Setup.setupDeck(false);
        Card pulled = null;
        for (int i = 0; i < 80; i++) {
            pulled = deck.pull();
        }
        assertEquals(pulled.getName(), Card.CARDBARREL);
        assertEquals(pulled.getSuit(), Card.SPADES);
        assertEquals(pulled.getValue(), Card.VALUEQ);
        assertEquals(pulled.getType(), Card.TYPEITEM);
    }

    public void testSetupPlayers() {
        Deck deck = Setup.setupDeck(false);
        List<Player> players = Setup.getPlayers(7, deck);
        assertEquals(players.size(), 7);
    }

    public void testSetupPlayersHealth() {
        Deck deck = Setup.setupDeck(false);
        List<Player> players = Setup.getPlayers(7, deck);
        int health = players.get(6).getHealth();
        assertTrue(health == 3 || health == 4 || health == 5);
    }

    public void testSetupPlayersRoles() {
        Deck deck = Setup.setupDeck(false);
        List<Player> players = Setup.getPlayers(7, deck);
        int sheriff = 0;
        int outlaw = 0;
        int deputy = 0;
        int renegade = 0;
        for (Player player : players) {
            if (player.isSheriff()) {
                sheriff++;
            }
            if (Role.OUTLAW.equals(player.getRole())) {
                outlaw++;
            }
            if (Role.DEPUTY.equals(player.getRole())) {
                deputy++;
            }
            if (Role.RENEGADE.equals(player.getRole())) {
                renegade++;
            }
        }
        assertTrue(sheriff == 1 && outlaw == 3 && deputy == 2 && renegade == 1);
    }

    public void testSetupPlayersRoles4() {
        Deck deck = Setup.setupDeck(false);
        List<Player> players = Setup.getPlayers(4, deck);
        int sheriff = 0;
        int outlaw = 0;
        int deputy = 0;
        int renegade = 0;
        for (Player player : players) {
            if (player.isSheriff()) {
                sheriff++;
            }
            if (Role.OUTLAW.equals(player.getRole())) {
                outlaw++;
            }
            if (Role.DEPUTY.equals(player.getRole())) {
                deputy++;
            }
            if (Role.RENEGADE.equals(player.getRole())) {
                renegade++;
            }
        }
        assertTrue(sheriff == 1 && outlaw == 2 && deputy == 0 && renegade == 1);
    }

    public void testSetupPlayersRoles5() {
        Deck deck = Setup.setupDeck(false);
        List<Player> players = Setup.getPlayers(5, deck);
        int sheriff = 0;
        int outlaw = 0;
        int deputy = 0;
        int renegade = 0;
        for (Player player : players) {
            if (player.isSheriff()) {
                sheriff++;
            }
            if (Role.OUTLAW.equals(player.getRole())) {
                outlaw++;
            }
            if (Role.DEPUTY.equals(player.getRole())) {
                deputy++;
            }
            if (Role.RENEGADE.equals(player.getRole())) {
                renegade++;
            }
        }
        assertTrue(sheriff == 1 && outlaw == 2 && deputy == 1 && renegade == 1);
    }

    public void testSetupPlayersRoles6() {
        Deck deck = Setup.setupDeck(false);
        List<Player> players = Setup.getPlayers(6, deck);
        int sheriff = 0;
        int outlaw = 0;
        int deputy = 0;
        int renegade = 0;
        for (Player player : players) {
            if (player.isSheriff()) {
                sheriff++;
            }
            if (Role.OUTLAW.equals(player.getRole())) {
                outlaw++;
            }
            if (Role.DEPUTY.equals(player.getRole())) {
                deputy++;
            }
            if (Role.RENEGADE.equals(player.getRole())) {
                renegade++;
            }
        }
        assertTrue(sheriff == 1 && outlaw == 3 && deputy == 1 && renegade == 1);
    }

    public void testHands() {
        Deck deck = Setup.setupDeck(false);
        List<Player> players = Setup.getPlayers(4, deck);
        Setup.drawHands(players, deck);
        int maxHealth = players.get(3).getMaxHealth();
        Hand hand = players.get(3).getHand();
        assertTrue(maxHealth != 0);
        assertEquals(hand.size(), maxHealth);
        assertEquals(maxHealth, players.get(3).getHealth());
    }
}