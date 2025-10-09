package com.chriscarr.bang.turn;


import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;
import java.util.function.BiFunction;

public final class TurnApi {
    private final TriFunction<Deck, Integer, UserInterface, List<Card>> pullCardsFn;
    private final TriFunction<Player, List<Card>, UserInterface, Card> chooseValidCardToPutBackFn;
    private final TriFunction<Player, List<Player>, UserInterface, Player> chooseValidChosenPlayerFn;
    private final BiFunction<Player, List<Player>, Player> nextPlayerFn;

    private TurnApi(TriFunction<Deck, Integer, UserInterface, List<Card>> pullCardsFn, TriFunction<Player, List<Card>, UserInterface, Card> chooseValidCardToPutBackFn, TriFunction<Player, List<Player>, UserInterface, Player> chooseValidChosenPlayerFn, BiFunction<Player, List<Player>, Player> nextPlayerFn) {
        this.pullCardsFn = pullCardsFn;
        this.chooseValidCardToPutBackFn = chooseValidCardToPutBackFn;
        this.chooseValidChosenPlayerFn = chooseValidChosenPlayerFn;
        this.nextPlayerFn = nextPlayerFn;
    }

    public static TurnApi of(TriFunction<Deck, Integer, UserInterface, List<Card>> pullCardsFn,
                             TriFunction<Player, List<Card>, UserInterface, Card> chooseValidCardToPutBackFn,
                             TriFunction<Player, List<Player>, UserInterface, Player> chooseValidChosenPlayerFn,
                             BiFunction<Player, List<Player>, Player> nextPlayerFn) {
        return new TurnApi(pullCardsFn, chooseValidCardToPutBackFn, chooseValidChosenPlayerFn, nextPlayerFn);
    }

    public List<Card> pullCards(Deck deck, int count, UserInterface userInterface) {
        return pullCardsFn.apply(deck, count, userInterface);
    }

    public Card chooseValidCardToPutBack(Player player, List<Card> cards, UserInterface userInterface) {
        return chooseValidCardToPutBackFn.apply(player, cards, userInterface);
    }

    public Player validChosenPlayer(Player player, List<Player> players, UserInterface userInterface) {
        return chooseValidChosenPlayerFn.apply(player, players, userInterface);
    }

    public Player nextPlayer(Player player, List<Player> players) {
        return nextPlayerFn.apply(player, players);
    }
}
