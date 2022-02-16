package com.chriscarr.bang.userinterface;

import com.chriscarr.bang.*;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.SingleUseMissed;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateListener;

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

    private List<Object> makeCardList(String remove, Player player) {
        List<Object> cardsToDiscard = new ArrayList<>();
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
            handCards.append(((Card) hand.get(i)).getName()).append(", ");
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
            handCards.append(((Card) hand.get(i)).getName()).append(", ");
        }
        sendMessage(player.getName(), "askBlueDiscard " + handCards);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public int askOthersCard(Player player, InPlay inPlay, boolean hasHand) {
        StringBuilder inPlayCards = new StringBuilder();
        for (int i = 0; i < inPlay.count(); i++) {
            inPlayCards.append(((Card) inPlay.get(i)).getName()).append(", ");
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
            Card card = (Card) hand.get(i);
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
            Card card = (Card) inPlay.get(i);
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
        if (Figure.CHUCKWENGAM.equals(player.getAbility())) {
            handCards.append("loselifefor2cards" + "@true@").append(player.getName()).append("$").append(", ");
        }
        if (Figure.JOSEDELGADO.equals(player.getAbility())) {
            handCards.append("discardbluetodraw2" + "@true@").append(player.getName()).append("$").append(", ");
        }
        if (Figure.DOCHOLYDAY.equals(player.getAbility())) {
            handCards.append("discardtwotoshoot" + "@true@").append(player.getName()).append("$").append(", ");
        }
        if (Figure.SIDKETCHUM.equals(player.getAbility())) {
            handCards.append("discardtwoforlife" + "@true@").append(player.getName()).append("$").append(", ");
        }
        if (Figure.UNCLEWILL.equals(player.getAbility())) {
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
    public int chooseCardToPutBack(Player player, List<Object> cards) {
        String cardString = "";
        for (Object card : cards) {
            cardString += ((Card) card).getName() + "^" + Card.suitToString(((Card) card).getSuit()) + "^" + Card.valueToString(((Card) card).getValue()) + ", ";
        }
        sendMessage(player.getName(), "chooseCardToPutBack " + cardString);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public boolean chooseDiscard(Player player, Object card) {
        sendMessage(player.getName(), "chooseDiscard " + ((Card) card).getName());
        waitForResponse(player.getName());
        String response = removeResponse(player.getName());
        return !response.equals("-1");
    }

    @Override
    public int chooseDrawCard(Player player, List<Object> cards) {
        String cardString = "";
        for (Object card : cards) {
            cardString += ((Card) card).getName() + "^" + Card.suitToString(((Card) card).getSuit()) + "^" + Card.valueToString(((Card) card).getValue()) + ", ";
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
    public int chooseGeneralStoreCard(Player player, List<Object> cards) {
        String cardString = "";
        for (Object card : cards) {
            cardString += ((Card) card).getName() + ", ";
        }
        sendMessage(player.getName(), "chooseGeneralStoreCard " + cardString);
        waitForResponse(player.getName());
        return Integer.parseInt(removeResponse(player.getName()));
    }

    @Override
    public List<Object> chooseTwoDiscardForLife(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            handCards.append(((Card) hand.get(i)).getName()).append(", ");
        }
        sendMessage(player.getName(), "chooseTwoDiscardForLife " + handCards);
        waitForResponse(player.getName());
        return makeCardList(removeResponse(player.getName()), player);
    }

    @Override
    public List<Object> chooseTwoDiscardForShoot(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            handCards.append(((Card) hand.get(i)).getName()).append(", ");
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
            Card card = (Card) hand.get(i);
            String name = card.getName();
            boolean canPlay = Card.CARDBANG.equals(name) || (Card.CARDMISSED.equals(name) && Figure.CALAMITYJANET.equals(player.getAbility()));
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
            Card card = (Card) hand.get(i);
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
            Card card = (Card) hand.get(i);
            String name = card.getName();
            boolean canPlay = Card.CARDMISSED.equals(name) || Card.CARDDODGE.equals(name) || Figure.ELENAFUENTE.equals(player.getAbility()) || (Card.CARDBANG.equals(name) && Figure.CALAMITYJANET.equals(player.getAbility()));
            handCards.append(name).append("@").append(canPlay).append(", ");
        }
        InPlay inPlay = player.getInPlay();
        for (int i = 0; i < inPlay.size(); i++) {
            Card card = (Card) inPlay.get(i);
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
    public List<Object> respondTwoMiss(Player player) {
        Hand hand = player.getHand();
        StringBuilder handCards = new StringBuilder();
        for (int i = 0; i < hand.size(); i++) {
            boolean canPlay = false;
            String cardName = ((Card) hand.get(i)).getName();
            if (Card.CARDMISSED.equals(cardName) || Card.CARDDODGE.equals(cardName) || Figure.ELENAFUENTE.equals(player.getAbility()) || (Card.CARDBANG.equals(cardName) && Figure.CALAMITYJANET.equals(player.getAbility()))) {
                canPlay = true;
            }
            handCards.append(cardName).append("@").append(canPlay).append(", ");
        }
        InPlay inPlay = player.getInPlay();
        for (int i = 0; i < inPlay.size(); i++) {
            Card card = (Card) inPlay.get(i);
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
