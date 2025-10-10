package com.chriscarr.bang.turn;

import com.chriscarr.TestPlayerFactory;
import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainPhaseTest {

    @Test
    @Timeout(1)
    void loops_until_checkDonePlaying_becomes_true_and_invokes_play_each_iteration() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player deputy = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4, Role.DEPUTY);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        List<Player> players = List.of(sheriff, deputy, outlaw);

        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        AtomicInteger plays = new AtomicInteger();

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(getTurnApi(plays));

        // Act
        new MainPhase().carryOut(ctx);

        // Assert: play() wurde exakt 2x aufgerufen
        assertEquals(2, plays.get());
    }


    @Test
    @Timeout(1)
    void does_not_call_play_when_done_true_initially() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        List<Player> players = List.of(sheriff);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        AtomicInteger plays = new AtomicInteger();
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cs, u) -> null,
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> true,                 // schon beim Start "fertig"
            ctx -> plays.incrementAndGet(),
            done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(api);

        // Act
        new MainPhase().carryOut(ctx);

        // Assert
        assertEquals(0, plays.get(), "play() darf nicht aufgerufen werden, wenn done=true zu Beginn");
    }


    @Test
    @Timeout(1)
    void stops_when_current_player_removed_during_play_without_game_over() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player deputy = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4, Role.DEPUTY);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        Player renegade = TestPlayerFactory.mkPlayer(Character.CALAMITYJANET, 4, Role.RENEGADE);
        // mutable Liste, weil wir mutieren
        List<Player> players = new java.util.ArrayList<>(List.of(sheriff, deputy, outlaw, renegade));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        AtomicInteger plays = new AtomicInteger();
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cs, u) -> null,
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false,                // erzwinge Eintritt in die Schleife
            ctx -> {
                plays.incrementAndGet();
                ctx.players().remove(ctx.currentPlayer());
            },
            done -> {}
        );
        // currentPlayer ist NICHT der Sheriff, damit kein GameOver
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(deputy)
            .withApi(api);

        // Act
        new MainPhase().carryOut(ctx);

        // Assert
        assertEquals(1, plays.get(), "Schleife muss enden, sobald currentPlayer aus players entfernt wurde (ohne GameOver)");
    }

    @Test
    @Timeout(1)
    void play_receives_same_context_instance() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        List<Player> players = List.of(sheriff, outlaw);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        AtomicReference<TurnContext> seenCtx = new AtomicReference<>();
        AtomicInteger plays = new AtomicInteger();
        AtomicInteger checks = new AtomicInteger();
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cs, u) -> null,
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> checks.getAndIncrement() >= 1, // false, dann true
            ctx -> {
                plays.incrementAndGet();
                seenCtx.set(ctx);
            },
            done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(api);

        // Act
        new MainPhase().carryOut(ctx);

        // Assert
        assertEquals(1, plays.get(), "genau ein Aufruf");
        // identische Instanz?
        assertEquals(System.identityHashCode(ctx), System.identityHashCode(seenCtx.get()), "play() muss das identische ctx erhalten");
    }

    @Test
    @Timeout(1)
    void does_not_run_if_current_player_not_in_players() {
        // Arrange: currentPlayer absichtlich NICHT in players
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player deputy = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4, Role.DEPUTY);
        List<Player> players = List.of(deputy); // sheriff fehlt
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        AtomicInteger plays = new AtomicInteger();
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cs, u) -> null,
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false,
            ctx -> plays.incrementAndGet(),
            done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff) // nicht in players
            .withApi(api);

        // Act
        new MainPhase().carryOut(ctx);

        // Assert
        assertEquals(0, plays.get(), "Wenn currentPlayer nicht in players ist, darf play() nicht laufen");
    }

    @Test
    @Timeout(1)
    void throws_EndOfGameException_when_sheriff_dies() {
        // Arrange: Sheriff wird in play() „getötet“ (aus Liste entfernt)
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        Player renegade = TestPlayerFactory.mkPlayer(Character.CALAMITYJANET, 4, Role.RENEGADE);
        List<Player> players = new java.util.ArrayList<>(List.of(sheriff, outlaw, renegade));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, d, dis, u) -> null, (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false,
            ctx -> ctx.players().remove(sheriff), // Sheriff „stirbt“
            done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(outlaw) // egal, Hauptsache Sheriff ist in der Liste
            .withApi(api);

        // Act + Assert
        assertThrows(EndOfGameException.class, () -> new MainPhase().carryOut(ctx),
            "Wenn der Sheriff tot ist, muss MainPhase EndOfGameException werfen");
    }

    @Test
    @Timeout(1)
    void throws_EndOfGameException_when_outlaw_and_renegade_dead() {
        // Arrange: Outlaw & Renegade werden in play() entfernt, Sheriff lebt
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player deputy = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4, Role.DEPUTY);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        Player renegade = TestPlayerFactory.mkPlayer(Character.CALAMITYJANET, 4, Role.RENEGADE);
        List<Player> players = new java.util.ArrayList<>(List.of(sheriff, deputy, outlaw, renegade));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, d, dis, u) -> null, (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false,
            ctx -> ctx.players().removeIf(p -> p.getRole() == Role.OUTLAW || p.getRole() == Role.RENEGADE),
            done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(deputy)
            .withApi(api);

        // Act + Assert
        assertThrows(EndOfGameException.class, () -> new MainPhase().carryOut(ctx),
            "Wenn Outlaw und Renegade tot sind, muss MainPhase EndOfGameException werfen");
    }

    private static TurnApi getTurnApi(AtomicInteger plays) {
        AtomicInteger checks = new AtomicInteger();

        return TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {
                throw new UnsupportedOperationException();
            },
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.getFirst(),
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> checks.getAndIncrement() >= 2,      // false, false, dann true
            ctx -> plays.incrementAndGet(),
            done -> {}
        );
    }
}