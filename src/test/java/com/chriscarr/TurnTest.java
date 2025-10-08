package com.chriscarr;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.*;
import com.chriscarr.bang.services.TargetingService;
import com.chriscarr.bang.userinterface.UserInterface;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class TurnTest extends TestCase {
    public void testTurn() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setPlayers(players);
        turn.setUserInterface(new TestUserInterface());
        turn.setSheriffManualTest();
        Player sheriff = turn.getPlayersTurn();
        assertEquals(Role.SHERIFF, sheriff.getRole());
    }

  /*public void testTurnNext(){
  	Turn turn = new Turn();
  	List<Player> players = Setup.getNormalPlayers(4);
  	turn.setPlayers(players);
  	turn.setDeck(Setup.setupDeck(false));
  	turn.setDiscard(new Discard());
  	turn.setSheriffManualTest();
  	turn.setDeck(Setup.setupDeck(false));
  	turn.setDiscard(new Discard());
  	UserInterface testUserInterface = new TestUserInterface();
  	turn.setUserInterface(testUserInterface);
  	turn.nextTurn();
  	Player notSheriff =  turn.getPlayersTurn();
  	assertFalse(notSheriff.getRole() == Role.SHERIFF);
  }*/

  /*public void testTurnLoop(){
  	Turn turn = new Turn();
  	List<Player> players = Setup.getNormalPlayers(4);
  	turn.setPlayers(players);
  	turn.setDeck(Setup.setupDeck(false));
  	turn.setDiscard(new Discard());
  	turn.setSheriffManualTest();
  	turn.nextTurn();
  	turn.nextTurn();
  	turn.nextTurn();
  	turn.nextTurn();
  	Player sheriff =  turn.getPlayersTurn();
  	assertEquals(sheriff.getRole(), Role.SHERIFF);
  }*/

  /*public void testBangPlayed(){
  	Turn turn = new Turn();
  	List<Player> players = Setup.getNormalPlayers(4);
  	turn.setPlayers(players);
  	turn.setDeck(Setup.setupDeck(false));
  	turn.setDiscard(new Discard());
  	turn.setSheriffManualTest();
  	assertFalse(turn.isBangPlayed());
  	turn.setBangPlayed(true);
  	assertTrue(turn.isBangPlayed());
  	turn.nextTurn();
  	assertFalse(turn.isBangPlayed());
  }*/

    public void testDrawCards() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();

        Player player = turn.getCurrentPlayer();
        turn.drawCards(player, Setup.setupDeck(false));
        assertEquals(2, turn.getCurrentPlayer().getHand().size());
        turn.drawCards(player, Setup.setupDeck(false));
        assertEquals(4, turn.getCurrentPlayer().getHand().size());
    }

    public void testDiscardCards() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Deck deck = Setup.setupDeck(false);
        Player player = turn.getCurrentPlayer();
        turn.drawCards(player, deck);
        turn.drawCards(player, deck);
        turn.drawCards(player, deck);
        int health = player.getHealth();
        UserInterface testUserInterface = new TestUserInterface();
        turn.setUserInterface(testUserInterface);
        assertTrue(player.getHand().size() != health);
        turn.discard(player);
        assertEquals(player.getHand().size(), health);
    }

  /*public void testPlay(){
  	Turn turn = new Turn();
  	List<Player> players = Setup.getNormalPlayers(4);
  	turn.setPlayers(players);
  	turn.setDeck(Setup.setupDeck(false));
  	turn.setDiscard(new Discard());
  	turn.setSheriffManualTest();
  	UserInterface testUserInterface = new TestUserInterface();
  	turn.setUserInterface(testUserInterface);
  	assertFalse(turn.isDonePlaying());
  	turn.play();
  	assertTrue(turn.isDonePlaying());
  	turn.nextTurn();
  	assertFalse(turn.isDonePlaying());
  }*/

    public void testPlayItem() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Card(CardName.BARREL, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        UserInterface testUserInterface = new TestPlayOneUserInterface();
        turn.setUserInterface(testUserInterface);
        turn.play();
        assertTrue(sheriff.getCardsInPlay().hasItem(CardName.BARREL));
        assertFalse(sheriff.getCardsInPlay().hasItem(CardName.SCOPE));
    }

    public void testPlayItemTwice() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Card(CardName.BARREL, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        sheriff
            .getHand()
            .add(new Card(CardName.BARREL, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        UserInterface testUserInterface = new TestPlayOneUserInterface();
        turn.setUserInterface(testUserInterface);
        turn.play();
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testPlayItemJail() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Jail(CardName.JAIL, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer2();
        turn.setUserInterface(testUserInterface);
        turn.play();
        int otherPlayer = 0;
        if (players.getFirst().equals(sheriff)) {
            otherPlayer = 1;
        }
        Player jailedPlayer = players.get(otherPlayer);

        CardsInPlay otherCardsInPlay = jailedPlayer.getCardsInPlay();
        assertTrue(otherCardsInPlay.hasItem(CardName.JAIL));
    }

    public void testPlayItemAlreadyInJail() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Jail(CardName.JAIL, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        for (Player player : players) {
            player
                .getCardsInPlay()
                .add(new Jail(CardName.JAIL, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        }
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testPlayBeer() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Beer(CardName.BEER, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        int startingHealth = sheriff.getHealth();
        sheriff.setHealth(sheriff.getHealth() - 1);
        assertEquals(startingHealth - 1, sheriff.getHealth());
        turn.setDiscard(new Discard());
        turn.play();
        assertEquals(startingHealth, sheriff.getHealth());
    }

    public void testPlayBeerTwoPlayers() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        players.remove(sheriff);
        players.removeFirst();
        players.removeFirst();
        players.add(sheriff);
        sheriff.getHand().add(new Beer(CardName.BEER, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        int startingHealth = sheriff.getHealth();
        sheriff.setHealth(sheriff.getHealth() - 1);
        assertEquals(startingHealth - 1, sheriff.getHealth());
        turn.setDiscard(new Discard());
        turn.play();
        assertEquals(startingHealth - 1, sheriff.getHealth());
    }

    public void testPlayBeerMaxHealth() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Beer(CardName.BEER, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        int startingHealth = sheriff.getHealth();
        sheriff.setHealth(sheriff.getHealth());
        assertEquals(startingHealth, sheriff.getHealth());
        turn.setDiscard(new Discard());
        turn.play();
        assertEquals(startingHealth, sheriff.getHealth());
    }

    public void testPlayStagecoach() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Stagecoach(CardName.STAGECOACH, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        assertEquals(1, sheriff.getHand().size());
        UserInterface testUserInterface = new TestPlayOneUserInterface();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertEquals(2, sheriff.getHand().size());
    }

    public void testPlayWellsFargo() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new WellsFargo(CardName.WELLS_FARGO, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        assertEquals(1, sheriff.getHand().size());
        UserInterface testUserInterface = new TestPlayOneUserInterface();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertEquals(3, sheriff.getHand().size());
    }

    public void testPlaySaloon() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Saloon(CardName.SALOON, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        players.get(0).setHealth(players.get(0).getHealth() - 1);
        players.get(1).setHealth(players.get(1).getHealth() - 1);
        players.get(2).setHealth(players.get(2).getHealth() - 1);
        players.get(3).setHealth(players.get(3).getHealth() - 1);
        turn.setDiscard(new Discard());
        turn.play();
        assertEquals(players.get(0).getHealth(), players.get(0).getMaxHealth());
        assertEquals(players.get(1).getHealth(), players.get(1).getMaxHealth());
        assertEquals(players.get(2).getHealth(), players.get(2).getMaxHealth());
        assertEquals(players.get(3).getHealth(), players.get(3).getMaxHealth());
    }

    public void testPlaySaloonFullUp() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Saloon(CardName.SALOON, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.play();
        assertEquals(players.get(0).getHealth(), players.get(0).getMaxHealth());
        assertEquals(players.get(1).getHealth(), players.get(1).getMaxHealth());
        assertEquals(players.get(2).getHealth(), players.get(2).getMaxHealth());
        assertEquals(players.get(3).getHealth(), players.get(3).getMaxHealth());
    }

    public void testPlayIndians() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Indians(CardName.INDIANS, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>(players);
        others.remove(turn.getCurrentPlayer());

        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth() - 1);
        }
    }

    public void testPlayIndiansCalamityJanetMissedBack() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Indians(CardName.INDIANS, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayerBangBack();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            others.add(otherPlayer);
            Character character = Character.CALAMITYJANET;
            otherPlayer.setCharacter(character);
            otherPlayer
                .getHand()
                .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        }
        others.remove(turn.getCurrentPlayer());

        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth());
            assertEquals(0, otherPlayer.getHand().size());
        }
    }

    public void testPlayIndiansCalamityJanetBangBack() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Indians(CardName.INDIANS, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayerBangBack();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            others.add(otherPlayer);
            otherPlayer.setCharacter(Character.CALAMITYJANET);
            otherPlayer
                .getHand()
                .add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        }
        others.remove(turn.getCurrentPlayer());

        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth());
            assertEquals(0, otherPlayer.getHand().size());
        }
    }

    public void testPlayIndiansBangBack() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Indians(CardName.INDIANS, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayerBangBack();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            others.add(otherPlayer);
            otherPlayer
                .getHand()
                .add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        }
        others.remove(turn.getCurrentPlayer());

        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth());
            assertEquals(0, otherPlayer.getHand().size());
        }
    }

    public void testPlayGatling() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Gatling(CardName.GATLING, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>(players);
        others.remove(turn.getCurrentPlayer());
        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth() - 1);
        }
    }

    public void testPlayGatlingBarrelSave() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        Deck deck = new Deck();
        deck.add(new Gatling(CardName.GATLING, CardSuit.HEARTS, CardValue.QUEEN, CardType.PLAY));
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Gatling(CardName.GATLING, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            otherPlayer
                .getCardsInPlay()
                .add(new Card(CardName.BARREL, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
            others.add(otherPlayer);
        }
        others.remove(turn.getCurrentPlayer());
        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth());
        }
    }

    public void testPlayGatlingMiss() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Gatling(CardName.GATLING, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayerBangBack();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            otherPlayer
                .getHand()
                .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
            others.add(otherPlayer);
        }
        others.remove(turn.getCurrentPlayer());
        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth());
            assertEquals(0, otherPlayer.getHand().size());
        }
    }

    public void testPlayGatlingMissJanet() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Gatling(CardName.GATLING, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayerBangBack();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            otherPlayer.setCharacter(Character.CALAMITYJANET);
            otherPlayer
                .getHand()
                .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
            others.add(otherPlayer);
        }
        others.remove(turn.getCurrentPlayer());
        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth());
            assertEquals(0, otherPlayer.getHand().size());
        }
    }

    public void testPlayGatlingMissJanetBang() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Gatling(CardName.GATLING, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayerBangBack();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            Character otherCharacter = Character.CALAMITYJANET;
            otherPlayer.setCharacter(otherCharacter);
            otherPlayer
                .getHand()
                .add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
            others.add(otherPlayer);
        }
        others.remove(turn.getCurrentPlayer());
        turn.play();
        for (Player otherPlayer : others) {
            assertEquals(otherPlayer.getHealth(), otherPlayer.getMaxHealth());
            assertEquals(0, otherPlayer.getHand().size());
        }
    }

    public void testPlayGeneralStore() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(
                new GeneralStore(
                    CardName.GENERAL_STORE, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertEquals(1, players.get(0).getHand().size());
        assertEquals(1, players.get(1).getHand().size());
        assertEquals(1, players.get(2).getHand().size());
        assertEquals(1, players.get(3).getHand().size());
    }

    public void testPlayDuelWinFirst() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Duel(CardName.DUEL, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestPlayOneUserInterfaceChoosePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        int otherPlayer = 0;
        if (players.getFirst().equals(sheriff)) {
            otherPlayer = 1;
        }
        Player enemy = players.get(otherPlayer);
        assertEquals(enemy.getMaxHealth() - 1, enemy.getHealth());
    }

    public void testPlayDuelLoseFirst() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Duel(CardName.DUEL, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        for (Player player : players) {
            player.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        }
        UserInterface testUserInterface = new TestUserInterfaceBangBackOnce();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertEquals(sheriff.getMaxHealth() - 1, sheriff.getHealth());
    }

    public void testPlayDuelLoseFirstJanet() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Duel(CardName.DUEL, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        for (Player player : players) {
            Character character = Character.CALAMITYJANET;
            player.setCharacter(character);
            player
                .getHand()
                .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        }
        sheriff.setCharacter(Character.RANDOM);
        UserInterface testUserInterface = new TestUserInterfaceBangBackOnce();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertEquals(sheriff.getMaxHealth() - 1, sheriff.getHealth());
    }

    public void testPlayDuelWinSecond() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Duel(CardName.DUEL, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwice();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        int otherPlayer = 0;
        if (players.getFirst().equals(sheriff)) {
            otherPlayer = 1;
        }
        Player enemy = players.get(otherPlayer);
        enemy.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        assertEquals(1, enemy.getHand().size());
        assertEquals(2, sheriff.getHand().size());
        turn.play();
        assertEquals(0, enemy.getHand().size());
        assertEquals(0, sheriff.getHand().size());
        assertEquals(enemy.getMaxHealth() - 1, enemy.getHealth());
    }

    public void testPlayDuelWinSecondJanet() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Duel(CardName.DUEL, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwice();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        int otherPlayer = 0;
        if (players.getFirst().equals(sheriff)) {
            otherPlayer = 1;
        }
        Player enemy = players.get(otherPlayer);
        enemy.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        Character character = Character.CALAMITYJANET;
        sheriff.setCharacter(character);
        assertEquals(1, enemy.getHand().size());
        assertEquals(2, sheriff.getHand().size());
        turn.play();
        assertEquals(0, enemy.getHand().size());
        assertEquals(0, sheriff.getHand().size());
        assertEquals(enemy.getMaxHealth() - 1, enemy.getHealth());
    }

    public void testCatbalu() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new CatBalou(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceCatBalu();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        int otherPlayer = 0;
        if (players.getFirst().equals(sheriff)) {
            otherPlayer = 1;
        }
        Player enemy = players.get(otherPlayer);
        enemy.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        assertEquals(1, enemy.getHand().size());
        turn.play();
        assertEquals(0, enemy.getHand().size());
    }

    public void testPanic() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Panic(CardName.PANIC, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceAskPlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        enemy.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        assertEquals(1, enemy.getHand().size());
        turn.play();
        assertEquals(0, enemy.getHand().size());
        assertEquals(1, sheriff.getHand().size());
    }

    public void testBangHit() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth() - 1);
    }

    public void testBangMiss() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwicePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        enemy
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth());
        assertEquals(0, enemy.getHand().size());
    }

    public void testTwoBangFail() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);
        turn.play();
        assertEquals(1, sheriff.getHand().size());
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testTwoBangVolcanic() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .setGun(new Gun(CardName.VOLCANIC, CardSuit.CLUBS, CardValue.QUEEN, CardType.GUN));
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);
        turn.play();
        assertEquals(1, sheriff.getHand().size());
        turn.play();
        assertEquals(0, sheriff.getHand().size());
    }

    public void testBangDistanceWithoutMustang() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        assertEquals(3, TargetingService.getPlayersWithinRange(sheriff, players).size());
    }

    public void testBangDistanceWithMustang() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        for (Player player : players) {
            player
                .getCardsInPlay()
                .add(new Card(CardName.MUSTANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        }
        assertEquals(1, TargetingService.getPlayersWithinRange(sheriff, players).size());
    }

    public void testBangDistanceWithScope() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .add(new Card(CardName.SCOPE, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        assertEquals(4, TargetingService.getPlayersWithinRange(sheriff, players).size());
    }

    public void testPanicDistance() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        assertEquals(3, TargetingService.getPlayersWithinRange(sheriff, players).size());
    }

    public void testBangDistance() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .setGun(new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        assertEquals(4, TargetingService.getPlayersWithinRange(sheriff, players).size());
    }

    public void testBangDistanceRev() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(7);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .setGun(new Gun(CardName.REV_CARBINE, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        assertEquals(7, TargetingService.getPlayersWithinRange(sheriff, players).size());
    }

    public void testDynamiteTwo() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.TWO, CardType.ITEM);
        assertTrue(Card.isExplode(drawnCard));
    }

    public void testDynamiteNine() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        assertTrue(Card.isExplode(drawnCard));
    }

    public void testDynamiteTen() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.TEN, CardType.ITEM);
        assertFalse(Card.isExplode(drawnCard));
    }

    public void testDynamiteNineClubs() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.NINE, CardType.ITEM);
        assertFalse(Card.isExplode(drawnCard));
    }

    public void testPassDynamite() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(7);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .add(new Card(CardName.DYNAMITE, CardSuit.CLUBS, CardValue.NINE, CardType.ITEM));
        assertTrue(sheriff.getCardsInPlay().hasItem(CardName.DYNAMITE));
        turn.setUserInterface(new TestUserInterface());
        turn.passDynamite();
        assertFalse(sheriff.getCardsInPlay().hasItem(CardName.DYNAMITE));
        Player nextPlayer = Turn.getNextPlayer(turn.getCurrentPlayer(), players);
        assertTrue(nextPlayer.getCardsInPlay().hasItem(CardName.DYNAMITE));
    }

    public void testPassDynamiteNoDynamite() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(7);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        turn.setUserInterface(new TestUserInterface());
        turn.passDynamite();
        Player nextPlayer = Turn.getNextPlayer(turn.getCurrentPlayer(), players);
        assertFalse(nextPlayer.getCardsInPlay().hasItem(CardName.DYNAMITE));
    }

    public void testDraw() {
        Deck deck = new Deck();
        Card card = new Card(CardName.DYNAMITE, CardSuit.CLUBS, CardValue.NINE, CardType.ITEM);
        deck.add(card);
        Discard discard = new Discard();
        Player player = new Player();
        player.setCharacter(Character.RANDOM);
        Card drawnCard = Turn.draw(player, deck, discard, new TestUserInterface());
        assertEquals(drawnCard, card);
        assertEquals(discard.getLast(), card);
    }

    public void testDynamiteTurnExplode() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        turn.setUserInterface(new TestUserInterface());
        sheriff
            .getCardsInPlay()
            .add(new Card(CardName.DYNAMITE, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        assertTrue(turn.isDynamiteExplode());
    }

    public void testNoDynamite() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setSheriffManualTest();
        turn.setUserInterface(new TestUserInterface());
        assertFalse(turn.isDynamiteExplode());
    }

    public void testDynamiteTurnNotExplode() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        turn.setUserInterface(new TestUserInterface());
        sheriff
            .getCardsInPlay()
            .add(new Card(CardName.DYNAMITE, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        assertFalse(turn.isDynamiteExplode());
    }

    public void testInJail() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setSheriffManualTest();
        turn.setUserInterface(new TestUserInterface());
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .add(new Jail(CardName.JAIL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        assertTrue(turn.isInJail());
    }

    public void testGetJailablePlayers() {
        Player player = new Player();
        player.setRole(Role.OUTLAW);
        player.setInPlay(new CardsInPlay());
        Player sheriff = new Player();
        sheriff.setInPlay(new CardsInPlay());
        sheriff.setRole(Role.SHERIFF);
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(sheriff);
        List<Player> jailable = Turn.getJailablePlayers(player, players);
        assertEquals(1, jailable.size());
    }

    public void testOutOfJail() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setUserInterface(new TestUserInterface());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .add(new Jail(CardName.JAIL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        assertFalse(turn.isInJail());
    }

    public void testOutNoJail() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        turn.setPlayers(Setup.getNormalPlayers(4));
        turn.setSheriffManualTest();
        assertFalse(turn.isInJail());
    }

    public void testNoBarrel() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        turn.setPlayers(Setup.getNormalPlayers(4));
        turn.setSheriffManualTest();
        Player player = new Player();
        player.setInPlay(new CardsInPlay());
        player.setCharacter(Character.RANDOM);
        Player player2 = new Player();
        assertFalse(Turn.isBarrelSave(player, deck, discard, new TestUserInterface(), 1, player2) != 0);
    }

    public void testSavedByBarrel() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setSheriffManualTest();
        turn.setUserInterface(new TestUserInterface());
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        Player player2 = new Player();
        assertTrue(Turn.isBarrelSave(sheriff, deck, discard, new TestUserInterface(), 1, player2) != 0);
    }

    public void testNotSavedByBarrel() {
        Card drawnCard = new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.NINE, CardType.ITEM);
        Deck deck = new Deck();
        deck.add(drawnCard);
        Discard discard = new Discard();
        Turn turn = new Turn();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setSheriffManualTest();
        turn.setUserInterface(new TestUserInterface());
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        sheriff.setCharacter(Character.RANDOM);
        Player player2 = new Player();
        assertFalse(
            Turn.isBarrelSave(sheriff, deck, discard, new TestUserInterface(), 1, player2) != 0);
    }

    public void testNoBeerPlayer() {
        Player player = new Player();
        player.setCharacter(Character.RANDOM);
        player.setHealth(2);
        Turn turn = new Turn();
        turn.damagePlayer(player, null, player, 1, null, null, null, null);
        assertEquals(1, player.getHealth());
    }

    public void testNoBeerPlayerKill() {
        Player player = new Player();
        player.setCharacter(Character.RANDOM);
        player.setRole(Role.RANDOM);
        Turn turn = new Turn();
        List<Player> players = new ArrayList<>();
        players.add(player);
        Player other1 = new Player();
        other1.setRole(Role.OUTLAW);
        Player other2 = new Player();
        other2.setRole(Role.SHERIFF);
        Character character1 = Character.RANDOM;
        Character character2 = Character.RANDOM;
        other1.setCharacter(character1);
        other2.setCharacter(character2);
        players.add(other1);
        players.add(other2);
        turn.setPlayers(players);
        player.setHealth(1);
        player.setHand(new Hand());
        player.setInPlay(new CardsInPlay());
        assertTrue(players.contains(player));
        turn.setUserInterface(new TestUserInterface());
        turn.setDiscard(new Discard());
        turn.damagePlayer(player, players, player, 1, null, null, null, new TestUserInterface());
        assertFalse(players.contains(player));
    }

    public void testBeerPlayerKill() {
        Player player = new Player();
        player.setRole(Role.RANDOM);
        player.setHand(new Hand());
        player.setMaxHealth(4);
        Turn turn = new Turn();
        List<Player> players = new ArrayList<>();
        players.add(player);
        player.setInPlay(new CardsInPlay());
        Player other1 = new Player();
        other1.setRole(Role.OUTLAW);
        Player other2 = new Player();
        other2.setRole(Role.SHERIFF);
        Character character1 = Character.RANDOM;
        Character character2 = Character.RANDOM;
        other1.setCharacter(character1);
        other2.setCharacter(character2);
        players.add(other1);
        players.add(other2);
        turn.setPlayers(players);
        player.setHealth(1);
        turn.setUserInterface(new TestUserInterface());
        turn.setDiscard(new Discard());
        player.getHand().add(new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        player.setCharacter(Character.RANDOM);
        turn.damagePlayer(
            player, players, player, 1, null, null, new Discard(), new TestUserInterface());
        assertTrue(players.contains(player));
    }

    public void testBeerPlayerKillNoPlay() {
        Player player = new Player();
        player.setRole(Role.RANDOM);
        player.setHand(new Hand());
        Turn turn = new Turn();
        List<Player> players = new ArrayList<>();
        player.setInPlay(new CardsInPlay());
        Player other1 = new Player();
        other1.setRole(Role.OUTLAW);
        Player other2 = new Player();
        other2.setRole(Role.SHERIFF);
        Character character1 = Character.RANDOM;
        Character character2 = Character.RANDOM;
        other1.setCharacter(character1);
        other2.setCharacter(character2);
        players.add(other1);
        players.add(other2);
        Character character = Character.RANDOM;
        players.add(player);
        turn.setPlayers(players);
        player.setHealth(1);
        turn.setUserInterface(new TestUserInterface());
        turn.setDiscard(new Discard());
        player.getHand().add(new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        player.setCharacter(Character.RANDOM);
        turn.damagePlayer(
            player, players, player, 1, null, null, new Discard(), new NoBeerUserInterface());
        turn.setUserInterface(new NoBeerUserInterface());
        turn.setDiscard(new Discard());
        assertFalse(players.contains(player));
    }

    public void testSheriffKillDeputy() {
        Player player = new Player();
        player.setHand(new Hand());
        player.setInPlay(new CardsInPlay());
        player.setCharacter(Character.RANDOM);
        player.setRole(Role.SHERIFF);
        Turn turn = new Turn();
        List<Player> players = new ArrayList<>();
        players.add(player);
        Player deputy = new Player();
        deputy.setHand(new Hand());
        deputy.setInPlay(new CardsInPlay());
        deputy.setRole(Role.DEPUTY);
        deputy.setCharacter(Character.RANDOM);
        players.add(deputy);
        Player phil = new Player();
        phil.setHand(new Hand());
        phil.setInPlay(new CardsInPlay());
        phil.setRole(Role.OUTLAW);
        phil.setCharacter(Character.RANDOM);
        players.add(phil);
        turn.setPlayers(players);
        player.setHealth(1);
        turn.setDiscard(new Discard());
        turn.setUserInterface(new TestUserInterface());
        player.getHand().add(new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        player
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        player
            .getCardsInPlay()
            .setGun((new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.GUN)));

        deputy.getHand().add(new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        deputy
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        deputy
            .getCardsInPlay()
            .setGun((new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.GUN)));
        turn.handleDeath(deputy, player, phil, players, new TestUserInterface(), null, new Discard());
        assertEquals(0, player.getHand().size());
        assertFalse(player.getCardsInPlay().hasGun());
        assertFalse(player.getCardsInPlay().hasItem(CardName.BARREL));

        assertEquals(0, deputy.getHand().size());
        assertFalse(deputy.getCardsInPlay().hasGun());
        assertFalse(deputy.getCardsInPlay().hasItem(CardName.BARREL));
    }

    public void testSheriffKillOutlaw() {
        Player player = new Player();
        player.setHand(new Hand());
        player.setInPlay(new CardsInPlay());
        player.setRole(Role.SHERIFF);
        player.setCharacter(Character.RANDOM);
        Turn turn = new Turn();
        Deck deck = Setup.setupDeck(false);
        turn.setDeck(deck);
        List<Player> players = new ArrayList<>();
        players.add(player);
        Player deputy = new Player();
        deputy.setHand(new Hand());
        deputy.setInPlay(new CardsInPlay());
        deputy.setCharacter(Character.RANDOM);
        deputy.setRole(Role.OUTLAW);
        players.add(deputy);
        Player phil = new Player();
        phil.setHand(new Hand());
        phil.setInPlay(new CardsInPlay());
        phil.setRole(Role.OUTLAW);
        phil.setCharacter(Character.RANDOM);
        players.add(phil);
        turn.setPlayers(players);
        player.setHealth(1);
        turn.setDiscard(new Discard());
        turn.setUserInterface(new TestUserInterface());
        turn.handleDeath(deputy, player, phil, players, new TestUserInterface(), deck, null);
        assertEquals(3, player.getHand().size());
    }

    public void testOutlawKillDeputy() {
        Player player = new Player();
        player.setHand(new Hand());
        player.setInPlay(new CardsInPlay());
        player.setRole(Role.SHERIFF);
        player.setCharacter(Character.RANDOM);
        Turn turn = new Turn();
        Deck deck = Setup.setupDeck(false);
        turn.setDeck(deck);
        List<Player> players = new ArrayList<>();
        players.add(player);
        Player deputy = new Player();
        deputy.setHand(new Hand());
        deputy.setInPlay(new CardsInPlay());
        deputy.setCharacter(Character.RANDOM);
        deputy.setRole(Role.DEPUTY);
        players.add(deputy);
        Player phil = new Player();
        phil.setHand(new Hand());
        phil.setInPlay(new CardsInPlay());
        phil.setRole(Role.OUTLAW);
        phil.setCharacter(Character.RANDOM);
        players.add(phil);
        turn.setPlayers(players);
        player.setHealth(1);
        turn.setDiscard(new Discard());
        turn.setUserInterface(new TestUserInterface());
        turn.handleDeath(deputy, phil, phil, players, new TestUserInterface(), deck, null);
    }

    public void testOutlawKillSheriff() {
        Player player = new Player();
        player.setHand(new Hand());
        player.setInPlay(new CardsInPlay());
        player.setRole(Role.OUTLAW);
        player.setCharacter(Character.RANDOM);
        Turn turn = new Turn();
        Deck deck = Setup.setupDeck(false);
        turn.setDeck(deck);
        List<Player> players = new ArrayList<>();
        players.add(player);
        Player deputy = new Player();
        deputy.setHand(new Hand());
        deputy.setInPlay(new CardsInPlay());
        deputy.setCharacter(Character.RANDOM);
        deputy.setRole(Role.SHERIFF);
        players.add(deputy);
        Player phil = new Player();
        phil.setHand(new Hand());
        phil.setInPlay(new CardsInPlay());
        phil.setRole(Role.OUTLAW);
        phil.setCharacter(Character.RANDOM);
        players.add(phil);
        turn.setPlayers(players);
        player.setHealth(1);
        turn.setDiscard(new Discard());
        turn.setUserInterface(new TestUserInterface());
        try {
            turn.handleDeath(deputy, player, phil, players, new TestUserInterface(), deck, null);
        } catch (RuntimeException e) {
            fail();
        }
    }

    public void testNotGameOver() {
        List<Player> players = Setup.getNormalPlayers(4);
        Turn turn = new Turn();
        turn.setPlayers(players);
        assertFalse(Turn.isGameOver(players));
    }

    public void testRenegadeWin() {
        List<Player> players = new ArrayList<>();
        Player renegade = new Player();
        renegade.setRole(Role.RENEGADE);
        players.add(renegade);
        Turn turn = new Turn();
        turn.setPlayers(players);
        assertTrue(Turn.isGameOver(players));
        assertEquals("Renegade", Turn.getWinners(players));
    }

    public void testOutlawWin() {
        List<Player> players = new ArrayList<>();
        Player renegade = new Player();
        renegade.setRole(Role.OUTLAW);
        players.add(renegade);
        Turn turn = new Turn();
        turn.setPlayers(players);
        assertTrue(Turn.isGameOver(players));
        assertEquals("Outlaws", Turn.getWinners(players));
    }

    public void testOutlawWinDeuptyAlive() {
        List<Player> players = new ArrayList<>();
        Player deputy = new Player();
        deputy.setRole(Role.DEPUTY);
        players.add(deputy);
        Player renegade = new Player();
        renegade.setRole(Role.RENEGADE);
        players.add(renegade);
        Turn turn = new Turn();
        turn.setPlayers(players);
        assertTrue(Turn.isGameOver(players));
        assertEquals("Outlaws", Turn.getWinners(players));
    }

    public void testOutlawWinDeuptyAlive2() {
        List<Player> players = new ArrayList<>();
        Player deputy = new Player();
        deputy.setRole(Role.DEPUTY);
        players.add(deputy);
        Turn turn = new Turn();
        turn.setPlayers(players);
        assertTrue(Turn.isGameOver(players));
        assertEquals("Outlaws", Turn.getWinners(players));
    }

    public void testSheriffDeputyWin() {
        List<Player> players = new ArrayList<>();
        Player renegade = new Player();
        renegade.setRole(Role.SHERIFF);
        players.add(renegade);
        Turn turn = new Turn();
        turn.setPlayers(players);
        assertTrue(Turn.isGameOver(players));
        assertEquals("Sheriff and Deputies", Turn.getWinners(players));
    }

    public void testSheriffDeputyWin2() {
        List<Player> players = new ArrayList<>();
        Player renegade = new Player();
        renegade.setRole(Role.SHERIFF);
        players.add(renegade);
        Player renegade2 = new Player();
        renegade2.setRole(Role.DEPUTY);
        players.add(renegade2);
        Turn turn = new Turn();
        turn.setPlayers(players);
        assertTrue(Turn.isGameOver(players));
        assertEquals("Sheriff and Deputies", Turn.getWinners(players));
    }

    public void testNoOneWinRenegade() {
        List<Player> players = new ArrayList<>();
        Player renegade = new Player();
        renegade.setRole(Role.RENEGADE);
        players.add(renegade);
        Turn turn = new Turn();
        Player sheriff = new Player();
        sheriff.setRole(Role.SHERIFF);
        players.add(sheriff);
        turn.setPlayers(players);
        try {
            Turn.getWinners(players);
            fail();
        } catch (RuntimeException e) {
            // expected
        }
    }

    public void testNoOneWinOutlaw() {
        List<Player> players = new ArrayList<>();
        Player renegade = new Player();
        renegade.setRole(Role.OUTLAW);
        players.add(renegade);
        Turn turn = new Turn();
        Player sheriff = new Player();
        sheriff.setRole(Role.SHERIFF);
        players.add(sheriff);
        turn.setPlayers(players);
        try {
            Turn.getWinners(players);
            fail();
        } catch (RuntimeException e) {
            // expected
        }
    }

    public void testNoOneWinDeputy() {
        List<Player> players = new ArrayList<>();
        Player renegade = new Player();
        renegade.setRole(Role.RENEGADE);
        players.add(renegade);
        Turn turn = new Turn();
        Player deputy = new Player();
        deputy.setRole(Role.DEPUTY);
        players.add(deputy);
        Player sheriff = new Player();
        sheriff.setRole(Role.SHERIFF);
        players.add(sheriff);
        turn.setPlayers(players);
        try {
            Turn.getWinners(players);
            fail();
        } catch (RuntimeException e) {
            // expected
        }
    }

    public void testBartCasidy() {
        Turn turn = new Turn();
        Deck deck = Setup.setupDeck(false);
        turn.setDeck(deck);
        turn.setPlayers(new ArrayList<>());
        Player player = new Player();
        player.setHand(new Hand());
        Character character = Character.BARTCASSIDY;
        player.setCharacter(character);
        player.setMaxHealth(4);
        turn.damagePlayer(
            player, new ArrayList<>(), player, 1, player, deck, null, new TestUserInterface());
        assertEquals(1, player.getHand().size());
    }

    public void testJourdonnais() {
        Turn turn = new Turn();
        Player player = new Player();
        player.setInPlay(new CardsInPlay());
        Character character = Character.JOURDONNAIS;
        player.setCharacter(character);
        Deck deck = new Deck();
        Card card = new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        turn.setUserInterface(new TestUserInterface());
        deck.add(card);
        Discard discard = new Discard();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        assertTrue(
            Turn.isBarrelSave(player, deck, discard, new TestUserInterface(), 1, new Player()) != 0);
    }

    public void testJourdonnaisFail() {
        Turn turn = new Turn();
        Player player = new Player();
        player.setInPlay(new CardsInPlay());
        Character character = Character.JOURDONNAIS;
        player.setCharacter(character);
        Deck deck = new Deck();
        Card card = new Card(CardName.BARREL, CardSuit.DIAMONDS, CardValue.NINE, CardType.ITEM);
        turn.setUserInterface(new TestUserInterface());
        deck.add(card);
        Discard discard = new Discard();
        turn.setDeck(deck);
        turn.setDiscard(discard);

        assertFalse(
            Turn.isBarrelSave(player, deck, discard, new TestUserInterface(), 1, new Player()) != 0);
    }

    public void testJourdonnaisFailBarrelFail() {
        Turn turn = new Turn();
        Player player = new Player();
        player.setInPlay(new CardsInPlay());
        Character character = Character.JOURDONNAIS;
        player.setCharacter(character);
        player
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        Deck deck = new Deck();
        Card card = new Card(CardName.BARREL, CardSuit.DIAMONDS, CardValue.NINE, CardType.ITEM);
        turn.setUserInterface(new TestUserInterface());
        deck.add(card);
        Card card2 = new Card(CardName.BARREL, CardSuit.DIAMONDS, CardValue.NINE, CardType.ITEM);
        deck.add(card2);
        Discard discard = new Discard();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        assertEquals(
            0, Turn.isBarrelSave(player, deck, discard, new TestUserInterface(), 1, new Player()));
        assertEquals(0, deck.size());
    }

    public void testJourdonnaisFailBarrelSuccess() {
        Turn turn = new Turn();
        Player player = new Player();
        player.setInPlay(new CardsInPlay());
        Character character = Character.JOURDONNAIS;
        player.setCharacter(character);
        player
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        Deck deck = new Deck();
        Card card = new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        turn.setUserInterface(new TestUserInterface());
        deck.add(card);
        Card card2 = new Card(CardName.BARREL, CardSuit.DIAMONDS, CardValue.NINE, CardType.ITEM);
        deck.add(card2);
        Discard discard = new Discard();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        assertEquals(
            1, Turn.isBarrelSave(player, deck, discard, new TestUserInterface(), 1, new Player()));
        assertEquals(0, deck.size());
    }

    public void testJourdonnaisSuccessBarrelSkip() {
        Turn turn = new Turn();
        Player player = new Player();
        player.setInPlay(new CardsInPlay());
        Character character = Character.JOURDONNAIS;
        player.setCharacter(character);
        player
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        Deck deck = new Deck();
        Card card = new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        turn.setUserInterface(new TestUserInterface());
        deck.add(card);
        Card card2 = new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        deck.add(card2);
        Discard discard = new Discard();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        assertEquals(
            1, Turn.isBarrelSave(player, deck, discard, new TestUserInterface(), 1, new Player()));
        assertEquals(1, deck.size());
    }

    public void testJourdonnaisSuccessBarrelSuccessDoubleMiss() {
        Turn turn = new Turn();
        Player player = new Player();
        player.setInPlay(new CardsInPlay());
        Character character = Character.JOURDONNAIS;
        player.setCharacter(character);
        player
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        Deck deck = new Deck();
        Card card = new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        turn.setUserInterface(new TestUserInterface());
        deck.add(card);
        Card card2 = new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        deck.add(card2);
        Discard discard = new Discard();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        assertEquals(
            2, Turn.isBarrelSave(player, deck, discard, new TestUserInterface(), 2, new Player()));
        assertEquals(0, deck.size());
    }

    public void testPaulRegret() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        for (Player player : players) {
            Character character = Character.PAULREGRET;
            player.setCharacter(character);
        }
        assertEquals(1, TargetingService.getPlayersWithinRange(sheriff, players).size());
    }

    public void testRoseDoolan() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        Character character = Character.ROSEDOOLAN;
        sheriff.setCharacter(character);
        assertEquals(4, TargetingService.getPlayersWithinRange(sheriff, players).size());
    }

    public void testWillyTheKid() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        Character character = Character.WILLYTHEKID;
        sheriff.setCharacter(character);
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);
        turn.play();
        assertEquals(1, sheriff.getHand().size());
        turn.play();
        assertEquals(0, sheriff.getHand().size());
    }

    public void testVultureSam() {
        Player player = new Player();
        player.setHand(new Hand());
        Character character = Character.VULTURESAM;
        player.setCharacter(character);
        Player other = new Player();
        Character otherCharacter = Character.RANDOM;
        other.setCharacter(otherCharacter);
        Hand hand = new Hand();
        CardsInPlay cardsInPlay = new CardsInPlay();
        other.setHand(hand);
        other.setInPlay(cardsInPlay);
        other.getHand().add(new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        other
            .getCardsInPlay()
            .add(new Card(CardName.BARREL, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        other
            .getCardsInPlay()
            .setGun((new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.GUN)));
        Discard discard = new Discard();
        List<Player> players = new ArrayList<>();
        players.add(player);
        Turn turn = new Turn();
        turn.setUserInterface(new TestUserInterface());
        Deck deck = new Deck();
        turn.deadDiscardAll(other, players, discard, deck);
        assertEquals(3, player.getHand().size());
    }

    public void testLuckyDuke() {
        Turn turn = new Turn();
        Deck deck = new Deck();
        Player player = new Player();
        Character character = Character.LUCKYDUKE;
        player.setCharacter(character);
        Card card1 = new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Card card2 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card2);
        Discard discard = new Discard();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        turn.setUserInterface(new TestUserInterface());
        assertEquals(card2, Turn.draw(player, deck, discard, new TestUserInterface()));
        assertTrue(deck.isEmpty());
    }

    public void testLuckyInvalidValid() {
        Deck deck = new Deck();
        Player player = new Player();
        Character character = Character.LUCKYDUKE;
        player.setCharacter(character);
        Card card1 = new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Card card2 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card2);
        Discard discard = new Discard();
        assertEquals(card2, Turn.draw(player, deck, discard, new DukeUserInterface()));
        assertTrue(deck.isEmpty());
    }

    public void testLuckyDukeChooseOther() {
        Turn turn = new Turn();
        Deck deck = new Deck();
        Player player = new Player();
        Character character = Character.LUCKYDUKE;
        player.setCharacter(character);
        Card card1 = new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Card card2 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card2);
        Discard discard = new Discard();
        turn.setDeck(deck);
        turn.setDiscard(discard);
        turn.setUserInterface(new TestPlayOneUserInterface());
        assertEquals(card1, Turn.draw(player, deck, discard, new TestPlayOneUserInterface()));
        assertTrue(deck.isEmpty());
    }

    public void testBlackJackHearts() {
        Player player = new Player();
        Character character = Character.BLACKJACK;
        player.setCharacter(character);
        player.setHand(new Hand());
        Turn turn = new Turn();
        turn.setUserInterface(new TestUserInterface());
        Deck deck = new Deck();
        Card card1 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Card card2 = new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Card card3 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card2);
        deck.add(card3);
        turn.drawCards(player, deck);
        assertEquals(3, player.getHand().size());
    }

    public void testBlackJackDiamonds() {
        Player player = new Player();
        Character character = Character.BLACKJACK;
        player.setCharacter(character);
        player.setHand(new Hand());
        Turn turn = new Turn();
        turn.setUserInterface(new TestUserInterface());
        Deck deck = new Deck();
        Card card1 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Card card2 = new Beer(CardName.BEER, CardSuit.DIAMONDS, CardValue.NINE, CardType.ITEM);
        Card card3 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card2);
        deck.add(card3);
        turn.drawCards(player, deck);
        assertEquals(3, player.getHand().size());
    }

    public void testBlackJackDefault() {
        Player player = new Player();
        Character character = Character.BLACKJACK;
        player.setCharacter(character);
        player.setHand(new Hand());
        Turn turn = new Turn();
        turn.setUserInterface(new TestUserInterface());
        Deck deck = new Deck();
        Card card1 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Card card2 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Card card3 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card2);
        deck.add(card3);
        turn.drawCards(player, deck);
        assertEquals(2, player.getHand().size());
    }

    public void testPedroRamerez() {
        Player player = new Player();
        Character character = Character.PEDRORAMIREZ;
        player.setCharacter(character);
        player.setHand(new Hand());
        Turn turn = new Turn();
        Deck deck = new Deck();
        Card card1 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Card card2 = new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Card card3 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card3);
        Discard discard = new Discard();
        discard.add(card2);
        turn.setDiscard(discard);
        turn.setUserInterface(new TestUserInterface());
        turn.drawCards(player, deck);
        assertEquals(2, player.getHand().size());
        assertFalse(deck.isEmpty());
    }

    public void testPedroRamerezDefault() {
        Player player = new Player();
        Character character = Character.PEDRORAMIREZ;
        player.setCharacter(character);
        player.setHand(new Hand());
        Turn turn = new Turn();
        Deck deck = new Deck();
        Card card1 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Card card2 = new Beer(CardName.BEER, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM);
        Card card3 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card3);
        Discard discard = new Discard();
        discard.add(card2);
        turn.setDiscard(discard);
        turn.setUserInterface(new TestUserInterfaceBangBackOnce());
        turn.drawCards(player, deck);
        assertEquals(2, player.getHand().size());
        assertTrue(deck.isEmpty());
    }

    public void testPedroRamerezDiscardEmpty() {
        Player player = new Player();
        Character character = Character.PEDRORAMIREZ;
        player.setCharacter(character);
        player.setHand(new Hand());
        Turn turn = new Turn();
        Deck deck = new Deck();
        Card card1 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Card card3 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card1);
        deck.add(card3);
        Discard discard = new Discard();
        turn.setDiscard(discard);
        turn.setUserInterface(new TestUserInterfaceBangBackOnce());
        turn.drawCards(player, deck);
        assertEquals(2, player.getHand().size());
        assertTrue(deck.isEmpty());
    }

    public void testJesseJones() {
        Player player = new Player();
        Character character = Character.JESSEJONES;
        player.setCharacter(character);
        player.setHand(new Hand());

        Player other = new Player();
        Character otherCharacter = Character.JESSEJONES;
        other.setCharacter(otherCharacter);
        Hand otherHand = new Hand();
        Card card1 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        otherHand.add(card1);
        other.setHand(otherHand);

        Turn turn = new Turn();
        Deck deck = new Deck();
        Card card2 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card2);
        Discard discard = new Discard();
        turn.setDiscard(discard);
        turn.setUserInterface(new TestUserInterface());

        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(other);
        turn.setPlayers(players);

        turn.drawCards(player, deck);
        assertEquals(2, player.getHand().size());
    }

    public void testJesseJonesDefault() {
        Player player = new Player();
        Character character = Character.JESSEJONES;
        player.setCharacter(character);
        player.setHand(new Hand());

        Player other = new Player();
        Character otherCharacter = Character.JESSEJONES;
        other.setCharacter(otherCharacter);
        other.setHand(new Hand());

        Turn turn = new Turn();
        Deck deck = new Deck();
        Card card2 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        Card card1 = new Beer(CardName.BEER, CardSuit.SPADES, CardValue.NINE, CardType.ITEM);
        deck.add(card2);
        deck.add(card1);
        Discard discard = new Discard();
        turn.setDiscard(discard);
        turn.setUserInterface(new TestUserInterfaceBangBackOnce());

        List<Player> players = new ArrayList<>();
        players.add(other);
        players.add(player);
        turn.setPlayers(players);

        turn.drawCards(player, deck);
        assertEquals(2, player.getHand().size());
    }

    public void testCalamityJanetBang() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        Character character = Character.CALAMITYJANET;
        sheriff.setCharacter(character);
        sheriff
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth() - 1);
    }

    public void testCalamityJanetMiss() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwicePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        Character enemyCharacter = Character.CALAMITYJANET;
        enemy.setCharacter(enemyCharacter);
        enemy.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth());
        assertEquals(0, enemy.getHand().size());
    }

    public void testKitCarlson() {
        Deck deck = new Deck();
        Turn turn = new Turn();
        Card card2 = new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY);
        deck.add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        deck.add(card2);
        deck.add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        turn.setDeck(deck);
        Player player = new Player();
        player.setHand(new Hand());
        Character character = Character.KITCARLSON;
        player.setCharacter(character);
        turn.setUserInterface(new TestPlayOneUserInterfaceChoosePlayerBangBack());
        turn.drawCards(player, deck);
        assertEquals(2, player.getHand().size());
        assertFalse(deck.isEmpty());
        assertEquals(card2, deck.pull());
    }

    public void testElGringo() {
        Player elGringo = new Player();
        Character character = Character.ELGRINGO;
        elGringo.setMaxHealth(4);
        elGringo.setCharacter(character);
        Hand gringoHand = new Hand();
        elGringo.setHand(gringoHand);
        Player other = new Player();
        other.setMaxHealth(4);
        other.setCharacter(character);
        Hand otherHand = new Hand();
        otherHand.add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        other.setHand(otherHand);
        Turn turn = new Turn();
        turn.setPlayers(new ArrayList<>());
        turn.damagePlayer(
            elGringo, new ArrayList<>(), other, 1, other, null, null, new TestUserInterface());
        assertEquals(0, otherHand.size());
        assertEquals(1, gringoHand.size());
    }

    public void testElGringoOtherHasNone() {
        Player elGringo = new Player();
        Character character = Character.ELGRINGO;
        elGringo.setCharacter(character);
        elGringo.setMaxHealth(4);
        Hand gringoHand = new Hand();
        elGringo.setHand(gringoHand);
        Player other = new Player();
        Hand otherHand = new Hand();
        other.setHand(otherHand);
        Turn turn = new Turn();
        turn.setPlayers(new ArrayList<>());
        turn.damagePlayer(elGringo, new ArrayList<>(), other, 1, other, null, null, null);
        assertEquals(0, otherHand.size());
        assertEquals(0, gringoHand.size());
    }

    public void testSuzyLafayette() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        Deck deck = Setup.setupDeck(false);
        turn.setDeck(deck);
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        Character character = Character.SUZYLAFAYETTE;
        sheriff.setCharacter(character);
        sheriff
            .getHand()
            .add(new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.QUEEN, CardType.GUN));
        UserInterface testUserInterface = new TestPlayOneUserInterface();
        turn.setUserInterface(testUserInterface);
        turn.play();
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testSidKetchumUserInterface() {
        Player sidKetchum = new Player();
        Hand hand = new Hand();
        hand.add(new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.QUEEN, CardType.GUN));
        hand.add(new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.QUEEN, CardType.GUN));
        sidKetchum.setHand(hand);
        UserInterface userInterface = new TestUserInterface();
        List<Card> cardsToDiscard = userInterface.chooseTwoDiscardForLife(sidKetchum);
        for (Card card : cardsToDiscard) {
            hand.remove(card);
        }
        assertEquals(0, hand.size());
    }

    public void testSidKetchumDiscard() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        Character character = Character.SIDKETCHUM;
        sheriff.setCharacter(character);
        UserInterface testUserInterface = new TestUserInterfaceSpecial();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        sheriff.setHealth(1);
        assertEquals(1, sheriff.getHealth());
        assertEquals(2, sheriff.getHand().size());
        turn.play();
        assertEquals(2, sheriff.getHealth());
        assertEquals(0, sheriff.getHand().size());
    }

    public void testSlabTheKiller() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        Character character = Character.SLABTHEKILLER;
        sheriff.setCharacter(character);
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwicePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        enemy
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        enemy
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth());
        assertEquals(0, enemy.getHand().size());
    }

    public void testSlabTheKillerRespondGreen() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        Character character = Character.SLABTHEKILLER;
        sheriff.setCharacter(character);
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwicePlayer1green();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        enemy
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        enemy
            .getCardsInPlay()
            .add(
                new SingleUseMissed(
                    CardName.SOMBRERO, CardSuit.CLUBS, CardValue.SEVEN, CardType.SINGLE_USE_ITEM));
        assertEquals(1, enemy.getHand().size());
        assertEquals(1, enemy.getCardsInPlay().size());
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth());
        assertEquals(0, enemy.getHand().size());
        assertEquals(0, enemy.getCardsInPlay().size());
    }

    public void testSlabTheKillerRespondGreen2() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        Character character = Character.SLABTHEKILLER;
        sheriff.setCharacter(character);
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwicePlayer1green2();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        enemy
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        enemy
            .getCardsInPlay()
            .add(
                new SingleUseMissed(
                    CardName.SOMBRERO, CardSuit.CLUBS, CardValue.SEVEN, CardType.SINGLE_USE_ITEM));
        assertEquals(1, enemy.getHand().size());
        assertEquals(1, enemy.getCardsInPlay().size());
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth());
        assertEquals(0, enemy.getHand().size());
        assertEquals(0, enemy.getCardsInPlay().size());
    }

    public void testSlabTheKillerRespondGreen3() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        Character character = Character.SLABTHEKILLER;
        sheriff.setCharacter(character);
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwicePlayer1green3();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);

        Player enemy = others.getFirst();
        enemy
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        enemy
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        enemy
            .getCardsInPlay()
            .add(
                new SingleUseMissed(
                    CardName.SOMBRERO, CardSuit.CLUBS, CardValue.SEVEN, CardType.SINGLE_USE_ITEM));
        assertEquals(2, enemy.getHand().size());
        assertEquals(1, enemy.getCardsInPlay().size());
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth());
        assertEquals(1, enemy.getHand().size());
        assertEquals(0, enemy.getCardsInPlay().size());
    }

    public void testSlabTheKillerJanetOneEach() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        Character character = Character.SLABTHEKILLER;
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwicePlayer1();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {

            Character otherCharacter = Character.CALAMITYJANET;
            otherPlayer.setCharacter(otherCharacter);
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);
        sheriff.setCharacter(character);
        Player enemy = others.getFirst();
        enemy
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        enemy.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        turn.play();
        assertEquals(enemy.getHealth(), enemy.getMaxHealth());
        assertEquals(0, enemy.getHand().size());
    }

    public void testPlayMissDoNothing() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Missed(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceBangBackTwice();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testVolcanic() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.setCharacter(Character.WILLYTHEKID);
        sheriff
            .getCardsInPlay()
            .setGun(new Gun(CardName.VOLCANIC, CardSuit.CLUBS, CardValue.QUEEN, CardType.GUN));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        List<Player> others = new ArrayList<>();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(sheriff), players.indexOf(otherPlayer), players.size());
            if (distance <= 1) {
                others.add(otherPlayer);
            }
        }
        others.remove(sheriff);
        turn.play();
        assertEquals(1, sheriff.getHand().size());
        turn.play();
        assertEquals(0, sheriff.getHand().size());
    }

    public void testTryMultiBangScofield() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff
            .getCardsInPlay()
            .setGun(new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.QUEEN, CardType.GUN));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertEquals(1, sheriff.getHand().size());
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testBangNoOneInRange() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff.getHand().add(new Bang(CardName.BANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        for (Player player : players) {
            player
                .getCardsInPlay()
                .add(new Card(CardName.MUSTANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        }
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testPanicNoOneInRange() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new Panic(CardName.PANIC, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        for (Player player : players) {
            player
                .getCardsInPlay()
                .add(new Card(CardName.MUSTANG, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        }
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testCatbalouNothingInPlay() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new CatBalou(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertEquals(1, sheriff.getHand().size());
    }

    public void testPlayEmptyHand() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        UserInterface testUserInterface = new TestUserInterfaceNoMiss();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertTrue(turn.isDonePlaying());
    }

    public void testPlayDonePlaying() {
        Turn turn = new Turn();
        List<Player> players = Setup.getNormalPlayers(4);
        turn.setPlayers(players);
        turn.setDeck(Setup.setupDeck(false));
        turn.setDiscard(new Discard());
        turn.setSheriffManualTest();
        Player sheriff = turn.getCurrentPlayer();
        sheriff
            .getHand()
            .add(new CatBalou(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        UserInterface testUserInterface = new TestUserInterfaceDonePlaying();
        turn.setUserInterface(testUserInterface);
        turn.setDiscard(new Discard());
        turn.setDeck(Setup.setupDeck(false));
        turn.play();
        assertTrue(turn.isDonePlaying());
    }

    public void testCatBalouTargets() {
        Player player = new Player();
        player.setRole(Role.OUTLAW);
        player.setInPlay(new CardsInPlay());
        player.setHand(new Hand());
        Player sheriff = new Player();
        sheriff.setHand(new Hand());
        sheriff.setInPlay(new CardsInPlay());
        sheriff.setRole(Role.SHERIFF);
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(sheriff);
        CatBalou catBalou =
            new CatBalou(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY);
        List<Player> targets = catBalou.targets(player, players);
        assertEquals(0, targets.size());
    }

    public void testCatBalouTargetsHand() {
        Player player = new Player();
        player.setRole(Role.OUTLAW);
        player.setInPlay(new CardsInPlay());
        player.setHand(new Hand());
        Player sheriff = new Player();
        Hand hand = new Hand();
        hand.add(new CatBalou(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        sheriff.setHand(hand);
        sheriff.setInPlay(new CardsInPlay());
        sheriff.setRole(Role.SHERIFF);
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(sheriff);
        CatBalou catBalou =
            new CatBalou(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY);
        List<Player> targets = catBalou.targets(player, players);
        assertEquals(1, targets.size());
    }

    public void testCatBalouTargetsInPlay() {
        Player player = new Player();
        player.setRole(Role.OUTLAW);
        player.setInPlay(new CardsInPlay());
        player.setHand(new Hand());
        Player sheriff = new Player();
        Hand hand = new Hand();
        sheriff.setHand(hand);
        CardsInPlay cardsInPlay = new CardsInPlay();
        cardsInPlay.add(new Card(CardName.SCOPE, CardSuit.CLUBS, CardValue.QUEEN, CardType.ITEM));
        sheriff.setInPlay(cardsInPlay);
        sheriff.setRole(Role.SHERIFF);
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(sheriff);
        CatBalou catBalou =
            new CatBalou(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY);
        List<Player> targets = catBalou.targets(player, players);
        assertEquals(1, targets.size());
    }

    public void testCatBalouTargetsGun() {
        Player player = new Player();
        player.setRole(Role.OUTLAW);
        player.setInPlay(new CardsInPlay());
        player.setHand(new Hand());
        Player sheriff = new Player();
        Hand hand = new Hand();
        sheriff.setHand(hand);
        CardsInPlay cardsInPlay = new CardsInPlay();
        sheriff.setInPlay(cardsInPlay);
        sheriff.setGun(new Gun(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.QUEEN, CardType.GUN));
        sheriff.setRole(Role.SHERIFF);
        List<Player> players = new ArrayList<>();
        players.add(player);
        players.add(sheriff);
        CatBalou catBalou =
            new CatBalou(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY);
        List<Player> targets = catBalou.targets(player, players);
        assertEquals(1, targets.size());
    }
}
