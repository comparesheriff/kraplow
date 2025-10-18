package com.chriscarr.bang.turn.ports;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public interface HandPort {
    List<Card> pullCards(Deck deck, int count, UserInterface ui);

    Card chooseValidCardToPutBack(Player player, List<Card> cards, UserInterface ui);
}
