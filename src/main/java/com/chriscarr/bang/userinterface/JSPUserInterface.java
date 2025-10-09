package com.chriscarr.bang.userinterface;

import com.chriscarr.bang.CardsInPlay;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.CardType;
import com.chriscarr.bang.cards.SingleUseMissed;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateListener;
import com.chriscarr.bang.turn.Turn;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JSPUserInterface implements UserInterface, GameStateListener {

    public List<Message> messages;
    public List<Message> responses;
    protected Turn turn;

    public JSPUserInterface() {
        messages = new ArrayList<>();
        responses = new ArrayList<>();
    }

    public ArrayList<String> getRoles() {
        return turn.getRoles();
    }

    protected void waitForResponse(String player) {
        while (responses.isEmpty()) {
            Thread.yield();
        }
    }

    private List<Card> makeCardList(String remove, Player player) {
        List<Card> cardsToDiscard = new ArrayList<>();
        if (!"".equals(remove) && !"-1".equals(remove)) {
            String[] removed = remove.split(",");
            Hand hand = player.getHand();
            for (String s : removed) {
                if (!"".equals(s)) {
                    if (Integer.parseInt(s) < hand.size()) {
                        if (!cardsToDiscard.contains(hand.get(Integer.parseInt(s)))) {
                            cardsToDiscard.add(hand.get(Integer.parseInt(s)));
                        }
                    } else {
                        // In Play Cards
                        // REFACTORING - What is happening here
                        CardsInPlay cardsInPlay = player.getCardsInPlay();
                        cardsInPlay.get(Integer.parseInt(s) - hand.size());
                        cardsToDiscard.add(cardsInPlay.get(Integer.parseInt(s) - hand.size()));
                    }
                }
            }
        }
        return cardsToDiscard;
    }

    @Override
    public int askDiscard(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card card : hand) {
            handCards.append(card.getName()).append(", ");
        }
        sendMessage(player.getName(), "askDiscard " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int askBlueDiscard(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card card : hand) {
            handCards.append(card.getName()).append(", ");
        }
        sendMessage(player.getName(), "askBlueDiscard " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int askOthersCard(Player player, CardsInPlay cardsInPlay, boolean hasHand) {
        StringBuilder inPlayCards = new StringBuilder();
        for (Card card : cardsInPlay) {
            inPlayCards.append(card.getName()).append(", ");
        }
        boolean hasGun = cardsInPlay.hasGun();
        sendMessage(
            player.getName(),
            "askOthersCard " + hasHand + ", " + hasGun + cardsInPlay.getGunName() + ", " + inPlayCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int askPlay(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card card : hand) {
            CardName name = card.getName();
            boolean canPlay = turn.canPlay(player, card);
            List<String> targets = turn.targets(player, card);
            StringBuilder targetString = new StringBuilder();
            for (String otherName : targets) {
                targetString.append(otherName).append("$");
            }
            handCards
                .append(name)
                .append("^")
                .append(card.getSuit().getLabel())
                .append("^")
                .append(card.getValue().getLabel())
                .append("@")
                .append(canPlay)
                .append("@")
                .append(targetString)
                .append(", ");
        }
        CardsInPlay cardsInPlay = player.getCardsInPlay();
        for (Card card : cardsInPlay) {
            if (card.getType() == CardType.SINGLE_USE_ITEM) {
                CardName name = card.getName();
                boolean canPlay = turn.canPlay(player, card);
                List<String> targets = turn.targets(player, card);
                StringBuilder targetString = new StringBuilder();
                for (String otherName : targets) {
                    targetString.append(otherName).append("$");
                }
                handCards
                    .append(name)
                    .append("^")
                    .append(card.getSuit().getLabel())
                    .append("^")
                    .append(card.getValue().getLabel())
                    .append("@")
                    .append(canPlay)
                    .append("@")
                    .append(targetString)
                    .append(", ");
            }
        }
        if (Character.CHUCKWENGAM.equals(player.getCharacter())) {
            handCards
                .append("loselifefor2cards" + "@true@")
                .append(player.getName())
                .append("$")
                .append(", ");
        }
        if (Character.JOSEDELGADO.equals(player.getCharacter())) {
            handCards
                .append("discardbluetodraw2" + "@true@")
                .append(player.getName())
                .append("$")
                .append(", ");
        }
        if (Character.DOCHOLYDAY.equals(player.getCharacter())) {
            handCards
                .append("discardtwotoshoot" + "@true@")
                .append(player.getName())
                .append("$")
                .append(", ");
        }
        if (Character.SIDKETCHUM.equals(player.getCharacter())) {
            handCards
                .append("discardtwoforlife" + "@true@")
                .append(player.getName())
                .append("$")
                .append(", ");
        }
        if (Character.UNCLEWILL.equals(player.getCharacter())) {
            handCards
                .append("discardforgeneralstore" + "@true@")
                .append(player.getName())
                .append("$")
                .append(", ");
        }
        sendMessage(player.getName(), "askPlay " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int askPlayer(Player player, List<String> otherPlayers) {
        StringBuilder names = new StringBuilder();
        for (String name : otherPlayers) {
            names.append(name).append(", ");
        }
        sendMessage(player.getName(), "askPlayer " + names);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int chooseCardToPutBack(Player player, List<Card> cards) {
        StringBuilder cardString = new StringBuilder();
        for (Card card : cards) {
            cardString
                .append(card.getName())
                .append("^")
                .append(card.getSuit().getLabel())
                .append("^")
                .append(card.getValue().getLabel())
                .append(", ");
        }
        sendMessage(player.getName(), "chooseCardToPutBack " + cardString);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public boolean chooseDiscard(Player player, Card card) {
        sendMessage(player.getName(), "chooseDiscard " + card.getName());
        waitForResponse(player.getName());
        String response = removeResponse(player.getName());
        return !response.equals("-1");
    }

    @Override
    public int chooseDrawCard(Player player, List<Card> cards) {
        StringBuilder cardString = new StringBuilder();
        for (Card card : cards) {
            cardString
                .append(card.getName())
                .append("^")
                .append(card.getSuit().getLabel())
                .append("^")
                .append(card.getValue().getLabel())
                .append(", ");
        }
        sendMessage(player.getName(), "chooseDrawCard " + cardString);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public boolean chooseFromPlayer(Player player) {
        sendMessage(player.getName(), "chooseFromPlayer");
        waitForResponse(player.getName());
        return Boolean.parseBoolean(removeResponse(player.getName()));
    }

    @Override
    public int chooseGeneralStoreCard(Player player, List<Card> cards) {
        StringBuilder cardString = new StringBuilder();
        for (Card card : cards) {
            cardString.append(card.getName()).append(", ");
        }
        sendMessage(player.getName(), "chooseGeneralStoreCard " + cardString);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public List<Card> chooseTwoDiscardForLife(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card card : hand) {
            handCards.append(card.getName()).append(", ");
        }
        sendMessage(player.getName(), "chooseTwoDiscardForLife " + handCards);
        waitForResponse(player.getName());
        return makeCardList(removeResponse(player.getName()), player);
    }

    @Override
    public List<Card> chooseTwoDiscardForShoot(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card card : hand) {
            handCards.append(card.getName()).append(", ");
        }
        sendMessage(player.getName(), "chooseTwoDiscardForShoot " + handCards);
        waitForResponse(player.getName());
        return makeCardList(removeResponse(player.getName()), player);
    }

    @Override
    public void printInfo(String info) {
        messages.add(new MessageImpl(info));
    }

    @Override
    public int respondBang(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card value : hand) {
            CardName name = value.getName();
            boolean canPlay =
                CardName.BANG.equals(name)
                    || (CardName.MISSED.equals(name)
                    && Character.CALAMITYJANET.equals(player.getCharacter()));
            handCards.append(name).append("@").append(canPlay).append(", ");
        }
        sendMessage(player.getName(), "respondBang " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int respondBeer(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card value : hand) {
            CardName name = value.getName();
            boolean canPlay = CardName.BEER.equals(name);
            handCards.append(name).append("@").append(canPlay).append(", ");
        }
        sendMessage(player.getName(), "respondBeer " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int respondMiss(Player player, boolean canSingleUse) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card card : hand) {
            CardName name = card.getName();
            boolean canPlay =
                CardName.MISSED.equals(name)
                    || CardName.DODGE.equals(name)
                    || Character.ELENAFUENTE.equals(player.getCharacter())
                    || (CardName.BANG.equals(name)
                    && Character.CALAMITYJANET.equals(player.getCharacter()));
            handCards.append(name).append("@").append(canPlay).append(", ");
        }
        CardsInPlay cardsInPlay = player.getCardsInPlay();
        for (Card card : cardsInPlay) {
            CardName name = card.getName();
            // BELLESTAR
            boolean canPlay = false;
            if (canSingleUse) {
                canPlay = card instanceof SingleUseMissed;
            }
            handCards.append(name).append("@").append(canPlay).append(", ");
        }
        sendMessage(player.getName(), "respondMiss " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public GameState getGameState() {
        return turn.getGameState();
    }

    public void sendMessage(String player, String message) {
        messages.add(new MessageImpl(player + "-" + message));
    }

    @Override
    public List<Card> respondTwoMiss(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (Card value : hand) {
            boolean canPlay = false;
            CardName cardName = value.getName();
            if (CardName.MISSED.equals(cardName)
                || CardName.DODGE.equals(cardName)
                || Character.ELENAFUENTE.equals(player.getCharacter())
                || (CardName.BANG.equals(cardName)
                && Character.CALAMITYJANET.equals(player.getCharacter()))) {
                canPlay = true;
            }
            handCards.append(cardName).append("@").append(canPlay).append(", ");
        }
        CardsInPlay cardsInPlay = player.getCardsInPlay();
        for (Card card : cardsInPlay) {
            CardName name = card.getName();
            boolean canPlay = card instanceof SingleUseMissed;

            handCards.append(name).append("@").append(canPlay).append(", ");
        }
        // TODO add InHand Green Cards and BELLESTAR's ability
        sendMessage(player.getName(), "respondTwoMiss " + handCards);
        waitForResponse(player.getName());
        return makeCardList(removeResponse(player.getName()), player);
    }

    public String removeResponse(String playerName) {
        return responses.removeFirst().getMessage();
    }

    @Override
    public String getRoleForName(String name) {
        return turn.getRoleForName(name);
    }

    public String getGoalForName(String name) {
        return turn.roleToGoal(name);
    }

    @Nullable
    public GameState getGameState(boolean gameOver) {
        if (turn != null) {
            return turn.getGameState(gameOver);
        }
        return null;
    }

    @Nullable
    public String getTimeout() {
        return null;
    }

    public Hand getHandForUser(String playerName) {
        return turn.getPlayerForName(playerName)
            .map(Player::getHand)
            .orElseThrow(() -> new IllegalArgumentException("Player not found"));
    }

    public boolean isPlayerAlive(String playerName) {
        return turn.getPlayerForName(playerName).isPresent();
    }
}
