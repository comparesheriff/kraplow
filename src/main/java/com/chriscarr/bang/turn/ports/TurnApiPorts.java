package com.chriscarr.bang.turn.ports;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.turn.TurnApi;
import com.chriscarr.bang.turn.TurnContext;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public final class TurnApiPorts implements HandPort, DamagePort, DrawPort, TargetingPort, PlayerControlPort, TurnOrderPort {
    private final TurnApi api;

    private TurnApiPorts(TurnApi api) {
        this.api = api;
    }

    public static TurnApiPorts from(TurnApi api) {
        return new TurnApiPorts(api);
    }

    @Override
    public List<Card> pullCards(Deck deck, int count, UserInterface ui) {
        return api.pullCards(deck, count, ui);
    }

    @Override
    public Card chooseValidCardToPutBack(Player player, List<Card> cards, UserInterface ui) {
        return api.chooseValidCardToPutBack(player, cards, ui);
    }

    @Override
    public Card draw(Player currentPlayer, Deck deck, Discard discard, UserInterface ui) {
        return api.draw(currentPlayer, deck, discard, ui);
    }

    @Override
    public Player validChosenPlayer(Player player, List<Player> players, UserInterface ui) {
        return api.validChosenPlayer(player, players, ui);
    }

    @Override
    public Player nextPlayer(Player player, List<Player> players) {
        return api.nextPlayer(player, players);
    }

    @Override
    public void damagePlayer(Player currentPlayer, List<Player> players, Player target, int damage, Player damager, Deck deck, Discard discard, UserInterface ui) {
        api.damagePlayer(currentPlayer, players, target, damage, damager, deck, discard, ui);
    }

    @Override
    public boolean checkDonePlaying() {
        return api.checkDonePlaying();
    }

    @Override
    public void setDonePlaying(boolean donePlaying) {
        api.setDonePlaying(donePlaying);
    }

    @Override
    public void play(TurnContext ctx) {
        api.play(ctx);
    }
}
