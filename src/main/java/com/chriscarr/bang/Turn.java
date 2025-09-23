package com.chriscarr.bang;

import com.chriscarr.bang.cards.*;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.gamestate.GameStateImpl;
import com.chriscarr.bang.gamestate.GameStatePlayer;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Turn {

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

    public Player getPlayerForName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
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
        //Log of all cards
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
        //End log all cards

        currentPlayer = getNextPlayer(currentPlayer, players);
        donePlaying = false;
        bangsPlayed = 0;
        turnLoop(currentPlayer);
    }

    private void turnLoop(Player currentPlayer) {
        this.joseActions = 0;
        this.uncleWillActions = 0;
        boolean inJail;
        try {
            userInterface.printInfo(currentPlayer.getName() + "'s turn.");
            if (Character.VERACUSTER.equals(currentPlayer.getCharacter())) {
                List<Player> otherPlayers = new ArrayList<>();
                for (Player other : players) {
                    if (!other.equals(currentPlayer)) {
                        otherPlayers.add(other);
                    }
                }
                userInterface.printInfo(Character.VERACUSTER
                        + " will choose the abilities of another player");
                Player chosenPlayer = getValidChosenPlayer(currentPlayer,
                        otherPlayers, userInterface);
                currentPlayer.setCharacter(chosenPlayer.getCharacter());
                userInterface.printInfo(currentPlayer.getCharacter().getName()
                        + " chose the abilities of " + chosenPlayer.getName());
            }

            if (isDynamiteExplode()) {
                discardDynamite();
                userInterface.printInfo("Dynamite Exploded on "
                        + currentPlayer.getName());
                damagePlayer(currentPlayer, players, currentPlayer, 3, null,
                        deck, discard, userInterface);
                if (isGameOver(players)) {
                    userInterface.printInfo("Winners are " + getWinners(players) + " " + getRoles(players));
                    throw new EndOfGameException("Game over");
                }
            } else {
                passDynamite();
            }
            inJail = isInJail();
            if (!inJail && players.contains(currentPlayer)) {
                this.drawCards(currentPlayer, deck);
                while (!donePlaying && players.contains(currentPlayer)) {
                    play();
                    if (isGameOver(players)) {
                        userInterface.printInfo("Winners are " + getWinners(players) + " " + getRoles(players));
                        throw new EndOfGameException("Game over");
                    }
                }
            }
        } catch (EndOfGameException e) {
            return;
        }
        if (players.contains(currentPlayer)) {
            if (!inJail) {
                discard(currentPlayer);
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

    public void drawCards(Player player, Deck deck) {
        Hand hand = player.getHand();
        if (Character.KITCARLSON.equals(player.getCharacter())) {
            List<Card> cards = pullCards(deck, 3, userInterface);
            Card cardToPutBack = chooseValidCardToPutBack(player, cards, userInterface);
            cards.remove(cardToPutBack);
            deck.add(cardToPutBack);
            hand.addAll(cards);
            userInterface.printInfo(player.getCharacter().getName()
                    + " put a card back on the draw pile");
        } else if (Character.JESSEJONES.equals(player.getCharacter())) {
            List<Player> otherPlayers = new ArrayList<>();
            for (Player other : players) {
                if (!other.equals(player) && !other.getHand().isEmpty()) {
                    otherPlayers.add(other);
                }
            }
            boolean chosenFromPlayer = false;
            if (!otherPlayers.isEmpty()) {
                chosenFromPlayer = userInterface.chooseFromPlayer(player);
            }
            if (chosenFromPlayer) {
                Player chosenPlayer = getValidChosenPlayer(player,
                        otherPlayers, userInterface);
                Card randomCard = chosenPlayer.removeRandom();
                hand.add(randomCard);
                userInterface.printInfo(player.getCharacter().getName()
                        + " drew a card from " + chosenPlayer.getName()
                        + " hand.");
            } else {
                if (deck.isEmpty()) {
                    userInterface.printInfo("Shuffling the deck");
                }
                hand.add(deck.pull());
                userInterface.printInfo(player.getCharacter().getName()
                        + " drew a card from the deck.");
            }
            hand.add(deck.pull());
        } else if (Character.PATBRENNAN.equals(player.getCharacter())) {
            boolean chosenFromPlayer = userInterface.chooseFromPlayer(player);
            if (chosenFromPlayer) {
                List<Player> otherPlayers = new ArrayList<>();
                for (Player other : players) {
                    if (!other.equals(player) && (!other.getCardsInPlay().isEmpty() || other.getCardsInPlay().hasGun())) {
                        otherPlayers.add(other);
                    }
                }
                if (!otherPlayers.isEmpty()) {
                    Player chosenPlayer = getValidChosenPlayer(player,
                            otherPlayers, userInterface);
                    int chosenCard = -3;
                    while (chosenCard < -2 || chosenCard > chosenPlayer.getCardsInPlay().size() - 1) {
                        chosenCard = userInterface.askOthersCard(player, chosenPlayer.getCardsInPlay(), false);
                    }
                    if (chosenCard == -2) {
                        Card card = chosenPlayer.getCardsInPlay().removeGun();
                        hand.add(card);
                        userInterface.printInfo(currentPlayer.getName() + " takes a " + card.getName() + " from " + chosenPlayer.getName());
                    } else {
                        Card card = chosenPlayer.getCardsInPlay().remove(chosenCard);
                        hand.add(card);
                        userInterface.printInfo(currentPlayer.getName() + " takes a " + card.getName() + " from " + chosenPlayer.getName());
                    }
                } else {
                    hand.add(deck.pull());
                    hand.add(deck.pull());
                }
            } else {
                hand.add(deck.pull());
                hand.add(deck.pull());
            }
        } else if (Character.PEDRORAMIREZ.equals(player.getCharacter())) {
            if (!discard.isEmpty()) {
                boolean chosenDiscard = userInterface.chooseDiscard(player, discard.getLast());
                if (chosenDiscard) {
                    Card discardCard = discard.removeLast();
                    hand.add(discardCard);
                    userInterface.printInfo(player.getCharacter().getName() + " drew a "
                            + discardCard.getName()
                            + " from the discard pile.");
                } else {
                    hand.add(deck.pull());
                    userInterface.printInfo(player.getCharacter().getName()
                            + " drew a card from the deck.");
                }
            } else {
                hand.add(deck.pull());
                userInterface.printInfo(player.getCharacter().getName()
                        + " drew a card from the deck.");
            }
            hand.add(deck.pull());
        } else if (Character.PIXIEPETE.equals(player.getCharacter())) {
            hand.add(deck.pull());
            hand.add(deck.pull());
            hand.add(deck.pull());
        } else if (Character.BILLNOFACE.equals(player.getCharacter())) {
            hand.add(deck.pull());
            int cardsToDraw = player.getMaxHealth() - player.getHealth();
            while (cardsToDraw > 0) {
                hand.add(deck.pull());
                cardsToDraw -= 1;
            }
            userInterface.printInfo(player.getName()
                    + " drew " + (player.getMaxHealth() - player.getHealth() + 1) + " card(s) from the deck.");
        } else if (Character.CLAUSTHESAINT.equals(player.getCharacter())) {
            List<Card> cards = pullCards(deck, players.size() + 1, userInterface);
            Player generalPlayer = Turn.getNextPlayer(player, players);
            while (!generalPlayer.equals(player)) {
                Card card = chooseValidCardToPutBack(player, cards,
                        userInterface);
                cards.remove(card);
                userInterface.printInfo(player.getName() + " gives " + generalPlayer.getName() + " a card.");
                generalPlayer.getHand().add(card);
                generalPlayer = Turn.getNextPlayer(generalPlayer, players);
            }
            hand.addAll(cards);
        } else {
            hand.add(deck.pull());
            Card secondCard = deck.pull();
            hand.add(secondCard);
            if (Character.BLACKJACK.equals(player.getCharacter())) {
                CardSuit suit = secondCard.getSuit();
                userInterface.printInfo(player.getCharacter().getName() + " drew a "
                        + suit.getLabel() + " "
                        + secondCard.getName());
                if (suit == CardSuit.HEARTS || suit == CardSuit.DIAMONDS) {
                    hand.add(deck.pull());
                    userInterface.printInfo(player.getCharacter().getName()
                            + " drew a third card from the deck.");
                }
            }
        }
    }

    public void discard(Player player) {
        int maxHandSize = player.getHealth();
        if (Character.SEANMALLORY.equals(player.getCharacter())) {
            maxHandSize = 10;
        }
        Hand hand = player.getHand();
        StringBuilder discardedCards = new StringBuilder();
        while (hand.size() > maxHandSize) {
            Card discardedCard = askPlayerToDiscard(player, discard);
            discardedCards.append(discardedCard.getName()).append(", ");
        }
        if (!discardedCards.toString().isEmpty()) {
            userInterface.printInfo(player.getName() + " discarded " + discardedCards.substring(0, discardedCards.length() - 2) + ".");
        }
    }

    private Card askPlayerToDiscard(Player player, Discard discard) {
        int card = -1;
        while (card < 0 || card > player.getHand().size() - 1) {
            card = userInterface.askDiscard(player);
        }
        Card removedCard = player.getHand().remove(card);
        discard.add(removedCard);
        return removedCard;
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void play() {
        for (Player player : players) {
            if (Character.SUZYLAFAYETTE.equals(player.getCharacter())) {
                Hand playerHand = player.getHand();
                if (playerHand.isEmpty()) {
                    if (deck.isEmpty()) {
                        userInterface.printInfo("Shuffling the deck");
                    }
                    playerHand.add(deck.pull());
                    userInterface.printInfo(player.getName()
                            + " ran out of cards and drew a card.");
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
            if (card > (hand.size() + singleUseInPlay.size() - 1) && Character.CHUCKWENGAM.equals(currentPlayer.getCharacter())) {
                if (currentPlayer.getHealth() > 1) {
                    currentPlayer.setHealth(currentPlayer.getHealth() - 1);
                    Hand playerHand = currentPlayer.getHand();
                    playerHand.add(deck.pull());
                    playerHand.add(deck.pull());
                    userInterface.printInfo(currentPlayer.getName()
                            + " traded one life for 2 cards.");
                    return;
                }
            } else if (card > (hand.size() + singleUseInPlay.size() - 1) && Character.SIDKETCHUM.equals(currentPlayer.getCharacter())) {
                discardTwoCardsForLife(currentPlayer, discard, userInterface);
            } else if (card > (hand.size() - 1) && Character.JOSEDELGADO.equals(currentPlayer.getCharacter())) {
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
                    userInterface.printInfo(currentPlayer.getName()
                            + " traded one blue card for 2 cards.");
                    return;
                }
            } else if (card > (hand.size() - 1) && Character.UNCLEWILL.equals(currentPlayer.getCharacter())) {
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
                    userInterface.printInfo(generalPlayer.getName() + " chooses " + storeCard.getName() + " from " + CardName.GENERAL_STORE);
                    generalPlayer.getHand().add(storeCard);
                    generalPlayer = Turn.getNextPlayer(generalPlayer, players);
                }
                return;

            } else if (card > (hand.size() + singleUseInPlay.size() - 1) && Character.DOCHOLYDAY.equals(currentPlayer.getCharacter())) {
                List<Card> cardsToDiscard = userInterface.chooseTwoDiscardForShoot(currentPlayer);
                if (cardsToDiscard.size() == 2) {
                    CardSuit discardSuit = CardSuit.DIAMONDS;
                    for (Card discardcard : cardsToDiscard) {
                        if (discardcard.getSuit() != CardSuit.DIAMONDS) {
                            discardSuit = discardcard.getSuit();
                        }
                        hand.remove(discardcard);
                        discard.add(discardcard);
                        userInterface.printInfo(currentPlayer.getName()
                                + " discards " + discardcard.getName()
                                + " for shoot.");
                    }
                    Bang tempBang = new Bang(CardName.BANG, discardSuit, CardValue.SEVEN, CardType.PLAY);
                    boolean success = tempBang.play(currentPlayer, players, userInterface, deck, discard, this, true);
                    if (!success) {
                        hand.add(discard.removeLast());
                        hand.add(discard.removeLast());
                    }
                    return;
                }

            }
        }
        if (hand.size() + singleUseInPlay.size() == 0 || card == -1) {
            donePlaying = true;
            userInterface.printInfo(currentPlayer.getName()
                    + " is finished playing.");
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
            singleUseInPlay.get(chosen).play(currentPlayer, players, userInterface, deck,
                    discard, this);
        } else {
            Card playedCard = hand.get(card);
            if (playedCard.canPlay(currentPlayer, players, bangsPlayed)) {
                hand.remove(card);
                if (Objects.equals(playedCard.getName(), CardName.GENERAL_STORE)
                        || Objects.equals(playedCard.getName(), CardName.GATLING)
                        || Objects.equals(playedCard.getName(), CardName.INDIANS)) {
                    userInterface.printInfo(currentPlayer.getName() + " played a "
                            + playedCard.getName() + ".");
                }
                boolean success = playedCard.play(currentPlayer, players, userInterface, deck,
                        discard, this);
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
                    userInterface.printInfo(currentPlayer.getName() + " played a "
                            + playedCard.getName() + ".");
                }
            }
        }
    }

    public static int validPlayMiss(Player player, UserInterface userInterface, boolean canSingleUse) {
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
            if (playedBeer == -1
                    || CardName.BEER.equals(player.getHand()
                    .get(playedBeer).getName())) {
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

    public void setDiscard(Discard discard) {
        this.discard = discard;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public static List<Player> getPlayersWithinRange(Player player, List<Player> players) {
        List<Player> others = new ArrayList<>();
        Player cancelPlayer = new CancelPlayer();
        cancelPlayer.setHand(new Hand());
        cancelPlayer.setInPlay(new CardsInPlay());
        others.add(cancelPlayer);
        int range = player.getGunRange();
        for (Player otherPlayer : players) {
            int distance = AlivePlayers.getDistance(players.indexOf(player), players.indexOf(otherPlayer), players.size());
            if (otherPlayer.getCardsInPlay().hasItem(CardName.MUSTANG)) {
                if (!Character.BELLESTAR.equals(player.getCharacter())) {
                    distance = distance + 1;
                }
            }
            if (otherPlayer.getCardsInPlay().hasItem(CardName.HIDEOUT)) {
                if (!Character.BELLESTAR.equals(player.getCharacter())) {
                    distance = distance + 1;
                }
            }
            if (Character.PAULREGRET.equals(otherPlayer.getCharacter())) {
                distance = distance + 1;
            }
            if (player.getCardsInPlay().hasItem(CardName.SCOPE)) {
                distance = distance - 1;
            }
            if (player.getCardsInPlay().hasItem(CardName.SILVER)) {
                distance = distance - 1;
            }
            if (Character.ROSEDOOLAN.equals(player.getCharacter())) {
                distance = distance - 1;
            }
            if (distance <= range) {
                others.add(otherPlayer);
            }
        }
        return others(player, others);
    }

    public boolean isDynamiteExplode() {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.DYNAMITE)) {
            userInterface.printInfo(currentPlayer.getName() + " is drawing to see if the dynamite explodes");
            Card drawnCard = draw(currentPlayer, deck, discard, userInterface);
            return Card.isExplode(drawnCard);
        }
        return false;
    }

    public void passDynamite() {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.DYNAMITE)) {
            Card dynamiteCard = currentCardsInPlay.removeDynamite();
            Player nextPlayer = getNextPlayer(currentPlayer, players);
            CardsInPlay nextCardsInPlay = nextPlayer.getCardsInPlay();
            if (!nextCardsInPlay.hasItem(CardName.DYNAMITE)) {
                userInterface.printInfo("Dynamite Passed to " + nextPlayer.getName());
                nextCardsInPlay.add(dynamiteCard);
            } else {
                nextPlayer = getNextPlayer(nextPlayer, players);
                nextCardsInPlay = nextPlayer.getCardsInPlay();
                userInterface.printInfo("Dynamite Passed to " + nextPlayer.getName());
                nextCardsInPlay.add(dynamiteCard);
            }
        }
    }

    public void discardDynamite() {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.DYNAMITE)) {
            Card dynamiteCard = currentCardsInPlay.removeDynamite();
            discard.add(dynamiteCard);
        }
    }

    public static Card draw(Player player, Deck deck, Discard discard,
                            UserInterface userInterface) {
        if (Character.LUCKYDUKE.equals(player.getCharacter())) {
            List<Card> cards = pullCards(deck, 2, userInterface);
            int chosenCard = -1;
            while (chosenCard < 0 || chosenCard > (cards.size() - 1)) {
                chosenCard = userInterface.chooseDrawCard(player, cards);
            }
            discard.addAll(cards);
            Card drawnCard = cards.get(chosenCard);
            userInterface.printInfo(player.getName() + " drew a "
                    + drawnCard.getValue().getLabel() + " of "
                    + drawnCard.getSuit().getLabel() + " "
                    + drawnCard.getName());
            return cards.get(chosenCard);
        } else {
            if (deck.isEmpty()) {
                userInterface.printInfo("Shuffling the deck");
            }
            Card card = deck.pull();
            userInterface.printInfo(player.getName() + " drew a "
                    + card.getValue().getLabel() + " of "
                    + card.getSuit().getLabel() + " "
                    + card.getName());
            discard.add(card);
            return card;
        }
    }

    public boolean isInJail() {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.JAIL)) {
            Card jailCard = currentCardsInPlay.removeJail();
            userInterface.printInfo(currentPlayer.getName()
                    + " is drawing to break out of jail");
            Card drawn = draw(currentPlayer, deck, discard,
                    userInterface);
            boolean inJail = drawn.getSuit() != CardSuit.HEARTS;
            discard.add(jailCard);
            if (inJail) {
                userInterface.printInfo(currentPlayer.getName()
                        + " stays in jail");
            } else {
                userInterface.printInfo(currentPlayer.getName()
                        + " breaks out of jail");
            }
            return inJail;
        }
        return false;
    }

    public static int isBarrelSave(Player player, Deck deck, Discard discard,
                                   UserInterface userInterface, int missesRequired, Player shooter) {
        int misses = 0;
        if (Character.JOURDONNAIS.equals(player.getCharacter())) {
            userInterface.printInfo(player.getName()
                    + " is drawing to be saved by a barrel");
            Card drawn = draw(player, deck, discard, userInterface);
            if (drawn.getSuit() == CardSuit.HEARTS) {
                misses = misses + 1;
                userInterface.printInfo(player.getCharacter().getName() + " drew a "
                        + CardSuit.HEARTS.getLabel() + " and was saved by his ability.");
            } else {
                userInterface.printInfo(player.getCharacter().getName() + " drew a "
                        + drawn.getSuit().getLabel() + " and was not saved by his ability.");
            }
        }
        if (misses >= missesRequired) {
            return misses;
        }
        CardsInPlay currentCardsInPlay = player.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.BARREL)) {
            if (Character.BELLESTAR.equals(shooter.getCharacter())) {
                userInterface.printInfo(player.getName()
                        + "'s barrel has no affect on " + shooter.getCharacter().getName());
            } else {
                userInterface.printInfo(player.getName()
                        + " is drawing to be saved by a barrel");
                Card drawn = draw(player, deck, discard, userInterface);
                if (drawn.getSuit() == CardSuit.HEARTS) {
                    misses = misses + 1;
                    userInterface.printInfo(player.getName() + " drew a "
                            + CardSuit.HEARTS.getLabel() + " and was saved by their barrel.");
                } else {
                    userInterface.printInfo(player.getName() + " drew a "
                            + drawn.getSuit().getLabel()
                            + " and was not saved by their barrel.");
                }
            }
        }
        return misses;
    }

    public void damagePlayer(Player player, List<Player> players,
                             Player currentPlayer, int damage, Player damager, Deck deck,
                             Discard discard, UserInterface userInterface) {
        player.setHealth(player.getHealth() - damage);
        if (player.getHealth() <= 0 && players.size() > 2) {
            boolean doNotPlayBeer = false;
            while (!doNotPlayBeer && player.getHealth() <= 0) {
                int playedBeer = validPlayBeer(player, userInterface);
                if (playedBeer != -1) {
                    if (Character.TEQUILAJOE.equals(player.getCharacter())) {
                        player.setHealth(player.getHealth() + 2);
                        discard.add(player.getHand().remove(playedBeer));
                        userInterface.printInfo(player.getName() + " plays a beer and gains two lives.");
                    } else {
                        player.setHealth(player.getHealth() + 1);
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
            handleDeath(player, damager, currentPlayer, players, userInterface,
                    deck, discard);
        } else {
            if (Character.BARTCASSIDY.equals(player.getCharacter())) {
                for (int i = 0; i < damage; i++) {
                    if (deck.isEmpty()) {
                        userInterface.printInfo("Shuffling the deck");
                    }
                    player.getHand().add(deck.pull());
                    userInterface
                            .printInfo(player.getCharacter().getName()
                                    + " draws a card from the deck because he was damaged.");
                }
            } else if (damager != null
                    && Character.ELGRINGO.equals(player.getCharacter())) {
                Hand otherHand = damager.getHand();
                if (!otherHand.isEmpty()) {
                    Hand playerHand = player.getHand();
                    playerHand.add(otherHand.removeRandom());
                    userInterface.printInfo(player.getCharacter().getName()
                            + " draws a card from " + damager.getName()
                            + " because he was damaged.");
                }
            }
        }
    }

    public void handleDeath(Player player, Player damager,
                            Player currentPlayer, List<Player> players,
                            UserInterface userInterface, Deck deck, Discard discard) {
        //I think we can remove this
        //if (player.equals(currentPlayer)) {
        //TODO is this the right way to handle all death... set it to last player?
        //currentPlayer = getPreviousPlayer(currentPlayer, players);
        //}
        players.remove(player);
        userInterface.printInfo(player.getName() + " is dead. Role was "
                + player.getRole().getRoleName());
        if (!isGameOver(players)) {
            deadDiscardAll(player, players, discard, deck);
            if (damager != null) {
                if (damager.getRole() == Role.SHERIFF
                        && player.getRole() == Role.DEPUTY) {
                    userInterface.printInfo(damager.getName()
                            + " killed own deputy, loses all cards");
                    discardAll(damager, discard);
                } else if (player.getRole() == Role.OUTLAW) {
                    userInterface.printInfo(damager.getName()
                            + " killed an outlaw, draws 3 cards");
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
            userInterface.printInfo(player.getName() + " discarded " + discardedCards.substring(0, discardedCards.length() - 2) + ".");
        }
    }

    public void deadDiscardAll(Player player, List<Player> players,
                               Discard discard, Deck deck) {
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
                userInterface.printInfo(player.getName() + " discarded " + discardedCards.substring(0, discardedCards.length() - 2) + ".");
            }
        } else {
            if (vultureSams.size() == 1) {
                //One vulture sams
                for (Card card : discardCards) {
                    vultureSam.getHand().add(card);
                }
                userInterface.printInfo(vultureSams.getFirst().getCharacter().getName() + " takes "
                        + player.getName() + "'s cards.");
            } else {
                //Two vulture sams
                int playerIndex = 0;
                Player vultureSamPlayer = vultureSams.get(playerIndex);
                while (!discardCards.isEmpty()) {
                    int chosenCard = -1;
                    while (chosenCard < 0 || chosenCard > discardCards.size() - 1) {
                        userInterface.printInfo(vultureSamPlayer.getName() + " choose a card from dead player.");
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
                    alivePlayer.setHealth(alivePlayer.getHealth() + 1);
                    bonusHealth += 1;
                }
                if (alivePlayer.getHealth() < alivePlayer.getMaxHealth()) {
                    alivePlayer.setHealth(alivePlayer.getHealth() + 1);
                    bonusHealth += 1;
                }
                userInterface.printInfo(alivePlayer.getCharacter().getName() + " gets " + bonusHealth + " health.");
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

    public static boolean isGameOver(List<Player> players) {
        return isDead(Role.SHERIFF, players)
                || (isDead(Role.RENEGADE, players)
                && isDead(Role.OUTLAW, players));
    }

    private static boolean isDead(Role role, List<Player> players) {
        for (Player player : players) {
            if (player.getRole() == role) {
                return false;
            }
        }
        return true;
    }

    public static String getWinners(List<Player> players) {
        if (isDead(Role.DEPUTY, players)
                && isDead(Role.OUTLAW, players)
                && isDead(Role.SHERIFF, players) && players.size() == 1) {
            return "Renegade";
        } else if (isDead(Role.SHERIFF, players)
                && (!isDead(Role.DEPUTY, players) || !isDead(Role.OUTLAW, players) || !isDead(Role.RENEGADE, players))) {
            return "Outlaws";
        } else if (isDead(Role.OUTLAW, players) && isDead(Role.RENEGADE, players)) {
            return "Sheriff and Deputies";
        } else {
            throw new RuntimeException("No Winner");
        }
    }

    public static String getRoles(List<Player> players) {
        StringBuilder result = new StringBuilder();
        for (Player player : players) {
            result.append(player.getCharacter().getName()).append(" was a ").append(player.getRole().getRoleName()).append(". ");
        }
        return result.toString();
    }

    public static void discardTwoCardsForLife(Player player, Discard discard,
                                              UserInterface userInterface) {
        if (Character.SIDKETCHUM.equals(player.getCharacter())) {
            Hand hand = player.getHand();
            if (hand.size() >= 2) {
                List<Card> cardsToDiscard = null;
                while (cardsToDiscard == null || cardsToDiscard.size() % 2 != 0) {
                    cardsToDiscard = userInterface
                            .chooseTwoDiscardForLife(player);
                }
                for (Card card : cardsToDiscard) {
                    hand.remove(card);
                    discard.add(card);
                    userInterface.printInfo(player.getCharacter().getName()
                            + " discards " + card.getName()
                            + " for life.");
                }
                player.setHealth(player.getHealth()
                        + (cardsToDiscard.size() / 2));
                if (player.getHealth() > player.getMaxHealth()) {
                    player.setHealth(player.getMaxHealth());
                }
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
        return others.stream().filter(Turn::playerHasCardsToTake).filter(p -> !p.equals(player)).collect(Collectors.toList());
    }

    public static List<Player> others(Player player, List<Player> others) {
        List<Player> othersCopy = new ArrayList<>(others);
        othersCopy.remove(player);
        return othersCopy;
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

    public static List<Player> getJailablePlayers(Player player,
                                                  List<Player> players) {
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

    public static Player getValidChosenPlayer(Player player,
                                              List<Player> choosable, UserInterface userInterface) {
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

    public static Card chooseValidCardToPutBack(Player player,
                                                List<Card> cards, UserInterface userInterface) {
        int cardIndex = -1;
        while (cardIndex < 0 || cardIndex > cards.size() - 1) {
            cardIndex = userInterface.chooseCardToPutBack(player, cards);
        }
        return cards.get(cardIndex);
    }

    public GameState getGameState() {
        return new GameStateImpl(this);
    }

    public GameStateCard getDiscardTopCard() {
        if (!discard.isEmpty()) {
            return cardToGameStateCard(discard.getLast());
        } else {
            return null;
        }
    }

    public static GameStateCard cardToGameStateCard(Card fromCard) {
        if (fromCard == null) {
            return null;
        }
        GameStateCard card = new GameStateCard();
        card.name = fromCard.getName();
        card.suit = fromCard.getSuit().getLabel();
        card.value = fromCard.getValue().getLabel();
        card.type = fromCard.getType().getTypeName();
        return card;
    }

    public int getDeckSize() {
        return deck.size();
    }

    public boolean isGameOver() {
        return isGameOver(players);
    }

    public List<GameStatePlayer> getGameStatePlayers() {
        List<GameStatePlayer> gameStatePlayers = new ArrayList<>();
        for (Player player : players) {
            GameStatePlayer gameStatePlayer = new GameStatePlayer();
            gameStatePlayer.name = player.getName();
            gameStatePlayer.health = player.getHealth();
            gameStatePlayer.maxHealth = player.getMaxHealth();
            gameStatePlayer.handSize = player.getHandSize();
            gameStatePlayer.gun = player.getGameStateGun();
            gameStatePlayer.isSheriff = player.isSheriff();
            gameStatePlayer.specialAbility = player.getSpecialAbility();
            gameStatePlayer.inPlay = player.getGameStateInPlay();
            gameStatePlayers.add(gameStatePlayer);
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

    public static List<Card> validRespondTwoMiss(Player player,
                                                 UserInterface userInterface) {
        List<Card> cards = null;
        boolean validCards = false;
        while (!validCards) {
            cards = userInterface.respondTwoMiss(player);
            System.out.println(cards.size());
            if (cards.isEmpty()) {
                validCards = true;
            } else if (cards.size() == 2) {
                validCards = true;
                for (Card card : cards) {
                    boolean invalidCard = isInvalidCard(player, card);
                    if (invalidCard) {
                        validCards = false;
                        break;
                    }
                }
            }
        }
        return cards;
    }

    private static boolean isInvalidCard(Player player, Card card) {
        //TODO figure this out xxx its broken
        return !card.getName().equals(CardName.MISSED) &&
                !card.getName().equals(CardName.DODGE) &&
                (!card.getName().equals(CardName.BANG) || !Character.CALAMITYJANET.equals(player.getCharacter())) &&
                !Character.ELENAFUENTE.equals(player.getCharacter()) &&
                !(card instanceof SingleUseMissed);
    }

    public String getRoleForName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player.getRole().getRoleName();
            }
        }
        return null;
    }

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

    public Player getSheriff() {
        for (Player player : players) {
            if (player.isSheriff()) {
                return player;
            }
        }
        return null;
    }
}
