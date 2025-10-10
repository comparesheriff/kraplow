package com.chriscarr.bang.turn;


import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class TurnApi {
    private final TriFunction<Deck, Integer, UserInterface, List<Card>> pullCardsFn;
    private final TriFunction<Player, List<Card>, UserInterface, Card> chooseValidCardToPutBackFn;
    private final TriFunction<Player, List<Player>, UserInterface, Player> chooseValidChosenPlayerFn;
    private final BiFunction<Player, List<Player>, Player> nextPlayerFn;
    private final QuadFunction<Player, Deck, Discard, UserInterface, Card> drawFn;
    private final OctConsumer<Player, List<Player>, Player, Integer, Player, Deck, Discard, UserInterface> damagePlayerFn;
    private final Supplier<Boolean> checkDonePlayingFn;
    private final Consumer<TurnContext> playFn;
    private final Consumer<Boolean> setDonePlayingFn;

    private TurnApi(TriFunction<Deck, Integer, UserInterface, List<Card>> pullCardsFn,
                    TriFunction<Player, List<Card>, UserInterface, Card> chooseValidCardToPutBackFn,
                    TriFunction<Player, List<Player>, UserInterface, Player> chooseValidChosenPlayerFn,
                    BiFunction<Player, List<Player>, Player> nextPlayerFn,
                    QuadFunction<Player, Deck, Discard, UserInterface, Card> drawFn,
                    OctConsumer<Player, List<Player>, Player, Integer, Player, Deck, Discard, UserInterface> damagePlayerFn,
                    Supplier<Boolean> checkDonePlayingFn,
                    Consumer<TurnContext> playFn,
                    Consumer<Boolean> setDonePlayingFn) {
        this.pullCardsFn = pullCardsFn;
        this.chooseValidCardToPutBackFn = chooseValidCardToPutBackFn;
        this.chooseValidChosenPlayerFn = chooseValidChosenPlayerFn;
        this.nextPlayerFn = nextPlayerFn;
        this.drawFn = drawFn;
        this.damagePlayerFn = damagePlayerFn;
        this.checkDonePlayingFn = checkDonePlayingFn;
        this.playFn = playFn;
        this.setDonePlayingFn = setDonePlayingFn;
    }

    public static TurnApi of(TriFunction<Deck, Integer, UserInterface, List<Card>> pullCardsFn,
                             TriFunction<Player, List<Card>, UserInterface, Card> chooseValidCardToPutBackFn,
                             TriFunction<Player, List<Player>, UserInterface, Player> chooseValidChosenPlayerFn,
                             BiFunction<Player, List<Player>, Player> nextPlayerFn,
                             QuadFunction<Player, Deck, Discard, UserInterface, Card> drawFn,
                             OctConsumer<Player, List<Player>, Player, Integer, Player, Deck, Discard, UserInterface> damagePlayerFn,
                             Supplier<Boolean> checkDonePlayingFn,
                             Consumer<TurnContext> playFn,
                             Consumer<Boolean> setDonePlayingFn) {
        return new TurnApi(pullCardsFn, chooseValidCardToPutBackFn, chooseValidChosenPlayerFn, nextPlayerFn, drawFn, damagePlayerFn, checkDonePlayingFn, playFn, setDonePlayingFn);
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

    public Card draw(Player currentPlayer, Deck deck, Discard discard, UserInterface ui) {
        return drawFn.apply(currentPlayer, deck, discard, ui);
    }

    public void damagePlayer(Player currentPlayer, List<Player> players, Player currentPlayer1, int i, Player o, Deck deck, Discard discard, UserInterface ui) {
        damagePlayerFn.accept(currentPlayer, players, currentPlayer1, i, o, deck, discard, ui);
    }

    public boolean checkDonePlaying() {
        return checkDonePlayingFn.get();
    }

    public void setDonePlaying(boolean donePlaying) {
        setDonePlayingFn.accept(donePlaying);
    }

    public void play(TurnContext ctx) {
        playFn.accept(ctx);
    }
}
