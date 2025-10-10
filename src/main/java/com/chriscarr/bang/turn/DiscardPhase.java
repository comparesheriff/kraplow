package com.chriscarr.bang.turn;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.UserInterface;

class DiscardPhase implements TurnPhase {
    @Override
    public void carryOut(TurnContext context) {
        discard(context.currentPlayer(), context.ui(), context.discard());
    }

    private void discard(Player player, UserInterface ui, Discard discard) {
        int maxHandSize = player.getHealth();
        if (Character.SEANMALLORY.equals(player.getCharacter())) {
            maxHandSize = 10;
        }
        Hand hand = player.getHand();
        StringBuilder discardedCards = new StringBuilder();
        while (hand.size() > maxHandSize) {
            Card discardedCard = askPlayerToDiscard(player, discard, ui);
            discardedCards.append(discardedCard.getName()).append(", ");
        }
        if (!discardedCards.toString().isEmpty()) {
            ui.printInfo(
                player.getName()
                    + " discarded "
                    + discardedCards.substring(0, discardedCards.length() - 2)
                    + ".");
        }
    }

    private Card askPlayerToDiscard(Player player, Discard discard, UserInterface ui) {
        int card = -1;
        while (card < 0 || card > player.getHand().size() - 1) {
            card = ui.askDiscard(player);
        }
        Card removedCard = player.getHand().remove(card);
        discard.add(removedCard);
        return removedCard;
    }

}
