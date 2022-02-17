package com.chriscarr.bang.userinterface;

import com.chriscarr.bang.*;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.SingleUseMissed;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateListener;
import com.chriscarr.bang.models.game.Character;

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
                        //In Play Cards
                        InPlay inPlay = player.getInPlay();
                        inPlay.get(Integer.parseInt(s) - hand.size());
                        cardsToDiscard.add(inPlay.get(Integer.parseInt(s) - hand.size()));
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
        for (int i = 0; i < hand.size(); i++) {
            handCards.append(hand.get(i).getName()).append(", ");
        }
        sendMessage(player.getName(), "askDiscard " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int askBlueDiscard(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            handCards.append(hand.get(i).getName()).append(", ");
        }
        sendMessage(player.getName(), "askBlueDiscard " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int askOthersCard(Player player, InPlay inPlay, boolean hasHand) {
        StringBuilder inPlayCards = new StringBuilder();
        for (int i = 0; i < inPlay.count(); i++) {
            inPlayCards.append(inPlay.get(i).getName()).append(", ");
        }
        boolean hasGun = inPlay.hasGun();
        sendMessage(player.getName(), "askOthersCard " + hasHand + ", " + hasGun + inPlay.getGunName() + ", " + inPlayCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int askPlay(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            String name = card.getName();
            boolean canPlay = turn.canPlay(player, card);
            List<String> targets = turn.targets(player, card);
            StringBuilder targetString = new StringBuilder();
            for (String otherName : targets) {
                targetString.append(otherName).append("$");
            }
            handCards.append(name).append("^").append(Card.suitToString(card.getSuit())).append("^").append(Card.valueToString(card.getValue())).append("@").append(canPlay).append("@").append(targetString).append(", ");
        }
        InPlay inPlay = player.getInPlay();
        for (int i = 0; i < inPlay.size(); i++) {
            Card card = inPlay.get(i);
            if (card.getType() == Card.TYPESINGLEUSEITEM) {
                String name = card.getName();
                boolean canPlay = turn.canPlay(player, card);
                List<String> targets = turn.targets(player, card);
                StringBuilder targetString = new StringBuilder();
                for (String otherName : targets) {
                    targetString.append(otherName).append("$");
                }
                handCards.append(name).append("^").append(Card.suitToString(card.getSuit())).append("^").append(Card.valueToString(card.getValue())).append("@").append(canPlay).append("@").append(targetString).append(", ");
            }
        }
        if (Character.CHUCK_WENGAM.equals(player.getCharacter())) {
            handCards.append("loselifefor2cards" + "@true@").append(player.getName()).append("$").append(", ");
        }
        if (Character.JOSE_DELGADO.equals(player.getCharacter())) {
            handCards.append("discardbluetodraw2" + "@true@").append(player.getName()).append("$").append(", ");
        }
        if (Character.DOC_HOLYDAY.equals(player.getCharacter())) {
            handCards.append("discardtwotoshoot" + "@true@").append(player.getName()).append("$").append(", ");
        }
        if (Character.SID_KETCHUM.equals(player.getCharacter())) {
            handCards.append("discardtwoforlife" + "@true@").append(player.getName()).append("$").append(", ");
        }
        if (Character.UNCLE_WILL.equals(player.getCharacter())) {
            handCards.append("discardforgeneralstore" + "@true@").append(player.getName()).append("$").append(", ");
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
        String cardString = "";
        for (Card card : cards) {
            cardString += card.getName() + "^" + Card.suitToString(card.getSuit()) + "^" + Card.valueToString(card.getValue()) + ", ";
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
        String cardString = "";
        for (Card card : cards) {
            cardString += card.getName() + "^" + Card.suitToString(card.getSuit()) + "^" + Card.valueToString(card.getValue()) + ", ";
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
        String cardString = "";
        for (Card card : cards) {
            cardString += card.getName() + ", ";
        }
        sendMessage(player.getName(), "chooseGeneralStoreCard " + cardString);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public List<Card> chooseTwoDiscardForLife(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            handCards.append(hand.get(i).getName()).append(", ");
        }
        sendMessage(player.getName(), "chooseTwoDiscardForLife " + handCards);
        waitForResponse(player.getName());
        return makeCardList(removeResponse(player.getName()), player);
    }

    @Override
    public List<Card> chooseTwoDiscardForShoot(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            handCards.append(hand.get(i).getName()).append(", ");
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
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            String name = card.getName();
            boolean canPlay = Card.CARDBANG.equals(name) || (Card.CARDMISSED.equals(name) && Character.CALAMITY_JANET.equals(player.getCharacter()));
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
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            String name = card.getName();
            boolean canPlay = Card.CARDBEER.equals(name);
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
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            String name = card.getName();
            boolean canPlay = Card.CARDMISSED.equals(name) || Card.CARDDODGE.equals(name) || Character.ELENA_FUENTE.equals(player.getCharacter()) || (Card.CARDBANG.equals(name) && Character.CALAMITY_JANET.equals(player.getCharacter()));
            handCards.append(name).append("@").append(canPlay).append(", ");
        }
        InPlay inPlay = player.getInPlay();
        for (int i = 0; i < inPlay.size(); i++) {
            Card card = inPlay.get(i);
            String name = card.getName();
            //BELLESTAR
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
        for (int i = 0; i < hand.size(); i++) {
            boolean canPlay = false;
            String cardName = hand.get(i).getName();
            if (Card.CARDMISSED.equals(cardName) || Card.CARDDODGE.equals(cardName) || Character.ELENA_FUENTE.equals(player.getCharacter()) || (Card.CARDBANG.equals(cardName) && Character.CALAMITY_JANET.equals(player.getCharacter()))) {
                canPlay = true;
            }
            handCards.append(cardName).append("@").append(canPlay).append(", ");
        }
        InPlay inPlay = player.getInPlay();
        for (int i = 0; i < inPlay.size(); i++) {
            Card card = inPlay.get(i);
            String name = card.getName();
            boolean canPlay = card instanceof SingleUseMissed;

            handCards.append(name).append("@").append(canPlay).append(", ");
        }
        //TODO add InHand Green Cards and BELLESTAR's ability
        sendMessage(player.getName(), "respondTwoMiss " + handCards);
        waitForResponse(player.getName());
        return makeCardList(removeResponse(player.getName()), player);
    }

    public String removeResponse(String playerName) {
        return responses.remove(0).getMessage();
    }

    @Override
    public String getRoleForName(String name) {
        return turn.getRoleForName(name);
    }

    public String getGoalForName(String name) {
        return turn.roleToGoal(name);
    }

    public GameState getGameState(boolean gameOver) {
        if (turn != null) {
            return turn.getGameState(gameOver);
        }
        return null;
    }

    public String getTimeout() {
        return null;
    }

    public Hand getHandForUser(String playerName) {
        return turn.getPlayerForName(playerName).getHand();
    }

    public boolean isPlayerAlive(String playerName) {
        return turn.getPlayerForName(playerName) != null;
    }

}
