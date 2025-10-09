package com.chriscarr.bang.userinterface;

import com.chriscarr.bang.CardsInPlay;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.gamestate.GameStateListener;
import com.chriscarr.bang.gamestate.GameStatePlayer;
import com.chriscarr.bang.turn.Turn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ManualUserInterface implements UserInterface, GameStateListener {
    private static final Logger LOG = LoggerFactory.getLogger(ManualUserInterface.class);

    Turn turn;

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public void printInfo(String info) {
        LOG.info(info);
    }

    @Override
    public List<Card> chooseTwoDiscardForShoot(Player player) {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    @Override
    public int askBlueDiscard(Player player) {
        return -1;
    }

    public int askDiscard(Player player) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Discard");
        Hand hand = player.getHand();
        int handSize = hand.size();
        for (int i = 0; i < handSize; i++) {
            LOG.info("{}) {}", i, hand.get(i).getName());
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= 0 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading input", e);
            }
        }
    }

    @Override
    public int askOthersCard(Player player, CardsInPlay cardsInPlay, boolean hasHand) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Choose Other Players Card");
        int handSize = cardsInPlay.size();
        if (hasHand) {
            LOG.info("-1) Hand");
        }
        boolean hasGun = cardsInPlay.hasGun();
        if (hasGun) {
            LOG.info("-2) Gun");
        }
        for (int i = 0; i < handSize; i++) {
            LOG.info("{}) {}", i, cardsInPlay.get(i).getName());
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= -2 && cardNumber < handSize) {
                    if (cardNumber == -2 && !hasGun) {
                        continue;
                    }
                    if (cardNumber == -1 && !hasHand) {
                        continue;
                    }
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public int askPlay(Player player) {
        printGameState();
        printPrivateInfo(player);
        LOG.info("Play");
        Hand hand = player.getHand();
        int handSize = hand.size();
        LOG.info("-1) done playing");
        for (int i = 0; i < handSize; i++) {
            Card card = hand.get(i);
            boolean canPlay = turn.canPlay(player, card);
            LOG.info("{}) {} can play? {}", i, card.getName(), canPlay);
            if (canPlay) {
                LOG.info(" Targets: ");
                for (String name : turn.targets(player, card)) {
                    LOG.info("{} ", name);
                }
            }
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= -1 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    private void printPrivateInfo(Player player) {
        LOG.info(player.getRole().getRoleName());
    }

    @Override
    public int askPlayer(Player player, List<String> otherPlayers) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Choose Player");
        int handSize = otherPlayers.size();
        for (int i = 0; i < handSize; i++) {
            LOG.info("{}) {}", i, otherPlayers.get(i));
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= 0 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public boolean chooseDiscard(Player player, Card card) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Draw Card From Discard");
        LOG.info("0) From Discard {}", card.getName());
        LOG.info("1) From Deck");
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber == 0) {
                    return true;
                } else if (cardNumber == 1) {
                    return false;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public int chooseGeneralStoreCard(Player player, List<Card> cards) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Choose General Store Card");
        int handSize = cards.size();
        for (int i = 0; i < handSize; i++) {
            LOG.info("{}) {}", i, cards.get(i).getName());
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= 0 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public List<Card> chooseTwoDiscardForLife(Player player) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Discard Two cards for 1 Life, 4 for 2, etc");
        Hand hand = player.getHand();
        int handSize = hand.size();
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        List<Card> chosenCards = new ArrayList<>();
        while (true) {
            LOG.info("-1) done choosing");
            for (int i = 0; i < handSize; i++) {
                String chosen = " not chosen";
                if (chosenCards.contains(hand.get(i))) {
                    chosen = " chosen";
                }
                LOG.info("{}) {}{}", i, hand.get(i).getName(), chosen);
            }
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= -1 && cardNumber < handSize) {
                    if (cardNumber == -1) {
                        return chosenCards;
                    } else {
                        Card card = hand.get(cardNumber);
                        if (chosenCards.contains(card)) {
                            chosenCards.remove(card);
                        } else {
                            chosenCards.add(card);
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public int respondBang(Player player) {
        printPrivateInfo(player);
        LOG.info("Respond Bang");
        Hand hand = player.getHand();
        int handSize = hand.size();
        LOG.info("-1) done playing");
        for (int i = 0; i < handSize; i++) {
            Card card = hand.get(i);
            boolean canPlay =
                CardName.BANG.equals(card.getName())
                    || (CardName.MISSED.equals(card.getName())
                    && Character.CALAMITYJANET.equals(player.getCharacter()));
            LOG.info("{}) {} can play? {}", i, card.getName(), canPlay);
            if (canPlay) {
                LOG.info(" Targets: ");
                for (String name : turn.targets(player, card)) {
                    LOG.info("{} ", name);
                }
            }
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= -1 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public int respondBeer(Player player) {
        printPrivateInfo(player);
        LOG.info("Respond Beer");
        Hand hand = player.getHand();
        int handSize = hand.size();
        LOG.info("-1) done playing");
        for (int i = 0; i < handSize; i++) {
            Card card = hand.get(i);
            boolean canPlay = CardName.BEER.equals(card.getName());
            LOG.info("{}) {} can play? {}", i, card.getName(), canPlay);
            if (canPlay) {
                LOG.info(" Targets: ");
                for (String name : turn.targets(player, card)) {
                    LOG.info("{} ", name);
                }
            }
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= -1 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public int respondMiss(Player player, boolean canSingleUse) {
        printPrivateInfo(player);
        LOG.info("Respond Miss");
        Hand hand = player.getHand();
        int handSize = hand.size();
        LOG.info("-1) done playing");
        for (int i = 0; i < handSize; i++) {
            Card card = hand.get(i);
            boolean canPlay =
                CardName.MISSED.equals(card.getName())
                    || (CardName.BANG.equals(card.getName())
                    && Character.CALAMITYJANET.equals(player.getCharacter()));
            LOG.info("{}) {} can play? {}", i, card.getName(), canPlay);
            if (canPlay) {
                LOG.info(" Targets: ");
                for (String name : turn.targets(player, card)) {
                    LOG.info("{} ", name);
                }
            }
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= -1 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public boolean chooseFromPlayer(Player player) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Draw Card From Player");
        LOG.info("0) From Player");
        LOG.info("1) From Deck");
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber == 0) {
                    return true;
                } else if (cardNumber == 1) {
                    return false;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public int chooseDrawCard(Player player, List<Card> cards) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Choose Draw Card to keep");
        int handSize = cards.size();
        for (int i = 0; i < handSize; i++) {
            LOG.info("{}) {}", i, cards.get(i).getName());
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= 0 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public int chooseCardToPutBack(Player player, List<Card> cards) {
        LOG.info(player.getCharacter().getName());
        LOG.info("Choose card put back");
        int handSize = cards.size();
        for (int i = 0; i < handSize; i++) {
            LOG.info("{}) {}", i, cards.get(i).getName());
        }
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= 0 && cardNumber < handSize) {
                    return cardNumber;
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    public void printGameState() {
        GameState gameState = turn.getGameState();
        LOG.info("Current Turn: {}", gameState.getCurrentName());
        LOG.info("Is game over: {}", gameState.isGameOver());
        LOG.info("Deck size: {}", gameState.getDeckSize());
        LOG.info("Discard top card: {}", gameState.discardTopCard());
        List<GameStatePlayer> players = gameState.getPlayers();
        for (GameStatePlayer player : players) {
            LOG.info("Name: {}", player.name);
            LOG.info("Is Sheriff: {}", player.isSheriff);
            LOG.info("Ability: {}", player.specialAbility);
            LOG.info("Health: {}", player.health);
            LOG.info("Max: {}", player.maxHealth);
            LOG.info("Hand: {}", player.handSize);
            GameStateCard gun = player.gun;
            if (gun != null) {
                LOG.info("Discard top card: {}", gun.name);
                LOG.info("Discard top card: {}", gun.suit);
                LOG.info("Discard top card: {}", gun.type);
                LOG.info("Discard top card: {}", gun.value);
            }
            List<GameStateCard> cards = player.inPlay;
            for (GameStateCard card : cards) {
                LOG.info("name: {}", card.name);
                LOG.info("suit: {}", card.suit);
                LOG.info("type: {}", card.type);
                LOG.info("value: {}", card.value);
            }
        }
    }

    @Override
    public List<Card> respondTwoMiss(Player player) {
        Hand hand = player.getHand();
        int handSize = hand.size();
        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        List<Card> chosenCards = new ArrayList<>();
        while (true) {
            try {
                String line = in.readLine();
                int cardNumber = Integer.parseInt(line);
                if (cardNumber >= -1 && cardNumber < handSize) {
                    if (cardNumber == -1) {
                        return chosenCards;
                    } else {
                        Card card = hand.get(cardNumber);
                        if (chosenCards.contains(card)) {
                            chosenCards.remove(card);
                        } else {
                            chosenCards.add(card);
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("Error reading Input", e);
            }
        }
    }

    @Override
    public String getRoleForName(String name) {
        return turn.getRoleForName(name);
    }

    public String getGoalForName(String name) {
        return turn.roleToGoal(name);
    }

    @Override
    public String getTimeout() {
        // TODO Auto-generated method stub
        return null;
    }
}
