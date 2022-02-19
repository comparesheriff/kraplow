package com.chriscarr.bang;

import com.chriscarr.bang.cards.BangDeck;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.gamestate.GameStateListener;
import com.chriscarr.bang.models.game.Character;
import com.chriscarr.bang.models.game.Role;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Setup {

    private Deck deck;
    private List<Player> players;

    public Setup(int countPlayers, UserInterface userInterface, GameStateListener gameStateListener) {
        new Setup(countPlayers, userInterface, gameStateListener, false, "random", "random");
    }

    public Setup(int countPlayers, UserInterface userInterface, GameStateListener gameStateListener, boolean sidestep, String pRole, String pChar) {
        deck = setupDeck(sidestep);
        deck.shuffle();
        DiscardPile discardPile = new DiscardPile();
        deck.setDiscard(discardPile);
        Role role = Role.getRole(pRole);
        Character character = Character.getCharacter(pChar);
        players = getPlayers(countPlayers, deck, sidestep, role, character);
        drawHands(players, deck);
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discardPile);
        turn.setPlayers(players);
        turn.setUserInterface(userInterface);
        gameStateListener.setTurn(turn);
        //This starts the game loop
        turn.setSheriff();
    }

    public static Deck setupDeck(boolean sidestep) {
        Deck deck = new Deck();
        List<Card> cards = BangDeck.makeDeck();
        for (Card card : cards) {
            deck.add(card);
        }
        if (sidestep) {
            List<Card> sidestepCards = BangDeck.makeSidestepDeck();
            for (Card card : sidestepCards) {
                deck.add(card);
            }
        }
        return deck;
    }

    public static List<Player> getPlayers(int countCharacters, Deck deck) {
        return getPlayers(countCharacters, deck, false, Role.RANDOM, Character.RANDOM);
    }

    public static List<Player> getPlayers(int countCharacters, Deck deck, boolean sidestep, Role pRole, Character pChar) {
        List<Player> players = new ArrayList<>();
        List<Character> characterList = Character.getCharacters(sidestep);

        Collections.shuffle(characterList);
        List<Role> newRoles = getNewRoles(countCharacters);
        Collections.shuffle(newRoles);
        for (int i = 0; i < countCharacters; i++) {
            Player player = new Player();

            if (i == 0 && !Character.RANDOM.equals(pChar) && characterList.contains(pChar)) { //refactor
                characterList.remove(pChar);
                characterList.add(0, pChar);
            }
            Character character = characterList.get(i);
            if (i == 0 && !Role.RANDOM.equals(pRole) && newRoles.contains(pRole)) {
                newRoles.remove(pRole);
                newRoles.add(0, pRole);
            }
            Role role = newRoles.get(i);

            player.setRole(role);
            player.setCharacter(character);
            int maxHealth = character.startingHealth(); //refactor
            if (player.isSheriff()) {
                maxHealth = maxHealth + 1;
            }
            player.setMaxHealth(maxHealth);

            Hand hand = new Hand();

            player.setHand(hand);

            player.setInPlay(new InPlay());

            players.add(player);
        }

        return players;
    }

    private static List<Role> getNewRoles(int countPlayers) {
        List<Role> roles = new ArrayList<>();
        roles.add(Role.SHERIFF);
        roles.add(Role.OUTLAW);
        roles.add(Role.OUTLAW);
        roles.add(Role.RENEGADE);
        if (countPlayers == 4) {
            return roles;
        }
        roles.add(Role.DEPUTY);
        if (countPlayers == 5) {
            return roles;
        }
        roles.add(Role.OUTLAW);
        if (countPlayers == 6) {
            return roles;
        }
        roles.add(Role.DEPUTY);
        if (countPlayers == 7) {
            return roles;
        }
        roles.add(Role.RENEGADE);
        return roles;
    }

    public static void drawHands(List<Player> players, Deck deck) {
        for (Player player : players) {
            int maxHealth = player.getMaxHealth();
            Hand hand = player.getHand();
            for (int i = 0; i < maxHealth; i++) {
                hand.add(deck.pull());
            }
        }
    }

    public static List<Player> getNormalPlayers(int countCharacters) { //refactor what is this function for?
        ArrayList<Player> players = new ArrayList<>();

        List<Role> roles = getNewRoles(countCharacters);
        Collections.shuffle(roles);
        for (int i = 0; i < countCharacters; i++) {
            Player player = new Player();

            Role role = roles.get(i);
            player.setRole(role);
            player.setCharacter(Character.RANDOM);
            int maxHealth = Character.RANDOM.startingHealth();
            if (player.isSheriff()) {
                maxHealth = maxHealth + 1;
            }
            player.setMaxHealth(maxHealth);

            player.setHand(new Hand());

            player.setInPlay(new InPlay());

            players.add(player);
        }

        return players;
    }
}