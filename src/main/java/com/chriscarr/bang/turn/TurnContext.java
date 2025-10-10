package com.chriscarr.bang.turn;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.userinterface.UserInterface;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Objects;

public final class TurnContext {
    private final Deck deck;
    private final Discard discard;
    private final List<Player> players;
    private final UserInterface ui;

    private Player currentPlayer;
    private TurnApi turnApi;
    private boolean inJail;

    private TurnContext(Deck deck, Discard discard, List<Player> players, UserInterface ui) {
        this.deck = deck;
        this.discard = discard;
        this.players = players;
        this.ui = ui;
    }

    public static TurnContext of(Deck deck, Discard discard, List<Player> players, UserInterface ui) {
        return new TurnContext(deck, discard, players, ui);
    }

    public Deck deck() {
        return deck;
    }

    public Discard discard() {
        return discard;
    }

    public List<Player> players() {
        return players;
    }

    public UserInterface ui() {
        return ui;
    }

    public TurnApi api() {
        return turnApi;
    }

    public TurnContext withApi(@Nonnull TurnApi turnApi) {
        this.turnApi = Objects.requireNonNull(turnApi);
        return this;
    }

    public Player currentPlayer() {
        return currentPlayer;
    }

    public TurnContext withCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        return this;
    }

    public boolean inJail() {
        return inJail;
    }

    public TurnContext withInJail(boolean inJail) {
        this.inJail = inJail;
        return this;
    }
}
