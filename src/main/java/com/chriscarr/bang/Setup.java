package com.chriscarr.bang;

import com.chriscarr.bang.cards.BangDeck;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.gamestate.GameStateListener;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Setup {

    private Deck deck;
    private List<Player> players;

    public Setup(int countPlayers, UserInterface userInterface, GameStateListener gameStateListener) {
        new Setup(countPlayers, userInterface, gameStateListener, false, Role.RANDOM, Character.RANDOM);
    }

    public Setup(int countPlayers, UserInterface userInterface, GameStateListener gameStateListener, boolean sidestep, Role pRole, Character pChar) {
        deck = setupDeck(sidestep);
        deck.shuffle();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        players = getPlayers(countPlayers, deck, sidestep, pRole, pChar);
        drawHands(players, deck);
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        turn.setPlayers(players);
        turn.setUserInterface(userInterface);
        gameStateListener.setTurn(turn);
        //This starts the game loop
        turn.setSheriff();
    }

    public Setup(List<String> players2, UserInterface userInterface,
                 GameStateListener gameStateListener) {
        // TODO Auto-generated constructor stub
    }

    public static Deck setupDeck(boolean sidestep) {
        Deck deck = new Deck();
        List<Card> cards = BangDeck.makeDeck();
        deck.addAll(cards);
        if (sidestep) {
            List<Card> sidestepCards = BangDeck.makeSidestepDeck();
            deck.addAll(sidestepCards);
        }
        return deck;
    }

    public static List<Player> getPlayers(int countCharacters, Deck deck) {
        return getPlayers(countCharacters, deck, false, Role.RANDOM, Character.RANDOM);
    }

    public static List<Player> getPlayers(int countCharacters, Deck deck, boolean sidestep, Role pRole, Character pChar) {
        List<Player> players = new ArrayList<>();
        List<Character> characterList = new ArrayList<>(Character.CHARACTERS);
        try {
            if (sidestep) {
                characterList.addAll(Character.CHARACTERSSIDESTEP);
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(Setup.class.getName());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        Collections.shuffle(characterList);
        List<Role> roles = getRoles(countCharacters);
        Collections.shuffle(roles);
        for (int i = 0; i < countCharacters; i++) {
            Player player = new Player();

            if (i == 0 && Character.RANDOM.equals(pChar) && characterList.contains(pChar)) {
                characterList.remove(pChar);
                characterList.addFirst(pChar);
            }
            Character character = characterList.get(i);
            if (i == 0 && !pRole.equals(Role.RANDOM) && roles.contains(pRole)) {
                roles.remove(pRole);
                roles.addFirst(pRole);
            }
            Role role = roles.get(i);

            player.setRole(role);
            player.setCharacter(character);
            int maxHealth = character.getStartingHealth();
            if (player.isSheriff()) {
                maxHealth = maxHealth + 1;
            }
            player.setMaxHealth(maxHealth);

            Hand hand = new Hand();

            player.setHand(hand);

            player.setInPlay(new CardsInPlay());

            players.add(player);
        }

        return players;
    }

    public static List<Role> getRoles(int countPlayers) {
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

    public static List<Player> getNormalPlayers(int countCharacters) {
        ArrayList<Player> players = new ArrayList<>();

        List<Role> roles = getRoles(countCharacters);
        Collections.shuffle(roles);
        for (int i = 0; i < countCharacters; i++) {
            Player player = new Player();

            Character character = Character.RANDOM;
            Role role = roles.get(i);
            player.setRole(role);
            player.setCharacter(character);
            int maxHealth = character.getStartingHealth();
            if (player.isSheriff()) {
                maxHealth = maxHealth + 1;
            }
            player.setMaxHealth(maxHealth);

            player.setHand(new Hand());

            player.setInPlay(new CardsInPlay());

            players.add(player);
        }

        return players;
    }
}
