package com.chriscarr.bang.turn.ports;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.UserInterface;

public interface DrawPort {
    Card draw(Player currentPlayer, Deck deck, Discard discard, UserInterface ui);
}
