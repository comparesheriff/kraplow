package com.chriscarr.bang.turn;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.*;
import com.chriscarr.bang.gamestate.*;
import com.chriscarr.bang.services.GameOverService;
import com.chriscarr.bang.userinterface.UserInterface;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Turn {
    private static final Logger LOG = LoggerFactory.getLogger(Turn.class);

    private List<Player> players;
    private Player currentPlayer;
    private UserInterface userInterface;
    private boolean donePlaying = false;
    private Discard discard;
    private Deck deck;
    private int bangsPlayed = 0;
    private int joseActions = 0;
    private int uncleWillActions = 0;

    public ArrayList<String> getRoles() {
        ArrayList<String> roles = new ArrayList<>();
        for (Player player : players) {
            roles.add(player.getRole().getRoleName());
        }
        Collections.sort(roles);
        return roles;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player getPlayersTurn() {
        return currentPlayer;
    }

    public int countPlayers() {
        return players.size();
    }

    public Optional<Player> getPlayerForName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

    public static Player getNextPlayer(Player player, List<Player> players) {
        int index = players.indexOf(player);
        if (index == players.size() - 1) {
            index = 0;
        } else {
            index = index + 1;
        }
        return players.get(index);
    }

    public static Player getPreviousPlayer(Player player, List<Player> players) {
        int index = players.indexOf(player);
        if (index == 0) {
            index = players.size() - 1;
        } else {
            index = index - 1;
        }
        return players.get(index);
    }

    public void nextTurn() {
        // Log of all cards
    /*
    int cardTotal = 0;
    cardTotal += deck.size();
    cardTotal += discard.size();
    int handTotal = 0;
    int inPlayTotal = 0;
    for(int i = 0; i < players.size(); i++) {
    	Player countPlayer = players.get(i);
    	Hand testHand = countPlayer.getHand();
    	cardTotal += testHand.size();
    	handTotal += testHand.size();
    	InPlay testInPlay = countPlayer.getInPlay();
    	cardTotal += testInPlay.size();
    	inPlayTotal += testInPlay.size();
    	if(testInPlay.hasGun()) {
    		cardTotal += 1;
    		inPlayTotal += 1;
    	}
    }
    userInterface.printInfo("Card Total: "+cardTotal + " deck: "+deck.size()+" discard: "+discard.size()+" hands: "+ handTotal+" inplays: "+inPlayTotal);
    */
        // End log all cards

        currentPlayer = getNextPlayer(currentPlayer, players);
        setDonePlaying(false);
        bangsPlayed = 0;
        turnLoop(currentPlayer);
    }

    private void turnLoop(Player currentPlayer) {
        this.joseActions = 0;
        this.uncleWillActions = 0;
        TurnContext ctx = TurnContext.of(deck, discard, players, userInterface)
            .withCurrentPlayer(currentPlayer)
            .withApi(TurnApi.of(
                Turn::pullCards,
                Turn::chooseValidCardToPutBack,
                Turn::getValidChosenPlayer,
                Turn::getNextPlayer,
                Turn::draw,
                this::damagePlayer,
                this::isDonePlaying,
                this::play,
                this::setDonePlaying
            ))
            .withInJail(false);
        try {
            new UpkeepPhase().carryOut(ctx);
            if (!ctx.inJail() && players.contains(currentPlayer)) {
                new DrawPhase().carryOut(ctx);
                new MainPhase().carryOut(ctx);
            }
        } catch (EndOfGameException e) {
            return;
        }
        if (players.contains(currentPlayer)) {
            if (!ctx.inJail()) {
                new DiscardPhase().carryOut(ctx);
            }
        }
        nextTurn();
    }

    public void setSheriff() {
        for (Player player : players) {
            if (player.getRole() == Role.SHERIFF) {
                currentPlayer = player;
                turnLoop(currentPlayer);
            }
        }
    }

    public void setSheriffManualTest() {
        for (Player player : players) {
            if (player.getRole() == Role.SHERIFF) {
                currentPlayer = player;
            }
        }
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void play(TurnContext ctx) {
        for (Player player : players) {
            if (Character.SUZYLAFAYETTE.equals(player.getCharacter())) {
                Hand playerHand = player.getHand();
                if (playerHand.isEmpty()) {
                    if (deck.isEmpty()) {
                        userInterface.printInfo("Shuffling the deck");
                    }
                    playerHand.add(deck.pull());
                    userInterface.printInfo(player.getName() + " ran out of cards and drew a card.");
                }
            }
        }
        Hand hand = currentPlayer.getHand();
        int card = -2;
        CardsInPlay allCardsInPlay = currentPlayer.getCardsInPlay();
        ArrayList<SingleUse> singleUseInPlay = new ArrayList<>();
        for (Card inPlayCard : allCardsInPlay) {
            if (inPlayCard instanceof SingleUse singleUseInplayCard) {
                singleUseInPlay.add(singleUseInplayCard);
            }
        }
        while (card < -1 || card > hand.size() + singleUseInPlay.size() - 1) {
            card = userInterface.askPlay(currentPlayer);
            if (card > (hand.size() + singleUseInPlay.size() - 1)
                && Character.CHUCKWENGAM.equals(currentPlayer.getCharacter())) {
                if (currentPlayer.getHealth() > 1) {
                    currentPlayer.removeHealth(1);
                    Hand playerHand = currentPlayer.getHand();
                    playerHand.add(deck.pull());
                    playerHand.add(deck.pull());
                    userInterface.printInfo(currentPlayer.getName() + " traded one life for 2 cards.");
                    return;
                }
            } else if (card > (hand.size() + singleUseInPlay.size() - 1)
                && Character.SIDKETCHUM.equals(currentPlayer.getCharacter())) {
                discardTwoCardsForLife(currentPlayer, discard, userInterface);
            } else if (card > (hand.size() - 1)
                && Character.JOSEDELGADO.equals(currentPlayer.getCharacter())) {
                if (this.joseActions >= 2) {
                    userInterface.printInfo("Already used special abilitity twice this turn.");
                    return;
                }
                int cardIndex = userInterface.askBlueDiscard(currentPlayer);
                if (cardIndex == -1) {
                    return;
                }
                Card playedCard = hand.get(cardIndex);
                if (playedCard.getType() == CardType.GUN || playedCard.getType() == CardType.ITEM) {
                    this.joseActions += 1;
                    hand.remove(cardIndex);
                    discard.add(playedCard);
                    hand.add(deck.pull());
                    hand.add(deck.pull());
                    userInterface.printInfo(currentPlayer.getName() + " traded one blue card for 2 cards.");
                    return;
                }
            } else if (card > (hand.size() - 1)
                && Character.UNCLEWILL.equals(currentPlayer.getCharacter())) {
                if (this.uncleWillActions >= 1) {
                    userInterface.printInfo("Already used special abilitity this turn.");
                    return;
                }
                int cardIndex = userInterface.askDiscard(currentPlayer);
                if (cardIndex == -1) {
                    return;
                }
                Card removedCard = currentPlayer.getHand().remove(cardIndex);
                discard.add(removedCard);
                this.uncleWillActions += 1;

                List<Card> generalStoreCards = new ArrayList<>();
                for (int i = 0; i < players.size(); i++) {
                    if (deck.isEmpty()) {
                        userInterface.printInfo("Shuffling the deck");
                    }
                    generalStoreCards.add(deck.pull());
                }
                Player generalPlayer = currentPlayer;
                while (!generalStoreCards.isEmpty()) {
                    int chosenCard = -1;
                    while (chosenCard < 0 || chosenCard > generalStoreCards.size() - 1) {
                        chosenCard = userInterface.chooseGeneralStoreCard(generalPlayer, generalStoreCards);
                    }
                    Card storeCard = generalStoreCards.remove(chosenCard);
                    userInterface.printInfo(
                        generalPlayer.getName()
                            + " chooses "
                            + storeCard.getName()
                            + " from "
                            + CardName.GENERAL_STORE);
                    generalPlayer.getHand().add(storeCard);
                    generalPlayer = Turn.getNextPlayer(generalPlayer, players);
                }
                return;

            } else if (card > (hand.size() + singleUseInPlay.size() - 1)
                && Character.DOCHOLYDAY.equals(currentPlayer.getCharacter())) {
                List<Card> cardsToDiscard = userInterface.chooseTwoDiscardForShoot(currentPlayer);
                if (cardsToDiscard.size() == 2) {
                    CardSuit discardSuit = CardSuit.DIAMONDS;
                    for (Card discardcard : cardsToDiscard) {
                        if (discardcard.getSuit() != CardSuit.DIAMONDS) {
                            discardSuit = discardcard.getSuit();
                        }
                        hand.remove(discardcard);
                        discard.add(discardcard);
                        userInterface.printInfo(
                            currentPlayer.getName() + " discards " + discardcard.getName() + " for shoot.");
                    }
                    Bang tempBang = new Bang(CardName.BANG, discardSuit, CardValue.SEVEN, CardType.PLAY);
                    boolean success =
                        tempBang.play(currentPlayer, players, userInterface, deck, discard, this, true);
                    if (!success) {
                        hand.add(discard.removeLast());
                        hand.add(discard.removeLast());
                    }
                    return;
                }
            }
        }
        if (hand.size() + singleUseInPlay.size() == 0 || card == -1) {
            ctx.api().setDonePlaying(true);
            userInterface.printInfo(currentPlayer.getName() + " is finished playing.");
            CardsInPlay allCardsInPlayActivate = currentPlayer.getCardsInPlay();
            for (int i = 0; i < allCardsInPlayActivate.size(); i++) {
                Card inPlayCardActivate = allCardsInPlay.get(i);
                if (inPlayCardActivate instanceof SingleUse cardActivate) {
                    cardActivate.setReadyToPlay(true);
                }
            }
            return;
        }
        if (card >= hand.size()) {
            int chosen = card - hand.size();
            singleUseInPlay.get(chosen).play(currentPlayer, players, userInterface, deck, discard, this);
        } else {
            Card playedCard = hand.get(card);
            if (playedCard.canPlay(currentPlayer, players, bangsPlayed)) {
                hand.remove(card);
                if (Objects.equals(playedCard.getName(), CardName.GENERAL_STORE)
                    || Objects.equals(playedCard.getName(), CardName.GATLING)
                    || Objects.equals(playedCard.getName(), CardName.INDIANS)) {
                    userInterface.printInfo(
                        currentPlayer.getName() + " played a " + playedCard.getName() + ".");
                }
                boolean success = playedCard.play(currentPlayer, players, userInterface, deck, discard, this);
                if (success) {
                    if (playedCard instanceof Bang) {
                        bangsPlayed++;
                    }
                }
                if (!Objects.equals(playedCard.getName(), CardName.CAT_BALOU)
                    && !Objects.equals(playedCard.getName(), CardName.PANIC)
                    && !Objects.equals(playedCard.getName(), CardName.JAIL)
                    && !Objects.equals(playedCard.getName(), CardName.MISSED)
                    && !Objects.equals(playedCard.getName(), CardName.BANG)
                    && !Objects.equals(playedCard.getName(), CardName.DUEL)
                    && !Objects.equals(playedCard.getName(), CardName.GENERAL_STORE)
                    && !Objects.equals(playedCard.getName(), CardName.GATLING)
                    && !Objects.equals(playedCard.getName(), CardName.INDIANS)) {
                    userInterface.printInfo(
                        currentPlayer.getName() + " played a " + playedCard.getName() + ".");
                }
            }
        }
    }

    public static int validPlayMiss(
        Player player, UserInterface userInterface, boolean canSingleUse) {
        while (true) {
            int playedMiss = userInterface.respondMiss(player, canSingleUse);
            if (playedMiss == -1) {
                return playedMiss;
            } else {
                Hand hand = player.getHand();
                if (playedMiss < hand.size()) {
                    Card card = hand.get(playedMiss);
                    if (CardName.MISSED.equals(card.getName())) {
                        return playedMiss;
                    } else if (CardName.DODGE.equals(card.getName())) {
                        return playedMiss;
                    } else if (CardName.BANG.equals(card.getName())
                        && Character.CALAMITYJANET.equals(player.getCharacter())) {
                        return playedMiss;
                    } else if (Character.ELENAFUENTE.equals(player.getCharacter())) {
                        return playedMiss;
                    }
                } else {
                    return playedMiss;
                }
            }
        }
    }

    static int validPlayBeer(Player player, UserInterface userInterface) {
        while (true) {
            int playedBeer = userInterface.respondBeer(player);
            if (playedBeer == -1 || CardName.BEER.equals(player.getHand().get(playedBeer).getName())) {
                return playedBeer;
            }
        }
    }

    public static int validPlayBang(Player player, UserInterface userInterface) {
        while (true) {
            int playerShot = userInterface.respondBang(player);
            if (playerShot == -1) {
                return playerShot;
            } else {
                Hand hand = player.getHand();
                Card card = hand.get(playerShot);
                if (CardName.BANG.equals(card.getName())) {
                    return playerShot;
                } else if (CardName.MISSED.equals(card.getName())
                    && Character.CALAMITYJANET.equals(player.getCharacter())) {
                    return playerShot;
                }
            }
        }
    }

    public boolean isDonePlaying() {
        return donePlaying;
    }

    public void setDonePlaying(boolean donePlaying) {
        this.donePlaying = donePlaying;
    }

    public void setDiscard(Discard discard) {
        this.discard = discard;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public static Card draw(Player player, Deck deck, Discard discard, UserInterface userInterface) {
        if (Character.LUCKYDUKE.equals(player.getCharacter())) {
            List<Card> cards = pullCards(deck, 2, userInterface);
            int chosenCard = -1;
            while (chosenCard < 0 || chosenCard > (cards.size() - 1)) {
                chosenCard = userInterface.chooseDrawCard(player, cards);
            }
            discard.addAll(cards);
            Card drawnCard = cards.get(chosenCard);
            userInterface.printInfo(
                player.getName()
                    + " drew a "
                    + drawnCard.getValue().getLabel()
                    + " of "
                    + drawnCard.getSuit().getLabel()
                    + " "
                    + drawnCard.getName());
            return cards.get(chosenCard);
        } else {
            if (deck.isEmpty()) {
                userInterface.printInfo("Shuffling the deck");
            }
            Card card = deck.pull();
            userInterface.printInfo(
                player.getName()
                    + " drew a "
                    + card.getValue().getLabel()
                    + " of "
                    + card.getSuit().getLabel()
                    + " "
                    + card.getName());
            discard.add(card);
            return card;
        }
    }

    public static int isBarrelSave(
        Player player,
        Deck deck,
        Discard discard,
        UserInterface userInterface,
        int missesRequired,
        Player shooter) {
        int misses = 0;
        if (Character.JOURDONNAIS.equals(player.getCharacter())) {
            userInterface.printInfo(player.getName() + " is drawing to be saved by a barrel");
            Card drawn = draw(player, deck, discard, userInterface);
            if (drawn.getSuit() == CardSuit.HEARTS) {
                misses = misses + 1;
                userInterface.printInfo(
                    player.getCharacter().getName()
                        + " drew a "
                        + CardSuit.HEARTS.getLabel()
                        + " and was saved by his ability.");
            } else {
                userInterface.printInfo(
                    player.getCharacter().getName()
                        + " drew a "
                        + drawn.getSuit().getLabel()
                        + " and was not saved by his ability.");
            }
        }
        if (misses >= missesRequired) {
            return misses;
        }
        CardsInPlay currentCardsInPlay = player.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.BARREL)) {
            if (Character.BELLESTAR.equals(shooter.getCharacter())) {
                userInterface.printInfo(
                    player.getName() + "'s barrel has no affect on " + shooter.getCharacter().getName());
            } else {
                userInterface.printInfo(player.getName() + " is drawing to be saved by a barrel");
                Card drawn = draw(player, deck, discard, userInterface);
                if (drawn.getSuit() == CardSuit.HEARTS) {
                    misses = misses + 1;
                    userInterface.printInfo(
                        player.getName()
                            + " drew a "
                            + CardSuit.HEARTS.getLabel()
                            + " and was saved by their barrel.");
                } else {
                    userInterface.printInfo(
                        player.getName()
                            + " drew a "
                            + drawn.getSuit().getLabel()
                            + " and was not saved by their barrel.");
                }
            }
        }
        return misses;
    }

    public void damagePlayer(
        Player player,
        List<Player> players,
        Player currentPlayer,
        int damage,
        Player damager,
        Deck deck,
        Discard discard,
        UserInterface userInterface) {
        player.removeHealth(damage);
        if (player.getHealth() <= 0 && players.size() > 2) {
            boolean doNotPlayBeer = false;
            while (!doNotPlayBeer && player.getHealth() <= 0) {
                int playedBeer = validPlayBeer(player, userInterface);
                if (playedBeer != -1) {
                    if (Character.TEQUILAJOE.equals(player.getCharacter())) {
                        player.addHealth(2);
                        discard.add(player.getHand().remove(playedBeer));
                        userInterface.printInfo(player.getName() + " plays a beer and gains two lives.");
                    } else {
                        player.addHealth(1);
                        discard.add(player.getHand().remove(playedBeer));
                        userInterface.printInfo(player.getName() + " plays a beer and gains one life.");
                        if (Character.MOLLYSTARK.equals(player.getCharacter())) {
                            Hand otherHand = player.getHand();
                            otherHand.add(deck.pull());
                            userInterface.printInfo(player.getName() + " draws a card");
                        }
                    }
                } else {
                    doNotPlayBeer = true;
                }
            }
        }
        if (player.getHealth() <= 0) {
            handleDeath(player, damager, currentPlayer, players, userInterface, deck, discard);
        } else {
            if (Character.BARTCASSIDY.equals(player.getCharacter())) {
                for (int i = 0; i < damage; i++) {
                    if (deck.isEmpty()) {
                        userInterface.printInfo("Shuffling the deck");
                    }
                    player.getHand().add(deck.pull());
                    userInterface.printInfo(
                        player.getCharacter().getName()
                            + " draws a card from the deck because he was damaged.");
                }
            } else if (damager != null && Character.ELGRINGO.equals(player.getCharacter())) {
                Hand otherHand = damager.getHand();
                if (!otherHand.isEmpty()) {
                    Hand playerHand = player.getHand();
                    otherHand.removeRandom().ifPresent(playerHand::add);
                    userInterface.printInfo(
                        player.getCharacter().getName()
                            + " draws a card from "
                            + damager.getName()
                            + " because he was damaged.");
                }
            }
        }
    }

    public void handleDeath(
        Player player,
        Player damager,
        Player currentPlayer,
        List<Player> players,
        UserInterface userInterface,
        Deck deck,
        Discard discard) {
        // I think we can remove this
        // if (player.equals(currentPlayer)) {
        // TODO is this the right way to handle all death... set it to last player?
        // currentPlayer = getPreviousPlayer(currentPlayer, players);
        // }
        players.remove(player);
        userInterface.printInfo(
            player.getName() + " is dead. Role was " + player.getRole().getRoleName());
        if (!GameOverService.isGameOver(players)) {
            deadDiscardAll(player, players, discard, deck);
            if (damager != null) {
                if (damager.getRole() == Role.SHERIFF && player.getRole() == Role.DEPUTY) {
                    userInterface.printInfo(damager.getName() + " killed own deputy, loses all cards");
                    discardAll(damager, discard);
                } else if (player.getRole() == Role.OUTLAW) {
                    userInterface.printInfo(damager.getName() + " killed an outlaw, draws 3 cards");
                    deckToHand(damager.getHand(), deck, 3, userInterface);
                }
            }
        }
    }

    public void discardAll(Player player, Discard discard) {
        List<Card> discardCards = new ArrayList<>();
        Hand hand = player.getHand();
        while (!hand.isEmpty()) {
            discardCards.add(hand.removeFirst());
        }
        CardsInPlay cardsInPlay = player.getCardsInPlay();
        if (cardsInPlay.hasGun()) {
            discardCards.add(cardsInPlay.removeGun());
        }
        while (!cardsInPlay.isEmpty()) {
            discardCards.add(cardsInPlay.removeFirst());
        }
        hand.addAll(discardCards);
        StringBuilder discardedCards = new StringBuilder();
        while (!hand.isEmpty()) {
            Card discardedCard = hand.removeFirst();
            discardedCards.append(discardedCard.getName()).append(", ");
        }
        if (!discardedCards.isEmpty()) {
            userInterface.printInfo(
                player.getName()
                    + " discarded "
                    + discardedCards.substring(0, discardedCards.length() - 2)
                    + ".");
        }
    }

    public void deadDiscardAll(Player player, List<Player> players, Discard discard, Deck deck) {
        List<Card> discardCards = new ArrayList<>();
        Hand hand = player.getHand();
        while (!hand.isEmpty()) {
            discardCards.add(hand.removeFirst());
        }
        CardsInPlay cardsInPlay = player.getCardsInPlay();
        if (cardsInPlay.hasGun()) {
            discardCards.add(cardsInPlay.removeGun());
        }
        while (!cardsInPlay.isEmpty()) {
            discardCards.add(cardsInPlay.removeFirst());
        }
        List<Player> vultureSams = new ArrayList<>();
        Player vultureSam = null;
        for (Player alivePlayer : players) {
            if (Character.VULTURESAM.equals(alivePlayer.getCharacter())) {
                vultureSam = alivePlayer;
                vultureSams.add(alivePlayer);
            }
        }
        if (vultureSam == null) {
            hand.addAll(discardCards);
            StringBuilder discardedCards = new StringBuilder();
            while (!hand.isEmpty()) {
                Card discardedCard = hand.removeFirst();
                discardedCards.append(discardedCard.getName()).append(", ");
            }
            if (!discardedCards.toString().isEmpty()) {
                userInterface.printInfo(
                    player.getName()
                        + " discarded "
                        + discardedCards.substring(0, discardedCards.length() - 2)
                        + ".");
            }
        } else {
            if (vultureSams.size() == 1) {
                // One vulture sams
                for (Card card : discardCards) {
                    vultureSam.getHand().add(card);
                }
                userInterface.printInfo(
                    vultureSams.getFirst().getCharacter().getName()
                        + " takes "
                        + player.getName()
                        + "'s cards.");
            } else {
                // Two vulture sams
                int playerIndex = 0;
                Player vultureSamPlayer = vultureSams.get(playerIndex);
                while (!discardCards.isEmpty()) {
                    int chosenCard = -1;
                    while (chosenCard < 0 || chosenCard > discardCards.size() - 1) {
                        userInterface.printInfo(
                            vultureSamPlayer.getName() + " choose a card from dead player.");
                        chosenCard = userInterface.chooseGeneralStoreCard(vultureSamPlayer, discardCards);
                    }
                    Card card = discardCards.remove(chosenCard);
                    userInterface.printInfo(vultureSamPlayer.getName() + " chooses a card from dead player.");
                    vultureSamPlayer.getHand().add(card);
                    if (playerIndex == 0) {
                        playerIndex = 1;
                    } else {
                        playerIndex = 0;
                    }
                    vultureSamPlayer = vultureSams.get(playerIndex);
                }
            }
        }
        for (Player alivePlayer : players) {
            if (Character.GREGDIGGER.equals(alivePlayer.getCharacter())) {
                int bonusHealth = 0;
                if (alivePlayer.getHealth() < alivePlayer.getMaxHealth()) {
                    alivePlayer.addHealth(1);
                    bonusHealth += 1;
                }
                if (alivePlayer.getHealth() < alivePlayer.getMaxHealth()) {
                    alivePlayer.addHealth(1);
                    bonusHealth += 1;
                }
                userInterface.printInfo(
                    alivePlayer.getCharacter().getName() + " gets " + bonusHealth + " health.");
            }
        }
        for (Player alivePlayer : players) {
            if (Character.HERBHUNTER.equals(alivePlayer.getCharacter())) {
                Hand herbHand = alivePlayer.getHand();
                herbHand.add(deck.pull());
                herbHand.add(deck.pull());
                userInterface.printInfo(alivePlayer.getCharacter().getName() + " draws 2 cards.");
            }
        }
    }

    public static void discardTwoCardsForLife(
        Player player, Discard discard, UserInterface userInterface) {
        if (Character.SIDKETCHUM.equals(player.getCharacter())) {
            Hand hand = player.getHand();
            if (hand.size() >= 2) {
                List<Card> cardsToDiscard = null;
                while (cardsToDiscard == null || cardsToDiscard.size() % 2 != 0) {
                    cardsToDiscard = userInterface.chooseTwoDiscardForLife(player);
                }
                for (Card card : cardsToDiscard) {
                    hand.remove(card);
                    discard.add(card);
                    userInterface.printInfo(
                        player.getCharacter().getName() + " discards " + card.getName() + " for life.");
                }
                player.addHealth((cardsToDiscard.size() / 2));
            }
        }
    }

    public static boolean playerHasCardsToTake(Player player) {
        boolean emptyHand = player.getHand().isEmpty();
        CardsInPlay cardsInPlay = player.getCardsInPlay();
        boolean hasGun = cardsInPlay.hasGun();
        boolean noCardsInPlay = cardsInPlay.isEmpty();
        return !emptyHand || hasGun || !noCardsInPlay;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static List<Player> othersWithCardsToTake(Player player, List<Player> others) {
        return others.stream()
            .filter(Turn::playerHasCardsToTake)
            .filter(p -> !p.equals(player))
            .collect(Collectors.toList());
    }

    public static List<Card> pullCards(Deck deck, int countCards, UserInterface userInterface) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < countCards; i++) {
            if (deck.isEmpty()) {
                userInterface.printInfo("Shuffling the deck");
            }
            cards.add(deck.pull());
        }
        return cards;
    }

    public static void deckToHand(Hand hand, Deck deck, int countCards, UserInterface userInterface) {
        for (int i = 0; i < countCards; i++) {
            if (deck.isEmpty()) {
                userInterface.printInfo("Shuffling the deck");
            }
            hand.add(deck.pull());
        }
    }

    public static List<Player> getJailablePlayers(Player player, List<Player> players) {
        List<Player> others = new ArrayList<>();
        Player cancelPlayer = new CancelPlayer();
        cancelPlayer.setHand(new Hand());
        cancelPlayer.setInPlay(new CardsInPlay());
        others.add(cancelPlayer);
        for (Player otherPlayer : players) {
            boolean isInJail = otherPlayer.getCardsInPlay().hasItem(CardName.JAIL);
            boolean isSheriff = otherPlayer.getRole() == Role.SHERIFF;
            boolean isPlayer = otherPlayer.equals(player);
            if (!isInJail && !isSheriff && !isPlayer) {
                others.add(otherPlayer);
            }
        }
        return others;
    }

    public static List<Player> getPlayersWithCards(List<Player> players) {
        List<Player> playersWithCards = new ArrayList<>();
        for (Player player : players) {
            if (player instanceof CancelPlayer || playerHasCardsToTake(player)) {
                playersWithCards.add(player);
            }
        }
        return playersWithCards;
    }

    public static List<String> getPlayersNames(List<Player> players) {
        return players.stream().map(Player::getName).collect(Collectors.toList());
    }

    public static Player getValidChosenPlayer(
        Player player, List<Player> choosable, UserInterface userInterface) {
        int chosenPlayer = -1;
        while (chosenPlayer < 0 || chosenPlayer > choosable.size() - 1) {
            chosenPlayer = userInterface.askPlayer(player, getPlayersNames(choosable));
        }
        return choosable.get(chosenPlayer);
    }

    public static boolean isBeerGiveHealth(List<Player> players) {
        return players.size() > 2;
    }

    public static boolean isMaxHealth(Player player) {
        return player.getHealth() == player.getMaxHealth();
    }

    public static Card chooseValidCardToPutBack(
        Player player, List<Card> cards, UserInterface userInterface) {
        int cardIndex = -1;
        while (cardIndex < 0 || cardIndex > cards.size() - 1) {
            cardIndex = userInterface.chooseCardToPutBack(player, cards);
        }
        return cards.get(cardIndex);
    }

    public GameState getGameState() {
        return new GameStateImpl(this);
    }

    public Optional<GameStateCard> getDiscardTopCard() {
        if (!discard.isEmpty()) {
            return GameStateMapper.cardToGameStateCard(discard.getLast());
        } else {
            return Optional.empty();
        }
    }

    public int getDeckSize() {
        return deck.size();
    }

    public boolean isGameOver() {
        return GameOverService.isGameOver(players);
    }

    public List<GameStatePlayer> getGameStatePlayers() {
        List<GameStatePlayer> gameStatePlayers = new ArrayList<>();
        for (Player player : players) {
            gameStatePlayers.add(GameStateMapper.playerToGameStatePlayer(player));
        }
        return gameStatePlayers;
    }

    public List<String> targets(Player player, Card card) {
        List<String> names = new ArrayList<>();
        List<Player> targets = card.targets(player, players);
        for (Player target : targets) {
            names.add(target.getName());
        }
        return names;
    }

    public boolean canPlay(Player player, Card card) {
        return card.canPlay(player, players, bangsPlayed);
    }

    public static List<Card> validRespondTwoMiss(Player player, UserInterface userInterface) {
        while (true) {
            List<Card> cards = Optional.ofNullable(userInterface.respondTwoMiss(player)).orElseGet(Collections::emptyList);
            LOG.debug("Cards: {}", cards);
            if (cards.isEmpty()) {
                return cards;
            }
            if (cards.size() == 2 && cards.stream().noneMatch(card -> isInvalidCard(player, card))) {
                return cards;
            }
        }
    }

    private static boolean isInvalidCard(Player player, Card card) {
        // TODO figure this out xxx its broken
        return !card.getName().equals(CardName.MISSED)
            && !card.getName().equals(CardName.DODGE)
            && (!card.getName().equals(CardName.BANG)
            || !Character.CALAMITYJANET.equals(player.getCharacter()))
            && !Character.ELENAFUENTE.equals(player.getCharacter())
            && !(card instanceof SingleUseMissed);
    }

    @Nullable
    public String getRoleForName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player.getRole().getRoleName();
            }
        }
        return null;
    }

    @Nullable
    public String roleToGoal(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player.getRole().getGoal();
            }
        }
        return null;
    }

    public GameState getGameState(boolean gameOver) {
        return new GameStateImpl(this, gameOver);
    }

    public String getTimeout() {
        return userInterface.getTimeout();
    }

    public Optional<Player> getSheriff() {
        for (Player player : players) {
            if (player.isSheriff()) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }
}
