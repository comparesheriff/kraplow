package com.chriscarr.bang.userinterface;

import com.chriscarr.bang.CardsInPlay;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Role;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStatePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebGameUserInterface extends JSPUserInterface {
    private static final Logger LOG = LoggerFactory.getLogger(WebGameUserInterface.class);

    Map<String, List<Message>> messages;
    Map<String, List<Message>> responses;
    public Map<String, String> userFigureNames = null;
    Map<String, String> figureNamesUser = null;
    List<String> timedOutPlayers;
    boolean gameOver = false;
    String timeout = null;
    int aiSleepMs;
    List<String> infoHistory;

    @FunctionalInterface
    interface Sleeper {
        void sleep(long millis) throws InterruptedException;
    }

    private Sleeper sleeper = Thread::sleep;

    void setSleeper(Sleeper sleeper) {
        this.sleeper = Objects.requireNonNull(sleeper);
    }

    private int responsePollWaitMs = 100;
    private int responsePollMaxMs = 360_000;

    public void setResponsePollWaitMs(int responsePollWaitMs) {
        this.responsePollWaitMs = Math.max(1, responsePollWaitMs);
        this.responsePollMaxMs = Math.max(this.responsePollWaitMs, responsePollMaxMs);
    }

    public WebGameUserInterface(List<String> users, int aiSleepMs) {
        infoHistory = new ArrayList<>();
        timedOutPlayers = new ArrayList<>();
        this.aiSleepMs = aiSleepMs;
        messages = new ConcurrentHashMap<>();
        responses = new ConcurrentHashMap<>();
        for (String user : users) {
            messages.put(user, new ArrayList<>());
            responses.put(user, new ArrayList<>());
        }
    }

    //This is the AI logic
    public String somethingAI(String player, String message) {
        String lastMessage = "";
        if (!infoHistory.isEmpty()) {
            lastMessage = infoHistory.getLast();
        }

        try {
            sleeper.sleep(this.aiSleepMs);
        } catch (InterruptedException ignored) {
        }

        Optional<Player> aiPlayerOptional = turn.getPlayerForName(player);
        if (aiPlayerOptional.isEmpty()) {
            throw new RuntimeException("Player " + player + " not found");
        }
        Player aiPlayer = aiPlayerOptional.get();
        if (message.indexOf("askOthersCard") == 0) {
            String[] splitMessage = message.split(",");

            for (int i = 2; i < splitMessage.length - 1; i++) {
                if (!splitMessage[i].equals(" Jail") && !splitMessage[i].equals(" Dynamite")) {
                    return Integer.toString(i - 2);
                }
            }

            if (splitMessage[0].contains("true")) {
                return "-1";
            }
            if (splitMessage[1].contains("true")) {
                return "-2";
            }
            return "0";
        } else if (message.indexOf("chooseDiscard") == 0
                || message.indexOf("chooseFromPlayer") == 0) {
            return "false";
        } else if (message.indexOf("askDiscard") == 0) {
            String commandStripped = message.replace("askDiscard ", "");
            CardName[] cards = Arrays.stream(commandStripped.split(", ")).map(CardName::valueOf).toArray(CardName[]::new);
            if (turn.countPlayers() == 2) {
                for (int i = 0; i < cards.length - 1; i++) {
                    if (cards[i].equals(CardName.BEER)) {
                        return Integer.toString(i);
                    }
                }
                for (int i = 0; i < cards.length - 1; i++) {
                    if (!aiPlayer.isSheriff() && cards[i].equals(CardName.JAIL)) {
                        return Integer.toString(i);
                    }
                }
            }
            for (int i = 0; i < cards.length - 1; i++) {
                CardsInPlay cardsInPlay = aiPlayer.getCardsInPlay();
                if (cardsInPlay.hasItem(cards[i]) || cardsInPlay.getGunName().equals(cards[i])) {
                    return Integer.toString(i);
                }
            }
            for (int i = 0; i < cards.length - 1; i++) {
                if (isGun(cards[i]) && isThisGunBetter(cards[i], aiPlayer.getCardsInPlay().getGunName())) {
                    return Integer.toString(i);
                }
            }
            for (int i = 0; i < cards.length - 1; i++) {
                if (!(cards[i].equals(CardName.BEER) || cards[i].equals(CardName.MISSED) || cards[i].equals(CardName.BANG))) {
                    return Integer.toString(i);
                }
            }
            return "0";
        } else if (message.indexOf("chooseGeneralStoreCard") == 0
                || message.indexOf("chooseDrawCard") == 0
                || message.indexOf("chooseCardToPutBack") == 0) {
            return "0";
        } else if (message.indexOf("askPlayer") == 0) {
            String commandStripped = message.replace("askPlayer ", "");
            String dollarsReplaced = commandStripped.replace(", ", "$");
            int playerToHurt = whoToHurt(aiPlayer, dollarsReplaced);
            return Integer.toString(playerToHurt);
        } else if (message.indexOf("chooseTwoDiscardForLife") == 0
                || message.indexOf("respondTwoMiss") == 0) {
            return "-1";
        } else if (message.indexOf("respondMiss") == 0) {
            String options = message.replace("respondMiss", "");
            String[] cards = options.split(",");
            for (int i = 0; i < cards.length - 1; i++) {
                String card = cards[i].trim();
                if (card.indexOf("Missed!") == 0) {
                    return Integer.toString(i);
                }
            }
            return "-1";
        } else if (message.indexOf("respondBang") == 0) {
            //TODO if this is a duel, and you are a deputy, and the other is the sheriff, take the hit
            int duelIndex = lastMessage.indexOf(" duels ");
            if (duelIndex != -1) {
                String otherPlayer = lastMessage.substring(0, duelIndex);
                if (aiPlayer.getRole() == Role.DEPUTY && turn.getPlayerForName(otherPlayer).map(other -> other.getRole() == Role.SHERIFF).orElse(false)) {
                    //Let the sheriff kill you(Not great for the sheriff)
                    return "-1";
                }
            }

            String options = message.replace("respondBang", "");
            String[] cards = options.split(",");
            for (int i = 0; i < cards.length - 1; i++) {
                String card = cards[i].trim();
                if (card.indexOf("Shoot") == 0) {
                    return Integer.toString(i);
                }
            }
            return "-1";
        } else if (message.indexOf("respondBeer") == 0) {
            String options = message.replace("respondBeer", "");
            String[] cards = options.split(",");
            for (int i = 0; i < cards.length - 1; i++) {
                String card = cards[i].trim();
                if (card.indexOf("Beer") == 0) {
                    return Integer.toString(i);
                }
            }
            return "-1";
        } else if (message.indexOf("askPlay") == 0
                && message.indexOf("askPlayer") != 0) {
            String options = message.replace("askPlay", "");
            String[] cards = options.split(",");
            for (int i = 0; i < cards.length - 1; i++) {
                String card = cards[i].trim();
                if (card.indexOf("Stagecoach") == 0) {
                    return Integer.toString(i);
                }
                if (card.indexOf("Remington") == 0) {
                    if (!aiPlayer.hasGun() || aiPlayer.isInPlay(CardName.VOLCANIC) || aiPlayer.isInPlay(CardName.SCHOFIELD)) {
                        if (!aiPlayer.isInPlay(CardName.REMINGTON)) {
                            return Integer.toString(i);
                        }
                    }
                }
                if (card.indexOf("Scope") == 0) {
                    if (!aiPlayer.isInPlay(CardName.SCOPE)) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Mustang") == 0) {
                    if (!aiPlayer.isInPlay(CardName.MUSTANG)) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Barrel") == 0) {
                    if (!aiPlayer.isInPlay(CardName.BARREL)) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Beer") == 0) {
                    if (aiPlayer.getHealth() < aiPlayer.getMaxHealth()) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Dynamite") == 0) {
                    if (!aiPlayer.isInPlay(CardName.DYNAMITE)) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Schofield") == 0) {
                    if (!aiPlayer.hasGun() || aiPlayer.isInPlay(CardName.VOLCANIC)) {
                        if (!aiPlayer.isInPlay(CardName.SCHOFIELD)) {
                            return Integer.toString(i);
                        }
                    }
                }
                if (card.indexOf("Volcanic") == 0) {
                    if (!aiPlayer.hasGun() && !aiPlayer.isInPlay(CardName.VOLCANIC)) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Winchester") == 0) {
                    if (!aiPlayer.isInPlay(CardName.WINCHESTER)) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Rev. Carbine") == 0) {
                    if (!aiPlayer.hasGun() || aiPlayer.isInPlay(CardName.VOLCANIC) || aiPlayer.isInPlay(CardName.SCHOFIELD) || aiPlayer.isInPlay(CardName.REMINGTON)) {
                        if (!aiPlayer.isInPlay(CardName.REV_CARBINE)) {
                            return Integer.toString(i);
                        }
                    }
                }
                if (card.indexOf("Wells Fargo") == 0) {
                    return Integer.toString(i);
                }
                if (card.indexOf("General Store") == 0) {
                    return Integer.toString(i);
                }
                if (card.indexOf("Panic!@true") == 0) {
                    String[] splitCard = card.split("@");
                    if (whoToHurtCardTake(aiPlayer, splitCard[2], true) != -1) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Cat Balou@true") == 0) {

                    String[] splitCard = card.split("@");
                    if (whoToHurtCardTake(aiPlayer, splitCard[2], true) != -1) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Indians!") == 0) {
                    if (hurtEveryone(aiPlayer)) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Gatling") == 0) {
                    if (hurtEveryone(aiPlayer)) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Saloon") == 0) {
                    if (healEveryone(aiPlayer)) {
                        return Integer.toString(i);
                    }

                }
                if (card.indexOf("Shoot@true") == 0) {
                    String[] splitCard = card.split("@");
                    if (whoToHurt(aiPlayer, splitCard[2]) != -1) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Missed!@true") == 0) {
                    String[] splitCard = card.split("@");
                    if (whoToHurt(aiPlayer, splitCard[2]) != -1) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Jail@true") == 0) {
                    String[] splitCard = card.split("@");
                    if (whoToHurt(aiPlayer, splitCard[2]) != -1) {
                        return Integer.toString(i);
                    }
                }
                if (card.indexOf("Duel@true") == 0) {
                    //Don't duel on one health
                    if (aiPlayer.getHealth() > 1) {
                        String[] splitCard = card.split("@");
                        if (whoToHurt(aiPlayer, splitCard[2]) != -1) {
                            return Integer.toString(i);
                        }
                    }
                }

            }
            return "-1";
        }
        return null;
    }

    private boolean isGun(CardName cardName) {
        return cardName.equals(CardName.VOLCANIC) || cardName.equals(CardName.SCHOFIELD) || cardName.equals(CardName.REMINGTON) || cardName.equals(CardName.REV_CARBINE) || cardName.equals(CardName.WINCHESTER);
    }

    private boolean isThisGunBetter(CardName thisGun, CardName thatGun) {
        Map<CardName, Integer> gunRank = new HashMap<>();
        gunRank.put(CardName.COLT, 0);
        gunRank.put(CardName.VOLCANIC, 1);
        gunRank.put(CardName.SCHOFIELD, 2);
        gunRank.put(CardName.REMINGTON, 3);
        gunRank.put(CardName.REV_CARBINE, 4);
        gunRank.put(CardName.WINCHESTER, 5);
        return gunRank.get(thisGun) - gunRank.get(thatGun) > 0;
    }


    public boolean hurtEveryone(Player player) {
        Role role = player.getRole();
        if (role == Role.DEPUTY || (role == Role.RENEGADE && turn.countPlayers() > 2)) {
            return turn.getSheriff().map(s -> s.getHealth() > 3).orElse(false);
        }
        return true;
    }

    public boolean healEveryone(Player player) {
        Role role = player.getRole();
        if (role == Role.DEPUTY || (role == Role.RENEGADE && turn.countPlayers() > 2)) {
            return turn.getSheriff().map(s -> s.getHealth() < 3).orElse(false);
        }
        return player.getHealth() < player.getMaxHealth();
    }

    public boolean playerGotCardIWantToTake(Player me, Player them) {
        if (them.getHandSize() > 0) {
            return true;
        } else if (them.hasGun()) {
            return true;
        }
        CardsInPlay inPlay = them.getCardsInPlay();
        int cardsInPlay = inPlay.size();
        if (inPlay.hasItem(CardName.JAIL)) {
            cardsInPlay--;
        }
        if (inPlay.hasItem(CardName.DYNAMITE)) {
            cardsInPlay--;
        }
        return cardsInPlay > 0;
    }

    public int whoToHurt(Player player, String namesString) {
        return whoToHurtCardTake(player, namesString, false);
    }

    public int whoToHurtCardTake(Player player, String namesString, boolean takeCard) {
        ArrayList<Integer> targets = new ArrayList<>();
        Role role = player.getRole();
        String[] names = namesString.split("\\$");
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            name = name.trim();
            if (!name.equals("Cancel")) {
                Optional<Player> otherOptional = turn.getPlayerForName(name);
                if (otherOptional.isEmpty()) {
                    throw new RuntimeException("Player " + name + " not found");
                }
                Player other = otherOptional.get();
                Role otherRole = other.getRole();
                if (role == Role.OUTLAW && otherRole == Role.SHERIFF) {
                    if (!takeCard || playerGotCardIWantToTake(player, other)) {
                        return i;
                    }
                }
                if (role == Role.DEPUTY && otherRole != Role.SHERIFF) {
                    if (!takeCard || playerGotCardIWantToTake(player, other)) {
                        targets.add(i);
                    }
                }
                if (role == Role.SHERIFF) {
                    if (!takeCard || playerGotCardIWantToTake(player, other)) {
                        targets.add(i);
                    }
                }
                if (role == Role.RENEGADE && (turn.countPlayers() == 2 || otherRole != Role.SHERIFF)) {
                    if (!takeCard || playerGotCardIWantToTake(player, other)) {
                        targets.add(i);
                    }
                }
            }
        }
        for (int i = 0; i < names.length - 1; i++) {
            if (!names[i].equals("Cancel")) {
                if (role == Role.OUTLAW) {
                    if (!takeCard || turn.getPlayerForName(names[i]).map(other -> playerGotCardIWantToTake(player, other)).orElse(false)) {
                        targets.add(i);
                    }
                }
            }
        }
        if (targets.isEmpty()) {
            return -1;
        } else {
            //Random target instead of first
            Collections.shuffle(targets);
            return targets.getFirst();
        }
    }

    public void sendMessage(String player, String message) {
        //Game loop already started before maps are made
        if (userFigureNames == null) {
            setupMap();
        }

        List<Message> playerMessages = messages
                .get(userFigureNames.get(player));
        playerMessages.add(new MessageImpl(player + "-" + message));
        if (userFigureNames.get(player).contains("AI") || timedOutPlayers.contains(userFigureNames.get(player))) {
            while (messages.isEmpty()) {
                //REFACTORING - should something be happening here?
            }
            addResponse(userFigureNames.get(player), somethingAI(player,
                    message));
        }
    }

    public void addResponse(String user, String message) {
        LOG.info("Response " + user + " " + message);
        if (!getMessages(user).isEmpty()) {
            LOG.info("Response " + getMessages(user).getFirst());
        }
        List<Message> playerResponses = responses.get(user);
        playerResponses.add(new MessageImpl(message));
    }

    synchronized public void printInfo(String info) {
        Set<String> keys = messages.keySet();
        for (String key : keys) {
            if (!key.contains("AI")) {
                List<Message> playerMessages = messages.get(key);
                playerMessages.add(new MessageImpl(info));
            }
        }
        infoHistory.add(info);
    }

    public List<Message> getMessages(String user) {
        return messages.get(user);
    }

    public GameState getGameState() {
        return super.getGameState(gameOver);
    }

    private void setupMap() {
        GameState gameState = super.getGameState();
        List<GameStatePlayer> players = gameState.getPlayers();
        Set<String> keys = messages.keySet();
        Iterator<String> userIter = keys.iterator();
        userFigureNames = new ConcurrentHashMap<>();
        figureNamesUser = new ConcurrentHashMap<>();
        //If there is only one human put them first so they can get there prefered role and character if chosen
        //More than one leave it mixed
        int humanCount = 0;
        ArrayList<String> humanFirstList = new ArrayList<>();
        ArrayList<String> sameOrderList = new ArrayList<>();
        while (userIter.hasNext()) {
            String user = userIter.next();
            sameOrderList.add(user);
            if (user.length() >= 2 && ("AI".equals(user.substring(user.length() - 2)))) {
                humanFirstList.add(user);
            } else {
                humanFirstList.addFirst(user);
                humanCount += 1;
            }
        }

        ArrayList<String> usersList;
        if (humanCount == 1) {
            usersList = humanFirstList;
        } else {
            usersList = sameOrderList;
        }
        int userIndex = 0;
        for (GameStatePlayer player : players) {
            String user = usersList.get(userIndex);
            userFigureNames.put(player.name, user);
            figureNamesUser.put(user, player.name);
            userIndex += 1;
        }
    }

    protected void waitForResponse(String player) {
        int waitCount = 0;
        while (responses.get(userFigureNames.get(player)).isEmpty()) {
            try {
                // polling ohne echten Sleep in Tests (Sleeper ist injizierbar)
                sleeper.sleep(responsePollWaitMs);
                waitCount += responsePollWaitMs;
                if (waitCount > responsePollMaxMs) {
                    printInfo(player + " has timed out and AI has taken over for them.");
                    timedOutPlayers.add(userFigureNames.get(player));
                    //-5 is never a valid response, but it will trigger the AI to make a valid one
                    addResponse(userFigureNames.get(player), "-5");
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    public String removeResponse(String player) {
        return responses.get(userFigureNames.get(player)).removeFirst()
                .getMessage();
    }

    public String getPlayerForUser(String user) {
        return figureNamesUser.get(user);
    }

    public String getTimeout() {
        return timeout;
    }
}
