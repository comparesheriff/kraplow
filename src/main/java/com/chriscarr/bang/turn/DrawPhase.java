package com.chriscarr.bang.turn;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardSuit;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;

class DrawPhase implements TurnPhase {
    @Override
    public void carryOut(TurnContext context) {
        drawCards(context.currentPlayer(), context.deck(), context.ui(), context.players(), context.discard(), context.api());
    }

    public void drawCards(Player player, Deck deck, UserInterface userInterface, List<Player> players, Discard discard, TurnApi api) {
        if (Character.KITCARLSON.equals(player.getCharacter())) {
            drawCardsForKitCarlson(player, deck, userInterface, api);
        } else if (Character.JESSEJONES.equals(player.getCharacter())) {
            drawCardsForJesseJones(player, deck, userInterface, players, api);
        } else if (Character.PATBRENNAN.equals(player.getCharacter()))
            drawCardsForPatBrennan(player, deck, userInterface, players, api);
        else if (Character.PEDRORAMIREZ.equals(player.getCharacter()))
            drawCardsForPedroRamirez(player, deck, userInterface, discard);
        else if (Character.PIXIEPETE.equals(player.getCharacter())) drawCardsForPixiePete(deck, player.getHand());
        else if (Character.BILLNOFACE.equals(player.getCharacter()))
            drawCardsForBillNoFace(player, deck, userInterface);
        else if (Character.CLAUSTHESAINT.equals(player.getCharacter()))
            drawCardsForClausTheSaint(player, deck, userInterface, players, api);
        else drawCardsNormally(player, deck, userInterface);
    }

    private static void drawCardsNormally(Player player, Deck deck, UserInterface userInterface) {
        Hand hand = player.getHand();
        hand.add(deck.pull());
        Card secondCard = deck.pull();
        hand.add(secondCard);
        if (Character.BLACKJACK.equals(player.getCharacter())) {
            CardSuit suit = secondCard.getSuit();
            userInterface.printInfo(
                player.getCharacter().getName()
                    + " drew a "
                    + suit.getLabel()
                    + " "
                    + secondCard.getName());
            if (suit == CardSuit.HEARTS || suit == CardSuit.DIAMONDS) {
                hand.add(deck.pull());
                userInterface.printInfo(
                    player.getCharacter().getName() + " drew a third card from the deck.");
            }
        }
    }

    private static void drawCardsForClausTheSaint(Player player, Deck deck, UserInterface userInterface, List<Player> players, TurnApi api) {
        Hand hand = player.getHand();
        List<Card> cards = api.pullCards(deck, players.size() + 1, userInterface);
        Player generalPlayer = api.nextPlayer(player, players);
        while (!generalPlayer.equals(player)) {
            Card card = api.chooseValidCardToPutBack(player, cards, userInterface);
            cards.remove(card);
            userInterface.printInfo(
                player.getName() + " gives " + generalPlayer.getName() + " a card.");
            generalPlayer.getHand().add(card);
            generalPlayer = api.nextPlayer(generalPlayer, players);
        }
        hand.addAll(cards);
    }

    private static void drawCardsForBillNoFace(Player player, Deck deck, UserInterface userInterface) {
        Hand hand = player.getHand();
        hand.add(deck.pull());
        int cardsToDraw = player.getMaxHealth() - player.getHealth();
        while (cardsToDraw > 0) {
            hand.add(deck.pull());
            cardsToDraw -= 1;
        }
        userInterface.printInfo(
            player.getName()
                + " drew "
                + (player.getMaxHealth() - player.getHealth() + 1)
                + " card(s) from the deck.");
    }

    private static void drawCardsForPixiePete(Deck deck, Hand hand) {
        hand.add(deck.pull());
        hand.add(deck.pull());
        hand.add(deck.pull());
    }

    private static void drawCardsForPedroRamirez(Player player, Deck deck, UserInterface userInterface, Discard discard) {
        Hand hand = player.getHand();
        if (!discard.isEmpty()) {
            boolean chosenDiscard = userInterface.chooseDiscard(player, discard.getLast());
            if (chosenDiscard) {
                Card discardCard = discard.removeLast();
                hand.add(discardCard);
                userInterface.printInfo(
                    player.getCharacter().getName()
                        + " drew a "
                        + discardCard.getName()
                        + " from the discard pile.");
            } else {
                hand.add(deck.pull());
                userInterface.printInfo(player.getCharacter().getName() + " drew a card from the deck.");
            }
        } else {
            hand.add(deck.pull());
            userInterface.printInfo(player.getCharacter().getName() + " drew a card from the deck.");
        }
        hand.add(deck.pull());
    }

    private static void drawCardsForPatBrennan(Player player, Deck deck, UserInterface userInterface, List<Player> players, TurnApi api) {
        Hand hand = player.getHand();
        boolean chosenFromPlayer = userInterface.chooseFromPlayer(player);
        if (chosenFromPlayer) {
            List<Player> otherPlayers = new ArrayList<>();
            for (Player other : players) {
                if (!other.equals(player)
                    && (!other.getCardsInPlay().isEmpty() || other.getCardsInPlay().hasGun())) {
                    otherPlayers.add(other);
                }
            }
            if (!otherPlayers.isEmpty()) {
                Player chosenPlayer = api.validChosenPlayer(player, otherPlayers, userInterface);
                int chosenCard = -3;
                while (chosenCard < -2 || chosenCard > chosenPlayer.getCardsInPlay().size() - 1) {
                    chosenCard = userInterface.askOthersCard(player, chosenPlayer.getCardsInPlay(), false);
                }
                if (chosenCard == -2) {
                    Card card = chosenPlayer.getCardsInPlay().removeGun();
                    hand.add(card);
                    userInterface.printInfo(
                        player.getName()
                            + " takes a "
                            + card.getName()
                            + " from "
                            + chosenPlayer.getName());
                } else {
                    Card card = chosenPlayer.getCardsInPlay().remove(chosenCard);
                    hand.add(card);
                    userInterface.printInfo(
                        player.getName()
                            + " takes a "
                            + card.getName()
                            + " from "
                            + chosenPlayer.getName());
                }
            } else {
                hand.add(deck.pull());
                hand.add(deck.pull());
            }
        } else {
            hand.add(deck.pull());
            hand.add(deck.pull());
        }
    }

    private static void drawCardsForJesseJones(Player player, Deck deck, UserInterface userInterface, List<Player> players, TurnApi api) {
        Hand hand = player.getHand();
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
            Player chosenPlayer = api.validChosenPlayer(player, otherPlayers, userInterface);
            chosenPlayer.removeRandom().ifPresent(hand::add);
            userInterface.printInfo(
                player.getCharacter().getName()
                    + " drew a card from "
                    + chosenPlayer.getName()
                    + " hand.");
        } else {
            if (deck.isEmpty()) {
                userInterface.printInfo("Shuffling the deck");
            }
            hand.add(deck.pull());
            userInterface.printInfo(player.getCharacter().getName() + " drew a card from the deck.");
        }
        hand.add(deck.pull());
    }

    private static void drawCardsForKitCarlson(Player player, Deck deck, UserInterface userInterface, TurnApi api) {
        Hand hand = player.getHand();
        List<Card> cards = api.pullCards(deck, 3, userInterface);
        Card cardToPutBack = api.chooseValidCardToPutBack(player, cards, userInterface);
        cards.remove(cardToPutBack);
        deck.add(cardToPutBack);
        hand.addAll(cards);
        userInterface.printInfo(player.getCharacter().getName() + " put a card back on the draw pile");
    }

}
