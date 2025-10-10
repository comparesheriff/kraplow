package com.chriscarr.bang.turn;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Player;
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

    public void drawCards(Player player, Deck deck, UserInterface ui, List<Player> players, Discard discard, TurnApi api) {
        switch (player.getCharacter()) {
            case KITCARLSON -> drawCardsForKitCarlson(player, deck, ui, api);
            case JESSEJONES -> drawCardsForJesseJones(player, deck, ui, players, api);
            case PATBRENNAN -> drawCardsForPatBrennan(player, deck, ui, players, api);
            case PEDRORAMIREZ -> drawCardsForPedroRamirez(player, deck, ui, discard);
            case PIXIEPETE -> drawCardsForPixiePete(deck, player.getHand());
            case BILLNOFACE -> drawCardsForBillNoFace(player, deck, ui);
            case CLAUSTHESAINT -> drawCardsForClausTheSaint(player, deck, ui, players, api);
            case BLACKJACK -> drawCardsForBlackJack(player, deck, ui);
            case null, default -> drawCardsNormally(player, deck);
        }
    }

    private void drawCardsForBlackJack(Player player, Deck deck, UserInterface ui) {
        drawCardsNormally(player, deck);
        Hand hand = player.getHand();
        Card secondCard = hand.getLast();
        CardSuit suit = secondCard.getSuit();
        ui.printInfo(
            player.getCharacter().getName()
                + " drew a "
                + suit.getLabel()
                + " "
                + secondCard.getName());
        if (suit == CardSuit.HEARTS || suit == CardSuit.DIAMONDS) {
            drawFromDeck(deck, hand, 1);
            ui.printInfo(player.getCharacter().getName() + " drew a third card from the deck.");
        }
    }

    private void drawCardsNormally(Player player, Deck deck) {
        Hand hand = player.getHand();
        drawFromDeck(deck, hand, 2);
    }

    private void drawCardsForClausTheSaint(Player player, Deck deck, UserInterface ui, List<Player> players, TurnApi api) {
        Hand hand = player.getHand();
        List<Card> cards = api.pullCards(deck, players.size() + 1, ui);
        Player generalPlayer = api.nextPlayer(player, players);
        while (!generalPlayer.equals(player)) {
            Card card = api.chooseValidCardToPutBack(player, cards, ui);
            cards.remove(card);
            ui.printInfo(player.getName() + " gives " + generalPlayer.getName() + " a card.");
            generalPlayer.getHand().add(card);
            generalPlayer = api.nextPlayer(generalPlayer, players);
        }
        hand.addAll(cards);
    }

    private void drawCardsForBillNoFace(Player player, Deck deck, UserInterface ui) {
        Hand hand = player.getHand();
        drawFromDeck(deck, hand, 1);
        int cardsToDraw = player.getMaxHealth() - player.getHealth();
        while (cardsToDraw > 0) {
            drawFromDeck(deck, hand, 1);
            cardsToDraw -= 1;
        }
        ui.printInfo(player.getName() + " drew " + (cardsToDraw + 1) + " card(s) from the deck.");
    }

    private void drawCardsForPixiePete(Deck deck, Hand hand) {
        drawFromDeck(deck, hand, 3);
    }

    private void drawCardsForPedroRamirez(Player player, Deck deck, UserInterface ui, Discard discard) {
        Hand hand = player.getHand();
        if (!discard.isEmpty()) {
            boolean chosenDiscard = ui.chooseDiscard(player, discard.getLast());
            if (chosenDiscard) {
                Card discardCard = discard.removeLast();
                hand.add(discardCard);
                ui.printInfo(
                    player.getCharacter().getName()
                        + " drew a "
                        + discardCard.getName()
                        + " from the discard pile.");
                drawFromDeck(deck, hand, 1);
                return;
            }
        }
        ui.printInfo(player.getCharacter().getName() + " drew a card from the deck.");
        drawFromDeck(deck, hand, 2);
    }

    private void drawCardsForPatBrennan(Player player, Deck deck, UserInterface ui, List<Player> players, TurnApi api) {
        Hand hand = player.getHand();
        boolean chosenFromPlayer = ui.chooseFromPlayer(player);
        if (chosenFromPlayer) {
            List<Player> otherPlayers = new ArrayList<>();
            for (Player other : players) {
                if (!other.equals(player)
                    && (!other.getCardsInPlay().isEmpty() || other.getCardsInPlay().hasGun())) {
                    otherPlayers.add(other);
                }
            }
            if (!otherPlayers.isEmpty()) {
                Player chosenPlayer = api.validChosenPlayer(player, otherPlayers, ui);
                int chosenCard = -3;
                while (chosenCard < -2 || chosenCard > chosenPlayer.getCardsInPlay().size() - 1) {
                    chosenCard = ui.askOthersCard(player, chosenPlayer.getCardsInPlay(), false);
                }
                Card card;
                if (chosenCard == -2) {
                    card = chosenPlayer.getCardsInPlay().removeGun();
                } else {
                    card = chosenPlayer.getCardsInPlay().remove(chosenCard);
                }
                hand.add(card);
                ui.printInfo(
                    player.getName()
                        + " takes a "
                        + card.getName()
                        + " from "
                        + chosenPlayer.getName());
            } else {
                drawFromDeck(deck, hand, 2);
            }
        } else {
            drawFromDeck(deck, hand, 2);
        }
    }

    private void drawCardsForJesseJones(Player player, Deck deck, UserInterface ui, List<Player> players, TurnApi api) {
        Hand hand = player.getHand();
        List<Player> otherPlayers = new ArrayList<>();
        for (Player other : players) {
            if (!other.equals(player) && !other.getHand().isEmpty()) {
                otherPlayers.add(other);
            }
        }
        boolean chosenFromPlayer = false;
        if (!otherPlayers.isEmpty()) {
            chosenFromPlayer = ui.chooseFromPlayer(player);
        }
        if (chosenFromPlayer) {
            Player chosenPlayer = api.validChosenPlayer(player, otherPlayers, ui);
            chosenPlayer.removeRandom().ifPresent(hand::add);
            ui.printInfo(
                player.getCharacter().getName()
                    + " drew a card from "
                    + chosenPlayer.getName()
                    + " hand.");
        } else {
            if (deck.isEmpty()) {
                ui.printInfo("Shuffling the deck");
            }
            drawFromDeck(deck, hand, 1);
            ui.printInfo(player.getCharacter().getName() + " drew a card from the deck.");
        }
        drawFromDeck(deck, hand, 1);
    }

    private void drawCardsForKitCarlson(Player player, Deck deck, UserInterface ui, TurnApi api) {
        Hand hand = player.getHand();
        List<Card> cards = api.pullCards(deck, 3, ui);
        Card cardToPutBack = api.chooseValidCardToPutBack(player, cards, ui);
        cards.remove(cardToPutBack);
        deck.add(cardToPutBack);
        hand.addAll(cards);
        ui.printInfo(player.getCharacter().getName() + " put a card back on the draw pile");
    }

    private void drawFromDeck(Deck deck, Hand hand, int cardsToPull) {
        for (int i = 0; i < cardsToPull; i++) {
            hand.add(deck.pull());
        }
    }

}
