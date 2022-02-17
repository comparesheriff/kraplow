package com.chriscarr.bang;

import com.chriscarr.bang.cards.BangDeck;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.gamestate.GameStateListener;
import com.chriscarr.bang.models.game.Role;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        players = getPlayers(countPlayers, deck, sidestep, pRole, pChar);
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

    public Setup(List<String> players2, UserInterface userInterface,
                 GameStateListener gameStateListener) {
        // TODO Auto-generated constructor stub
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
        return getPlayers(countCharacters, deck, false, "random", "random");
    }

    public static List<Player> getPlayers(int countCharacters, Deck deck, boolean sidestep, String pRole, String pChar) {
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<String> characterList = new ArrayList<>(Arrays.asList(Figure.CHARACTERS));
        try {
            if (sidestep) {
                characterList.addAll(Arrays.asList(Figure.CHARACTERSSIDESTEP));
            }
        } catch (Exception e) {
            Logger logger = Logger.getLogger(Setup.class.getName());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        Collections.shuffle(characterList);
        List<Role> newRoles = getNewRoles(countCharacters);
        Collections.shuffle(newRoles);
        for (int i = 0; i < countCharacters; i++) {
            Player player = new Player();

            Figure figure = new Figure();
            if (i == 0 && !pChar.equals("random") && characterList.contains(pChar)) {
                characterList.remove(pChar);
                characterList.add(0, pChar);
            }
            figure.setName(characterList.get(i));
            Role newRole = Role.getRole(pRole);
            if (i == 0 && !Role.RANDOM.equals(newRole) && newRoles.contains(newRole)) {
                newRoles.remove(newRole);
                newRoles.remove(newRole);
            }
            Role role = newRoles.get(i);

            player.setRole(role);
            player.setFigure(figure);
            int maxHealth = Figure.getStartingHealth(figure.getName());
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

    public static List<Player> getNormalPlayers(int countCharacters) {
        ArrayList<Player> players = new ArrayList<>();

        List<Role> roles = getNewRoles(countCharacters);
        Collections.shuffle(roles);
        for (int i = 0; i < countCharacters; i++) {
            Player player = new Player();

            Figure figure = new Figure();
            figure.setName("Average Joe");
            Role role = roles.get(i);
            player.setRole(role);
            player.setFigure(figure);
            int maxHealth = Figure.getStartingHealth(figure.getName());
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
