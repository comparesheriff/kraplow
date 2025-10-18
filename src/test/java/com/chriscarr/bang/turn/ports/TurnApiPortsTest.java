package com.chriscarr.bang.turn.ports;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.turn.TurnApi;
import com.chriscarr.bang.turn.TurnContext;
import com.chriscarr.bang.userinterface.UserInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TurnApiPortsTest {

    @Test
    @Timeout(1)
    void controlDelegates_checkAndSetDone() {
        AtomicBoolean done = new AtomicBoolean(false);
        TurnApi api = TurnApi.of(
            (Deck deck, Integer count, UserInterface ui) -> List.of(),                                           // pullCardsFn
            (Player p, List<Card> cards, UserInterface ui) -> cards.isEmpty() ? null : cards.get(0),            // chooseValidCardToPutBackFn
            (Player p, List<Player> players, UserInterface ui) -> p,                                            // chooseValidChosenPlayerFn
            (Player p, List<Player> players) -> p,                                                              // nextPlayerFn
            (Player cp, Deck d, Discard dc, UserInterface ui) -> null,                                          // drawFn
            (Player cp, List<Player> ps, Player target, Integer dmg, Player damager, Deck d, Discard dc, UserInterface ui) -> {}, // damagePlayerFn
            () -> done.get(),                                                                                   // checkDonePlayingFn
            (TurnContext ctx) -> {},                                                                            // playFn
            (Boolean value) -> done.set(value)                                                                  // setDonePlayingFn
        );
        var ports = TurnApiPorts.from(api);
        assertFalse(ports.checkDonePlaying());
        ports.setDonePlaying(true);
        assertTrue(ports.checkDonePlaying());
    }

    @Test
    @Timeout(1)
    void playDelegates_toUnderlyingApi() {
        AtomicInteger counter = new AtomicInteger();
        TurnApi api = TurnApi.of(
            (Deck deck, Integer count, UserInterface ui) -> List.of(),
            (Player p, List<Card> cards, UserInterface ui) -> null,
            (Player p, List<Player> players, UserInterface ui) -> null,
            (Player p, List<Player> players) -> null,
            (Player cp, Deck d, Discard dc, UserInterface ui) -> null,
            (Player cp, List<Player> ps, Player target, Integer dmg, Player damager, Deck d, Discard dc, UserInterface ui) -> {},
            () -> false,
            (TurnContext ctx) -> counter.incrementAndGet(),
            (Boolean value) -> {}
        );
        var ports = TurnApiPorts.from(api);
        // ctx kann hier null sein; die Lambda dereferenziert ihn nicht.
        ports.play(null);
        assertEquals(1, counter.get());
    }
}
