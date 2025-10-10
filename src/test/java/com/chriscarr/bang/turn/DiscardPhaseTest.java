package com.chriscarr.bang.turn;

import com.chriscarr.TestPlayerFactory;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.CardName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static com.chriscarr.TestCardFactory.card;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscardPhaseTest {

    @Test
    @Timeout(1)
    void reduces_hand_to_health_limit() {
        // Arrange
        Player p = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        p.setHealth(2);
        p.getHand().add(card(CardName.BANG));
        p.getHand().add(card(CardName.MISSED));
        p.getHand().add(card(CardName.BEER));
        p.getHand().add(card(CardName.CAT_BALOU));

        List<Player> players = List.of(p);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(p)
            .withApi(NoopTurnApi.get());

        // Act
        new DiscardPhase().carryOut(ctx);

        // Assert
        assertEquals(2, p.getHand().size(), "Hand sollte auf Health-Limit reduziert sein");
        assertEquals(2, discard.size(), "Zwei Karten sollten abgelegt sein");
    }

    @Test
    @Timeout(1)
    void seanMallory_limit_is_10() {
        // Arrange
        Player p = TestPlayerFactory.mkPlayer(Character.SEANMALLORY, 3);
        for (int i = 0; i < 12; i++) {
            p.getHand().add(card(CardName.BANG));
        }
        List<Player> players = List.of(p);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(p)
            .withApi(NoopTurnApi.get());

        // Act
        new DiscardPhase().carryOut(ctx);

        // Assert
        assertEquals(10, p.getHand().size());
        assertEquals(2, discard.size());
    }


    @Test
    @Timeout(1)
    void no_discard_when_hand_at_or_below_limit_does_not_call_ui() {
        // Arrange
        Player p = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 3);
        p.getHand().add(card(CardName.BANG));
        p.getHand().add(card(CardName.MISSED));
        p.getHand().add(card(CardName.BEER));

        List<Player> players = List.of(p);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TestUI ui = new TestUI();
        // Falls doch gefragt wird, hart failen:
        ui.askDiscardFn = pl -> {
            throw new AssertionError("askDiscard must not be called");
        };

        TurnContext ctx = TurnContext.of(deck, discard, players, ui)
            .withCurrentPlayer(p)
            .withApi(NoopTurnApi.get());

        // Act
        new DiscardPhase().carryOut(ctx);

        // Assert
        assertEquals(3, p.getHand().size());
        assertEquals(0, discard.size());
    }

    @Test
    @Timeout(1)
    void invalid_indices_then_valid_index_are_handled() {
        // Arrange
        Player p = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        p.setHealth(1);
        p.getHand().add(card(CardName.BANG));       // idx 0
        p.getHand().add(card(CardName.MISSED));     // idx 1
        p.getHand().add(card(CardName.BEER));       // idx 2

        List<Player> players = List.of(p);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TestUI ui = new TestUI();
        // Erst ungültig (-1), dann zu groß (99), dann 0, dann 0 → reduziert auf 1 Karte
        final int[] picks = {-1, 99, 0, 0};
        final int[] pos = {0};
        ui.askDiscardFn = pl -> picks[Math.min(pos[0]++, picks.length - 1)];

        TurnContext ctx = TurnContext.of(deck, discard, players, ui)
            .withCurrentPlayer(p)
            .withApi(NoopTurnApi.get());

        // Act
        new DiscardPhase().carryOut(ctx);

        // Assert
        assertEquals(1, p.getHand().size());
        assertEquals(2, discard.size());
        // Schleife muss bis zum gültigen Index weiterfragen – mindestens 3 Versuche
        assertTrue(pos[0] >= 3);
    }

    @Test
    @Timeout(1)
    void discards_specific_indices_in_order_last_then_first() {
        // Arrange
        Player p = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        p.setHealth(1);
        p.getHand().add(card(CardName.BANG));       // 0
        p.getHand().add(card(CardName.MISSED));     // 1
        p.getHand().add(card(CardName.BEER));       // 2

        List<Player> players = List.of(p);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TestUI ui = new TestUI();
        // Zuerst letztes Element (2 -> BEER), dann nach Shrink erstes (0 -> BANG)
        final int[] picks = {2, 0};
        final int[] pos = {0};
        ui.askDiscardFn = pl -> picks[Math.min(pos[0]++, picks.length - 1)];

        TurnContext ctx = TurnContext.of(deck, discard, players, ui)
            .withCurrentPlayer(p)
            .withApi(NoopTurnApi.get());

        // Act
        new DiscardPhase().carryOut(ctx);

        // Assert: Reihenfolge im Discard entspricht Auswahlreihenfolge
        assertEquals(1, p.getHand().size());
        assertEquals(2, discard.size());
        assertEquals(CardName.BEER, discard.get(0).getName());
        assertEquals(CardName.BANG, discard.get(1).getName());
    }

    @Test
    @Timeout(1)
    void seanMallory_limit_overrides_high_health_to_10() {
        // Arrange
        Player p = TestPlayerFactory.mkPlayer(Character.SEANMALLORY, 12);
        for (int i = 0; i < 13; i++) {
            p.getHand().add(card(CardName.BANG));
        }
        List<Player> players = List.of(p);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(p)
            .withApi(NoopTurnApi.get());

        // Act
        new DiscardPhase().carryOut(ctx);

        // Assert
        assertEquals(10, p.getHand().size(), "Sean Mallory darf max. 10 Karten halten");
        assertEquals(3, discard.size());
    }

}