package com.chriscarr.bang.turn;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.CardSuit;
import com.chriscarr.bang.services.GameOverService;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class UpkeepPhase implements TurnPhase {
    @Override
    public void carryOut(TurnContext context) {
        //TODO figure out how to skip turn
        boolean skipTurn = handleUpkeep(context.ui(), context.currentPlayer(), context.players(), context.api(), context.deck(), context.discard());
        context.withInJail(skipTurn);
    }

    private boolean handleUpkeep(UserInterface ui, Player currentPlayer, List<Player> players, TurnApi api, Deck deck, Discard discard) {
        ui.printInfo(currentPlayer.getName() + "'s turn.");
        if (com.chriscarr.bang.Character.VERACUSTER.equals(currentPlayer.getCharacter())) {
            handleVeraCuster(ui, currentPlayer, players, api);
        }

        if (isDynamiteExplode(currentPlayer, ui, deck, discard, api)) {
            discardDynamite(currentPlayer, discard);
            ui.printInfo("Dynamite Exploded on " + currentPlayer.getName());
            api.damagePlayer(currentPlayer, players, currentPlayer, 3, null, deck, discard, ui);
            if (GameOverService.isGameOver(players)) {
                ui.printInfo("Winners are " + GameOverService.getWinners(players) + " " + GameOverService.revealRolesOnGameEnd(players));
                throw new EndOfGameException("Game over");
            }
        } else {
            passDynamite(currentPlayer, api, ui, players);
        }
        return isInJail(currentPlayer, ui, deck, discard, api);
    }

    private void handleVeraCuster(UserInterface ui, Player currentPlayer, List<Player> players, TurnApi api) {
        List<Player> otherPlayers = new ArrayList<>();
        for (Player other : players) {
            if (!other.equals(currentPlayer)) {
                otherPlayers.add(other);
            }
        }
        ui.printInfo(Character.VERACUSTER + " will choose the abilities of another player");
        Player chosenPlayer = api.validChosenPlayer(currentPlayer, otherPlayers, ui);
        currentPlayer.setCharacter(chosenPlayer.getCharacter());
        ui.printInfo(currentPlayer.getCharacter().getName() + " chose the abilities of " + chosenPlayer.getName());
    }

    private boolean isDynamiteExplode(Player currentPlayer, UserInterface ui, Deck deck, Discard discard, TurnApi api) {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.DYNAMITE)) {
            ui.printInfo(currentPlayer.getName() + " is drawing to see if the dynamite explodes");
            Card drawnCard = api.draw(currentPlayer, deck, discard, ui);
            return Card.isExplode(drawnCard);
        }
        return false;
    }

    private void passDynamite(Player currentPlayer, TurnApi api, UserInterface ui, List<Player> players) {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.DYNAMITE)) {
            Optional<Card> dynamiteOptional = currentCardsInPlay.removeDynamite();
            if (dynamiteOptional.isEmpty()) {
                throw new IllegalStateException("Dynamite card not found in cards in play.");
            }
            Player nextPlayer = api.nextPlayer(currentPlayer, players);
            CardsInPlay nextCardsInPlay = nextPlayer.getCardsInPlay();
            if (!nextCardsInPlay.hasItem(CardName.DYNAMITE)) {
                ui.printInfo("Dynamite Passed to " + nextPlayer.getName());
                nextCardsInPlay.add(dynamiteOptional.get());
            } else {
                nextPlayer = api.nextPlayer(nextPlayer, players);
                nextCardsInPlay = nextPlayer.getCardsInPlay();
                ui.printInfo("Dynamite Passed to " + nextPlayer.getName());
                nextCardsInPlay.add(dynamiteOptional.get());
            }
        }
    }

    private void discardDynamite(Player currentPlayer, Discard discard) {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.DYNAMITE)) {
            currentCardsInPlay.removeDynamite().ifPresent(discard::add);
        }
    }

    private boolean isInJail(Player currentPlayer, UserInterface ui, Deck deck, Discard discard, TurnApi api) {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.JAIL)) {
            Optional<Card> jailOptional = currentCardsInPlay.removeJail();
            if (jailOptional.isEmpty()) {
                throw new IllegalStateException("Jail card not found in cards in play.");
            }
            ui.printInfo(currentPlayer.getName() + " is drawing to break out of jail");
            Card drawn = api.draw(currentPlayer, deck, discard, ui);
            boolean inJail = drawn.getSuit() != CardSuit.HEARTS;
            discard.add(jailOptional.get());
            if (inJail) {
                ui.printInfo(currentPlayer.getName() + " stays in jail");
            } else {
                ui.printInfo(currentPlayer.getName() + " breaks out of jail");
            }
            return inJail;
        }
        return false;
    }
}
