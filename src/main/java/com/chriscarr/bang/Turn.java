package com.chriscarr.bang;

import com.chriscarr.bang.cards.Bang;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.SingleUse;
import com.chriscarr.bang.cards.SingleUseMissed;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.gamestate.GameStateImpl;
import com.chriscarr.bang.gamestate.GameStatePlayer;
import com.chriscarr.bang.models.game.Character;
import com.chriscarr.bang.models.game.Role;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Turn {

    private List<Player> players;
    private Player currentPlayer;
    private UserInterface userInterface;
    private boolean donePlaying = false;
    private DiscardPile discardPile;
    private Deck deck;
    private int bangsPlayed = 0;
    private int joseActions = 0;
    private int uncleWillActions = 0;

    public ArrayList<String> getRoles() {
        ArrayList<String> roles = new ArrayList<>();
        for (Player player : players) {
            roles.add(player.getRole().roleName());
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

    public static Player getPreviousPlayer(Player player, List<Player> players) { //refactor
        int index = players.indexOf(player);
        if (index == 0) {
            index = players.size() - 1;
        } else {
            index = index - 1;
        }
        return players.get(index);
    }

    public void nextTurn() {
        //refactor
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
            if (Character.VERA_CUSTER.equals(currentPlayer.getCharacter())) {
                List<Player> otherPlayers = new ArrayList<>();
                for (Player other : players) {
                    if (!other.equals(currentPlayer)) {
                        otherPlayers.add(other);
                    }
                }
                userInterface.printInfo(Character.VERA_CUSTER.characterName()
                        + " will choose the abilities of another player");
                Player chosenPlayer = getValidChosenPlayer(currentPlayer,
                        otherPlayers, userInterface);
                currentPlayer.setCharacter(chosenPlayer.getCharacter());
                userInterface.printInfo(currentPlayer.getCharacter().characterName()
                        + " chose the abilities of " + chosenPlayer.getName());
            }

            if (isDynamiteExplode()) {
                discardDynamite();
                userInterface.printInfo("Dynamite Exploded on "
                        + currentPlayer.getName());
                damagePlayer(currentPlayer, players, currentPlayer, 3, null,
                        deck, discardPile, userInterface);
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
            if (player.isSheriff()) {
                currentPlayer = player;
                turnLoop(currentPlayer);
            }
        }
    }

    public void setSheriffManualTest() {
        for (Player player : players) {
            if (player.isSheriff()) {
                currentPlayer = player;
            }
        }
    }

    public void drawCards(Player player, Deck deck) {
        Hand hand = player.getHand();
        if (Character.KIT_CARLSON.equals(player.getCharacter())) {
            List<Card> cards = pullCards(deck, 3, userInterface);
            Card cardToPutBack = chooseValidCardToPutBack(player, cards,
                    userInterface);
            cards.remove(cardToPutBack);
            deck.add(cardToPutBack);
            for (Card card : cards) {
                hand.add(card);
            }
            userInterface.printInfo(player.getCharacter().characterName()
                    + " put a card back on the draw pile");
        } else if (Character.JESSE_JONES.equals(player.getCharacter())) {
            List<Player> otherPlayers = new ArrayList<>();
            for (Player other : players) {
                if (!other.equals(player) && other.getHand().size() > 0) {
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
                userInterface.printInfo(player.getCharacter().characterName()
                        + " drew a card from " + chosenPlayer.getName()
                        + " hand.");
            } else {
                if (deck.size() == 0) {
                    userInterface.printInfo("Shuffling the deck");
                }
                hand.add(deck.pull());
                userInterface.printInfo(player.getCharacter().characterName()
                        + " drew a card from the deck.");
            }
            hand.add(deck.pull());
        } else if (Character.PAT_BRENNAN.equals(player.getCharacter())) {
            boolean chosenFromPlayer = userInterface.chooseFromPlayer(player);
            if (chosenFromPlayer) {
                List<Player> otherPlayers = new ArrayList<>();
                for (Player other : players) {
                    if (!other.equals(player) && (other.getInPlay().count() > 0 || other.getInPlay().hasGun())) {
                        otherPlayers.add(other);
                    }
                }
                if (otherPlayers.size() != 0) {
                    Player chosenPlayer = getValidChosenPlayer(player,
                            otherPlayers, userInterface);
                    int chosenCard = -3;
                    while (chosenCard < -2 || chosenCard > chosenPlayer.getInPlay().size() - 1) {
                        chosenCard = userInterface.askOthersCard(player, chosenPlayer.getInPlay(), false);
                    }
                    if (chosenCard == -2) {
                        Card card = chosenPlayer.getInPlay().removeGun();
                        hand.add(card);
                        userInterface.printInfo(currentPlayer.getName() + " takes a " + card.getName() + " from " + chosenPlayer.getName());
                    } else {
                        Card card = chosenPlayer.getInPlay().remove(chosenCard);
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
        } else if (Character.PEDRO_RAMIREZ.equals(player.getCharacter())) {
            if (discardPile.canDrawFromDiscard()) {
                boolean chosenDiscard = userInterface.chooseDiscard(player, discardPile.peek());
                if (chosenDiscard) {
                    Card discardCard = discardPile.remove();
                    hand.add(discardCard);
                    userInterface.printInfo(player.getCharacter().characterName() + " drew a "
                            + discardCard.getName()
                            + " from the discard pile.");
                } else {
                    hand.add(deck.pull());
                    userInterface.printInfo(player.getCharacter().characterName()
                            + " drew a card from the deck.");
                }
            } else {
                hand.add(deck.pull());
                userInterface.printInfo(player.getCharacter().characterName()
                        + " drew a card from the deck.");
            }
            hand.add(deck.pull());
        } else if (Character.PIXIE_PETE.equals(player.getCharacter())) {
            hand.add(deck.pull());
            hand.add(deck.pull());
            hand.add(deck.pull());
        } else if (Character.BILL_NOFACE.equals(player.getCharacter())) {
            hand.add(deck.pull());
            int cardsToDraw = player.getMaxHealth() - player.getHealth();
            while (cardsToDraw > 0) {
                hand.add(deck.pull());
                cardsToDraw -= 1;
            }
            userInterface.printInfo(player.getName()
                    + " drew " + (player.getMaxHealth() - player.getHealth() + 1) + " card(s) from the deck.");
        } else if (Character.CLAUS_THE_SAINT.equals(player.getCharacter())) {
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
            for (Card card : cards) {
                hand.add(card);
            }
        } else {
            hand.add(deck.pull());
            Card secondCard = deck.pull();
            hand.add(secondCard);
            if (Character.BLACK_JACK.equals(player.getCharacter())) {
                int suit = secondCard.getSuit();
                userInterface.printInfo(player.getCharacter().characterName() + " drew a "
                        + Card.suitToString(suit) + " "
                        + secondCard.getName());
                if (suit == Card.HEARTS || suit == Card.DIAMONDS) {
                    hand.add(deck.pull());
                    userInterface.printInfo(player.getCharacter().characterName()
                            + " drew a third card from the deck.");
                }
            }
        }
    }

    public void discard(Player player) {
        int maxHandSize = player.getHealth();
        if (Character.SEAN_MALLORY.equals(player.getCharacter())) {
            maxHandSize = 10;
        }
        Hand hand = player.getHand();
        StringBuilder discardedCards = new StringBuilder();
        while (hand.size() > maxHandSize) {
            Card discardedCard = askPlayerToDiscard(player, discardPile);
            discardedCards.append(discardedCard.getName()).append(", ");
        }
        if (!discardedCards.toString().equals("")) {
            userInterface.printInfo(player.getName() + " discarded " + discardedCards.substring(0, discardedCards.length() - 2) + ".");
        }
    }

    private Card askPlayerToDiscard(Player player, DiscardPile discardPile) {
        int card = -1;
        while (card < 0 || card > player.getHand().size() - 1) {
            card = userInterface.askDiscard(player);
        }
        Card removedCard = player.getHand().remove(card);
        discardPile.add(removedCard);
        return removedCard;
    }

    public void setUserInterface(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void play() {
        for (Player player : players) {
            if (Character.SUZY_LAFAYETTE.equals(player.getCharacter())) {
                Hand playerHand = player.getHand();
                if (playerHand.isEmpty()) {
                    if (deck.size() == 0) {
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
        InPlay allInPlay = currentPlayer.getInPlay();
        ArrayList<SingleUse> singleUseInPlay = new ArrayList<>();
        for (int i = 0; i < allInPlay.size(); i++) {
            Card inPlayCard = allInPlay.get(i);
            if (inPlayCard instanceof SingleUse) {
                singleUseInPlay.add((SingleUse) inPlayCard);
            }
        }
        while (card < -1 || card > hand.size() + singleUseInPlay.size() - 1) {
            card = userInterface.askPlay(currentPlayer);
            if (card > (hand.size() + singleUseInPlay.size() - 1) && Character.CHUCK_WENGAM.equals(currentPlayer.getCharacter())) {
                if (currentPlayer.getHealth() > 1) {
                    currentPlayer.setHealth(currentPlayer.getHealth() - 1);
                    Hand playerHand = currentPlayer.getHand();
                    playerHand.add(deck.pull());
                    playerHand.add(deck.pull());
                    userInterface.printInfo(currentPlayer.getName()
                            + " traded one life for 2 cards.");
                    return;
                }
            } else if (card > (hand.size() + singleUseInPlay.size() - 1) && Character.SID_KETCHUM.equals(currentPlayer.getCharacter())) {
                discardTwoCardsForLife(currentPlayer, discardPile, userInterface);
            } else if (card > (hand.size() - 1) && Character.JOSE_DELGADO.equals(currentPlayer.getCharacter())) {
                if (this.joseActions >= 2) {
                    userInterface.printInfo("Already used special abilitity twice this turn.");
                    return;
                }
                int cardIndex = userInterface.askBlueDiscard(currentPlayer);
                if (cardIndex == -1) {
                    return;
                }
                Card playedCard = hand.get(cardIndex);
                if (playedCard.getType() == Card.TYPEGUN || playedCard.getType() == Card.TYPEITEM) {
                    this.joseActions += 1;
                    hand.remove(cardIndex);
                    discardPile.add(playedCard);
                    hand.add(deck.pull());
                    hand.add(deck.pull());
                    userInterface.printInfo(currentPlayer.getName()
                            + " traded one blue card for 2 cards.");
                    return;
                }
            } else if (card > (hand.size() - 1) && Character.UNCLE_WILL.equals(currentPlayer.getCharacter())) {
                if (this.uncleWillActions >= 1) {
                    userInterface.printInfo("Already used special abilitity this turn.");
                    return;
                }
                int cardIndex = userInterface.askDiscard(currentPlayer);
                if (cardIndex == -1) {
                    return;
                }
                Card removedCard = currentPlayer.getHand().remove(cardIndex);
                discardPile.add(removedCard);
                this.uncleWillActions += 1;

                List<Card> generalStoreCards = new ArrayList<>();
                for (int i = 0; i < players.size(); i++) {
                    if (deck.size() == 0) {
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
                    userInterface.printInfo(generalPlayer.getName() + " chooses " + storeCard.getName() + " from " + Card.CARDGENERALSTORE);
                    generalPlayer.getHand().add(storeCard);
                    generalPlayer = Turn.getNextPlayer(generalPlayer, players);
                }
                return;

            } else if (card > (hand.size() + singleUseInPlay.size() - 1) && Character.DOC_HOLYDAY.equals(currentPlayer.getCharacter())) {
                List<Card> cardsToDiscard = userInterface.chooseTwoDiscardForShoot(currentPlayer);
                if (cardsToDiscard.size() == 2) {
                    int discardSuit = Card.DIAMONDS;
                    for (Card discardcard : cardsToDiscard) {
                        if (discardcard.getSuit() != Card.DIAMONDS) {
                            discardSuit = discardcard.getSuit();
                        }
                        hand.remove(discardcard);
                        discardPile.add(discardcard);
                        userInterface.printInfo(currentPlayer.getName()
                                + " discards " + discardcard.getName()
                                + " for shoot.");
                    }
                    Bang tempBang = new Bang(Card.CARDBANG, discardSuit, Card.VALUE7, Card.TYPEPLAY);
                    boolean success = tempBang.play(currentPlayer, players, userInterface, deck, discardPile, this, true);
                    if (!success) {
                        hand.add(discardPile.remove());
                        hand.add(discardPile.remove());
                    }
                    return;
                }

            }
        }
        if (hand.size() + singleUseInPlay.size() == 0 || card == -1) {
            donePlaying = true;
            userInterface.printInfo(currentPlayer.getName()
                    + " is finished playing.");
            InPlay allInPlayActivate = currentPlayer.getInPlay();
            for (int i = 0; i < allInPlayActivate.size(); i++) {
                Card inPlayCardActivate = allInPlay.get(i);
                if (inPlayCardActivate instanceof SingleUse) {
                    SingleUse cardActivate = (SingleUse) inPlayCardActivate;
                    cardActivate.setReadyToPlay(true);
                }
            }
            return;
        }
        if (card >= hand.size()) {
            int chosen = card - hand.size();
            singleUseInPlay.get(chosen).play(currentPlayer, players, userInterface, deck,
                    discardPile, this);
        } else {
            Card playedCard = hand.get(card);
            if (playedCard.canPlay(currentPlayer, players, bangsPlayed)) {
                hand.remove(card);
                if (playedCard.getName().equals(Card.CARDGENERALSTORE)
                        || playedCard.getName().equals(Card.CARDGATLING)
                        || playedCard.getName().equals(Card.CARDINDIANS)) {
                    userInterface.printInfo(currentPlayer.getName() + " played a "
                            + playedCard.getName() + ".");
                }
                boolean success = playedCard.play(currentPlayer, players, userInterface, deck,
                        discardPile, this);
                if (success) {
                    if (playedCard instanceof Bang) {
                        bangsPlayed++;
                    }
                }
                if (!playedCard.getName().equals(Card.CARDCATBALOU)
                        && !playedCard.getName().equals(Card.CARDPANIC)
                        && !playedCard.getName().equals(Card.CARDJAIL)
                        && !playedCard.getName().equals(Card.CARDMISSED)
                        && !playedCard.getName().equals(Card.CARDBANG)
                        && !playedCard.getName().equals(Card.CARDDUEL)
                        && !playedCard.getName().equals(Card.CARDGENERALSTORE)
                        && !playedCard.getName().equals(Card.CARDGATLING)
                        && !playedCard.getName().equals(Card.CARDINDIANS)) {
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
                    if (Card.CARDMISSED.equals(card.getName())) {
                        return playedMiss;
                    } else if (Card.CARDDODGE.equals(card.getName())) {
                        return playedMiss;
                    } else if (Card.CARDBANG.equals(card.getName())
                            && Character.CALAMITY_JANET.equals(player.getCharacter())) {
                        return playedMiss;
                    } else if (Character.ELENA_FUENTE.equals(player.getCharacter())) {
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
                    || Card.CARDBEER.equals(player.getHand()
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
                if (Card.CARDBANG.equals(card.getName())) {
                    return playerShot;
                } else if (Card.CARDMISSED.equals(card.getName())
                        && Character.CALAMITY_JANET.equals(player.getCharacter())) {
                    return playerShot;
                }
            }
        }
    }

    public boolean isDonePlaying() {
        return donePlaying;
    }

    public void setDiscard(DiscardPile discardPile) {
        this.discardPile = discardPile;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public static List<Player> getPlayersWithinRange(Player player,
                                                     List<Player> players, int range) {
        List<Player> others = new ArrayList<>();
        Player cancelPlayer = new CancelPlayer();
        cancelPlayer.setHand(new Hand());
        cancelPlayer.setInPlay(new InPlay());
        others.add(cancelPlayer);
        for (Player otherPlayer : players) {
            int distance = AlivePlayers.getDistance(players.indexOf(player),
                    players.indexOf(otherPlayer), players.size());
            if (otherPlayer.getInPlay().hasItem(Card.CARDMUSTANG)) {
                if (!Character.BELLE_STAR.equals(player.getCharacter())) {
                    distance = distance + 1;
                }
            }
            if (otherPlayer.getInPlay().hasItem(Card.CARDHIDEOUT)) {
                if (!Character.BELLE_STAR.equals(player.getCharacter())) {
                    distance = distance + 1;
                }
            }
            if (Character.PAUL_REGRET.equals(otherPlayer.getCharacter())) {
                distance = distance + 1;
            }
            if (player.getInPlay().hasItem(Card.CARDSCOPE)) {
                distance = distance - 1;
            }
            if (player.getInPlay().hasItem(Card.CARDSILVER)) {
                distance = distance - 1;
            }
            if (Character.ROSE_DOOLAN.equals(player.getCharacter())) {
                distance = distance - 1;
            }
            if (distance <= range) {
                others.add(otherPlayer);
            }
        }
        return others(player, others);
    }

    public boolean isDynamiteExplode() {
        InPlay currentInPlay = currentPlayer.getInPlay();
        if (currentInPlay.hasItem(Card.CARDDYNAMITE)) {
            userInterface.printInfo(currentPlayer.getName()
                    + " is drawing to see if the dynamite explodes");
            Card drawnCard = draw(currentPlayer, deck, discardPile,
                    userInterface);
            return Card.isExplode(drawnCard);
        }
        return false;
    }

    public void passDynamite() {
        InPlay currentInPlay = currentPlayer.getInPlay();
        if (currentInPlay.hasItem(Card.CARDDYNAMITE)) {
            Card dynamiteCard = currentInPlay.removeDynamite();
            Player nextPlayer = getNextPlayer(currentPlayer, players);
            InPlay nextInPlay = nextPlayer.getInPlay();
            if (!nextInPlay.hasItem(Card.CARDDYNAMITE)) {
                userInterface.printInfo("Dynamite Passed to "
                        + nextPlayer.getName());
                nextInPlay.add(dynamiteCard);
            } else {
                nextPlayer = getNextPlayer(nextPlayer, players);
                nextInPlay = nextPlayer.getInPlay();
                userInterface.printInfo("Dynamite Passed to "
                        + nextPlayer.getName());
                nextInPlay.add(dynamiteCard);
            }
        }
    }

    public void discardDynamite() {
        InPlay currentInPlay = currentPlayer.getInPlay();
        if (currentInPlay.hasItem(Card.CARDDYNAMITE)) {
            Card dynamiteCard = currentInPlay.removeDynamite();
            discardPile.add(dynamiteCard);
        }
    }

    public static Card draw(Player player, Deck deck, DiscardPile discardPile,
                            UserInterface userInterface) {
        if (Character.LUCKY_DUKE.equals(player.getCharacter())) {
            List<Card> cards = pullCards(deck, 2, userInterface);
            int chosenCard = -1;
            while (chosenCard < 0 || chosenCard > (cards.size() - 1)) {
                chosenCard = userInterface.chooseDrawCard(player, cards);
            }
            for (Card card : cards) {
                discardPile.add(card);
            }
            Card drawnCard = cards.get(chosenCard);
            userInterface.printInfo(player.getName() + " drew a "
                    + Card.valueToString(drawnCard.getValue()) + " of "
                    + Card.suitToString(drawnCard.getSuit()) + " "
                    + drawnCard.getName());
            return cards.get(chosenCard);
        } else {
            if (deck.size() == 0) {
                userInterface.printInfo("Shuffling the deck");
            }
            Card card = deck.pull();
            userInterface.printInfo(player.getName() + " drew a "
                    + Card.valueToString(card.getValue()) + " of "
                    + Card.suitToString(card.getSuit()) + " "
                    + card.getName());
            discardPile.add(card);
            return card;
        }
    }

    public boolean isInJail() {
        InPlay currentInPlay = currentPlayer.getInPlay();
        if (currentInPlay.hasItem(Card.CARDJAIL)) {
            Card jailCard = currentInPlay.removeJail();
            userInterface.printInfo(currentPlayer.getName()
                    + " is drawing to break out of jail");
            Card drawn = draw(currentPlayer, deck, discardPile,
                    userInterface);
            boolean inJail = drawn.getSuit() != Card.HEARTS;
            discardPile.add(jailCard);
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

    public static int isBarrelSave(Player player, Deck deck, DiscardPile discardPile,
                                   UserInterface userInterface, int missesRequired, Player shooter) {
        int misses = 0;
        if (Character.JOURDONNAIS.equals(player.getCharacter())) {
            userInterface.printInfo(player.getName()
                    + " is drawing to be saved by a barrel");
            Card drawn = draw(player, deck, discardPile, userInterface);
            if (drawn.getSuit() == Card.HEARTS) {
                misses = misses + 1;
                userInterface.printInfo(player.getCharacter().characterName() + " drew a "
                        + Card.suitToString(Card.HEARTS) + " and was saved by his ability.");
            } else {
                userInterface.printInfo(player.getCharacter().characterName() + " drew a "
                        + Card.suitToString(drawn.getSuit())
                        + " and was not saved by his ability.");
            }
        }
        if (misses >= missesRequired) {
            return misses;
        }
        InPlay currentInPlay = player.getInPlay();
        if (currentInPlay.hasItem(Card.CARDBARREL)) {
            if (Character.BELLE_STAR.equals(shooter.getCharacter())) {
                userInterface.printInfo(player.getName()
                        + "'s barrel has no affect on " + shooter.getCharacter().characterName());
            } else {
                userInterface.printInfo(player.getName()
                        + " is drawing to be saved by a barrel");
                Card drawn = draw(player, deck, discardPile, userInterface);
                if (drawn.getSuit() == Card.HEARTS) {
                    misses = misses + 1;
                    userInterface.printInfo(player.getName() + " drew a "
                            + Card.suitToString(Card.HEARTS) + " and was saved by their barrel.");
                } else {
                    userInterface.printInfo(player.getName() + " drew a "
                            + Card.suitToString(drawn.getSuit())
                            + " and was not saved by their barrel.");
                }
            }
        }
        return misses;
    }

    public void damagePlayer(Player player, List<Player> players,
                             Player currentPlayer, int damage, Player damager, Deck deck,
                             DiscardPile discardPile, UserInterface userInterface) {
        player.setHealth(player.getHealth() - damage);
        if (player.getHealth() <= 0 && players.size() > 2) {
            boolean doNotPlayBeer = false;
            while (!doNotPlayBeer && player.getHealth() <= 0) {
                int playedBeer = validPlayBeer(player, userInterface);
                if (playedBeer != -1) {
                    if (Character.TEQUILA_JOE.equals(player.getCharacter())) {
                        player.setHealth(player.getHealth() + 2);
                        discardPile.add(player.getHand().remove(playedBeer));
                        userInterface.printInfo(player.getName() + " plays a beer and gains two lives.");
                    } else {
                        player.setHealth(player.getHealth() + 1);
                        discardPile.add(player.getHand().remove(playedBeer));
                        userInterface.printInfo(player.getName() + " plays a beer and gains one life.");
                        if (Character.MOLLY_STARK.equals(player.getCharacter())) {
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
                    deck, discardPile);
        } else {
            if (Character.BART_CASSIDY.equals(player.getCharacter())) {
                for (int i = 0; i < damage; i++) {
                    if (deck.size() == 0) {
                        userInterface.printInfo("Shuffling the deck");
                    }
                    player.getHand().add(deck.pull());
                    userInterface
                            .printInfo(player.getCharacter().characterName()
                                    + " draws a card from the deck because he was damaged.");
                }
            } else if (damager != null
                    && Character.EL_GRINGO.equals(player.getCharacter())) {
                Hand otherHand = damager.getHand();
                if (otherHand.size() != 0) {
                    Hand playerHand = player.getHand();
                    playerHand.add(otherHand.removeRandom());
                    userInterface.printInfo(player.getCharacter().characterName()
                            + " draws a card from " + damager.getName()
                            + " because he was damaged.");
                }
            }
        }
    }

    public void handleDeath(Player player, Player damager,
                            Player currentPlayer, List<Player> players,
                            UserInterface userInterface, Deck deck, DiscardPile discardPile) {
        //I think we can remove this
        //if (player.equals(currentPlayer)) {
        //TODO is this the right way to handle all death... set it to last player?
        //currentPlayer = getPreviousPlayer(currentPlayer, players);
        //}
        players.remove(player);
        userInterface.printInfo(player.getName() + " is dead. Role was "
                + player.getRole().roleName());
        if (!isGameOver(players)) {
            deadDiscardAll(player, players, discardPile, deck);
            if (damager != null) {
                if (damager.isSheriff()
                        && Role.DEPUTY.equals(player.getRole())) {
                    userInterface.printInfo(damager.getName()
                            + " killed own deputy, loses all cards");
                    discardAll(damager, discardPile);
                } else if (Role.OUTLAW.equals(player.getRole())) {
                    userInterface.printInfo(damager.getName()
                            + " killed an outlaw, draws 3 cards");
                    deckToHand(damager.getHand(), deck, 3, userInterface);
                }
            }
        }
    }

    public void discardAll(Player player, DiscardPile discardPile) {
        List<Card> discardCards = new ArrayList<>();
        Hand hand = player.getHand();
        while (hand.size() != 0) {
            discardCards.add(hand.remove(0));
        }
        InPlay inPlay = player.getInPlay();
        if (inPlay.hasGun()) {
            discardCards.add(inPlay.removeGun());
        }
        while (inPlay.count() > 0) {
            discardCards.add(inPlay.remove(0));
        }
        for (Card discardCard : discardCards) {
            hand.add(discardCard);
        }
        StringBuilder discardedCards = new StringBuilder();
        while (hand.size() != 0) {
            Card discardedCard = hand.remove(0);
            discardedCards.append(discardedCard.getName()).append(", ");
        }
        if (!discardedCards.toString().equals("")) {
            userInterface.printInfo(player.getName() + " discarded " + discardedCards.substring(0, discardedCards.length() - 2) + ".");
        }
    }

    public void deadDiscardAll(Player player, List<Player> players,
                               DiscardPile discardPile, Deck deck) {
        List<Card> discardCards = new ArrayList<>();
        Hand hand = player.getHand();
        while (hand.size() != 0) {
            discardCards.add(hand.remove(0));
        }
        InPlay inPlay = player.getInPlay();
        if (inPlay.hasGun()) {
            discardCards.add(inPlay.removeGun());
        }
        while (inPlay.count() > 0) {
            discardCards.add(inPlay.remove(0));
        }
        List<Player> vultureSams = new ArrayList<>();
        Player vultureSam = null;
        for (Player alivePlayer : players) {
            if (Character.VULTURE_SAM.equals(alivePlayer.getCharacter())) {
                vultureSam = alivePlayer;
                vultureSams.add(alivePlayer);
            }
        }
        if (vultureSam == null) {
            for (Card discardCard : discardCards) {
                hand.add(discardCard);
            }
            StringBuilder discardedCards = new StringBuilder();
            while (hand.size() != 0) {
                Card discardedCard = hand.remove(0);
                discardedCards.append(discardedCard.getName()).append(", ");
            }
            if (!discardedCards.toString().equals("")) {
                userInterface.printInfo(player.getName() + " discarded " + discardedCards.substring(0, discardedCards.length() - 2) + ".");
            }
        } else {
            if (vultureSams.size() == 1) {
                //One vulture sams
                for (Card card : discardCards) {
                    vultureSam.getHand().add(card);
                }
                userInterface.printInfo(vultureSams.get(0).getCharacter().characterName() + " takes "
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
            if (Character.GREG_DIGGER.equals(alivePlayer.getCharacter())) {
                int bonusHealth = 0;
                if (alivePlayer.getHealth() < alivePlayer.getMaxHealth()) {
                    alivePlayer.setHealth(alivePlayer.getHealth() + 1);
                    bonusHealth += 1;
                }
                if (alivePlayer.getHealth() < alivePlayer.getMaxHealth()) {
                    alivePlayer.setHealth(alivePlayer.getHealth() + 1);
                    bonusHealth += 1;
                }
                userInterface.printInfo(alivePlayer.getCharacter().characterName() + " gets " + bonusHealth + " health.");
            }
        }
        for (Player alivePlayer : players) {
            if (Character.HERB_HUNTER.equals(alivePlayer.getCharacter())) {
                Hand herbHand = alivePlayer.getHand();
                herbHand.add(deck.pull());
                herbHand.add(deck.pull());
                userInterface.printInfo(alivePlayer.getCharacter().characterName() + " draws 2 cards.");
            }
        }


    }

    public static boolean isGameOver(List<Player> players) {
        return isDead(Player.SHERIFF, players)
                || (isDead(Player.RENEGADE, players)
                && isDead(Player.OUTLAW, players));
    }

    private static boolean isDead(int role, List<Player> players) {
        for (Player player : players) {
            if (player.getRole().ordinal() == role) {
                return false;
            }
        }
        return true;
    }

    public static String getWinners(List<Player> players) {
        if (isDead(Player.DEPUTY, players)
                && isDead(Player.OUTLAW, players)
                && isDead(Player.SHERIFF, players) && players.size() == 1) {
            return "Renegade";
        } else if (isDead(Player.SHERIFF, players)
                && (!isDead(Player.DEPUTY, players) || !isDead(Player.OUTLAW, players) || !isDead(Player.RENEGADE, players))) {
            return "Outlaws";
        } else if (isDead(Player.OUTLAW, players) && isDead(Player.RENEGADE, players)) {
            return "Sheriff and Deputies";
        } else {
            throw new RuntimeException("No Winner");
        }
    }

    public static String getRoles(List<Player> players) {
        StringBuilder result = new StringBuilder();
        for (Player player : players) {
            result.append(player.getCharacter().characterName()).append(" was a ").append(player.getRole().roleName()).append(". ");
        }
        return result.toString();
    }

    public static void discardTwoCardsForLife(Player player, DiscardPile discardPile,
                                              UserInterface userInterface) {
        if (Character.SID_KETCHUM.equals(player.getCharacter())) {
            Hand hand = player.getHand();
            if (hand.size() >= 2) {
                List<Card> cardsToDiscard = null;
                while (cardsToDiscard == null || cardsToDiscard.size() % 2 != 0) {
                    cardsToDiscard = userInterface
                            .chooseTwoDiscardForLife(player);
                }
                if (cardsToDiscard.size() % 2 == 0) { //refactor simplfiy
                    for (Card card : cardsToDiscard) {
                        hand.remove(card);
                        discardPile.add(card);
                        userInterface.printInfo(player.getCharacter().characterName()
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
    }

    public static boolean playerHasCardsToTake(Player player) {
        boolean emptyHand = player.getHand().isEmpty();
        InPlay inPlay = player.getInPlay();
        boolean hasGun = inPlay.hasGun();
        boolean emptyInPlay = inPlay.isEmpty();
        return !emptyHand || hasGun || !emptyInPlay;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static List<Player> othersWithCardsToTake(Player player,
                                                     List<Player> others) {
        List<Player> othersCopy = new ArrayList<>();
        for (Player otherPlayer : others) {
            if (playerHasCardsToTake(otherPlayer)) {
                othersCopy.add(otherPlayer);
            }
        }
        othersCopy.remove(player);
        return othersCopy;
    }

    public static List<Player> others(Player player, List<Player> others) {
        List<Player> othersCopy = new ArrayList<>(others);
        othersCopy.remove(player);
        return othersCopy;
    }

    public static List<Card> pullCards(Deck deck, int countCards, UserInterface userInterface) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < countCards; i++) {
            if (deck.size() == 0) {
                userInterface.printInfo("Shuffling the deck");
            }
            cards.add(deck.pull());
        }
        return cards;
    }

    public static void deckToHand(Hand hand, Deck deck, int countCards, UserInterface userInterface) {
        for (int i = 0; i < countCards; i++) {
            if (deck.size() == 0) {
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
        cancelPlayer.setInPlay(new InPlay());
        others.add(cancelPlayer);
        for (Player otherPlayer : players) {
            boolean isInJail = otherPlayer.getInPlay().hasItem(Card.CARDJAIL);
            boolean isSheriff = otherPlayer.isSheriff();
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
            if (player instanceof CancelPlayer) {
                playersWithCards.add(player);
            } else if (playerHasCardsToTake(player)) {
                playersWithCards.add(player);
            }
        }
        return playersWithCards;
    }

    public static List<String> getPlayersNames(List<Player> players) {
        List<String> names = new ArrayList<>();
        for (Player player : players) {
            names.add(player.getName());
        }
        return names;
    }

    public static Player getValidChosenPlayer(Player player,
                                              List<Player> choosable, UserInterface userInterface) {
        int chosenPlayer = -1;
        while (chosenPlayer < 0 || chosenPlayer > choosable.size() - 1) {
            chosenPlayer = userInterface.askPlayer(player,
                    getPlayersNames(choosable));
        }
        return choosable.get(chosenPlayer);
    }

    public static boolean isBeerGiveHealth(List<Player> players) {
        return players.size() > 2;
    }

    public static boolean canPlayerHeal(Player player) {
        return player.getHealth() < player.getMaxHealth();
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
        if (discardPile.canDrawFromDiscard()) {
            return cardToGameStateCard(discardPile.peek());
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
        card.suit = Card.suitToString(fromCard.getSuit());
        card.value = Card.valueToString(fromCard.getValue());
        card.type = Card.typeToString(fromCard.getType());
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
            if (cards.size() == 0) {
                validCards = true;
            } else if (cards.size() == 2) {
                validCards = true;
                for (Card card : cards) {
                    //TODO figure this out xxx its broken
                    boolean invalidCard = !card.getName().equals(Card.CARDMISSED) &&
                            !card.getName().equals(Card.CARDDODGE) &&
                            (!card.getName().equals(Card.CARDBANG) || !Character.CALAMITY_JANET.equals(player.getCharacter())) &&
                            !Character.ELENA_FUENTE.equals(player.getCharacter()) &&
                            !(card instanceof SingleUseMissed);
                    if (invalidCard) {
                        validCards = false;
                        break;
                    }
                }
            }
        }
        return cards;
    }

    public String getRoleForName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player.getRole().roleName();
            }
        }
        return null;
    }

    public String roleToGoal(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player.getRole().goal();
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