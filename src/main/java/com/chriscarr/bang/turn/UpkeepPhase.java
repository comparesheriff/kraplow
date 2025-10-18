package com.chriscarr.bang.turn;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.CardSuit;
import com.chriscarr.bang.services.GameOverService;
import com.chriscarr.bang.turn.ports.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class UpkeepPhase implements TurnPhase {
    @Override
    public void carryOut(TurnContext context) {
        //TODO figure out how to skip turn
        TurnApiPorts ports = TurnApiPorts.from(context.api());
        boolean skipTurn = handleUpkeep(context.ui(),
            context.currentPlayer(),
            context.players(),
            context.deck(),
            context.discard(),
            ports,
            ports,
            ports,
            ports);
        context.withInJail(skipTurn);
    }

    private boolean handleUpkeep(UserInterface ui,
                                 Player currentPlayer,
                                 List<Player> players,
                                 Deck deck,
                                 Discard discard,
                                 DamagePort damagePort,
                                 DrawPort drawPort,
                                 TurnOrderPort turnOrderPort,
                                 TargetingPort targetingPort) {
        ui.printInfo(currentPlayer.getName() + "'s turn.");
        if (com.chriscarr.bang.Character.VERACUSTER.equals(currentPlayer.getCharacter())) {
            handleVeraCuster(ui, currentPlayer, players, targetingPort);
        }

        if (isDynamiteExplode(currentPlayer, ui, deck, discard, drawPort)) {
            discardDynamite(currentPlayer, discard);
            ui.printInfo("Dynamite Exploded on " + currentPlayer.getName());
            damagePort.damagePlayer(currentPlayer, players, currentPlayer, 3, null, deck, discard, ui);
            if (GameOverService.isGameOver(players)) {
                ui.printInfo("Winners are " + GameOverService.getWinners(players) + " " + GameOverService.revealRolesOnGameEnd(players));
                throw new EndOfGameException("Game over");
            }
        } else {
            passDynamite(currentPlayer, ui, players, turnOrderPort);
        }
        return isInJail(currentPlayer, ui, deck, discard, drawPort);
    }

    private void handleVeraCuster(UserInterface ui, Player currentPlayer, List<Player> players, TargetingPort targetingPort) {
        List<Player> otherPlayers = new ArrayList<>();
        for (Player other : players) {
            if (!other.equals(currentPlayer)) {
                otherPlayers.add(other);
            }
        }
        ui.printInfo(Character.VERACUSTER + " will choose the abilities of another player");
        Player chosenPlayer = targetingPort.validChosenPlayer(currentPlayer, otherPlayers, ui);
        currentPlayer.setCharacter(chosenPlayer.getCharacter());
        ui.printInfo(currentPlayer.getCharacter().getName() + " chose the abilities of " + chosenPlayer.getName());
    }

    private boolean isDynamiteExplode(Player currentPlayer, UserInterface ui, Deck deck, Discard discard, DrawPort draw) {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.DYNAMITE)) {
            ui.printInfo(currentPlayer.getName() + " is drawing to see if the dynamite explodes");
            Card drawnCard = draw.draw(currentPlayer, deck, discard, ui);
            return Card.isExplode(drawnCard);
        }
        return false;
    }

    private void passDynamite(Player currentPlayer, UserInterface ui, List<Player> players, TurnOrderPort turnOrderPort) {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.DYNAMITE)) {
            Optional<Card> dynamiteOptional = currentCardsInPlay.removeDynamite();
            if (dynamiteOptional.isEmpty()) {
                throw new IllegalStateException("Dynamite card not found in cards in play.");
            }
            Player nextPlayer = turnOrderPort.nextPlayer(currentPlayer, players);
            CardsInPlay nextCardsInPlay = nextPlayer.getCardsInPlay();
            if (!nextCardsInPlay.hasItem(CardName.DYNAMITE)) {
                ui.printInfo("Dynamite Passed to " + nextPlayer.getName());
                nextCardsInPlay.add(dynamiteOptional.get());
            } else {
                nextPlayer = turnOrderPort.nextPlayer(nextPlayer, players);
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

    private boolean isInJail(Player currentPlayer, UserInterface ui, Deck deck, Discard discard, DrawPort drawPort) {
        CardsInPlay currentCardsInPlay = currentPlayer.getCardsInPlay();
        if (currentCardsInPlay.hasItem(CardName.JAIL)) {
            Optional<Card> jailOptional = currentCardsInPlay.removeJail();
            if (jailOptional.isEmpty()) {
                throw new IllegalStateException("Jail card not found in cards in play.");
            }
            ui.printInfo(currentPlayer.getName() + " is drawing to break out of jail");
            Card drawn = drawPort.draw(currentPlayer, deck, discard, ui);
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
