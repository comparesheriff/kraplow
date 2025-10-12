package com.chriscarr.bang.play.impl;

import com.chriscarr.TestPlayerFactory;
import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.play.PlayParser;
import com.chriscarr.bang.play.PlayResolver;
import com.chriscarr.bang.play.PlayValidator;
import com.chriscarr.bang.turn.TestUI;
import com.chriscarr.bang.turn.TurnApi;
import com.chriscarr.bang.turn.TurnContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CommandPlayLoopTest {

    @Test
    @Timeout(1)
    void loops_until_done_and_invokes_resolver_each_iteration() {
        //Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        List<Player> players = List.of(sheriff, outlaw);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // TurnApi done=false, dann true
        AtomicInteger checks = new AtomicInteger();
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cs, u) -> null,
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> checks.getAndIncrement() >= 2,
            ctx -> {},
            done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(api); // TurnContext API: of(...), withCurrentPlayer(...), withApi(...) verfügbar

        AtomicInteger resolves = new AtomicInteger();
        PlayParser parser = new NoopParser();          // liefert PASS
        PlayValidator validator = new NoopValidator(); // immer OK
        PlayResolver resolver = (c, cmd) -> resolves.incrementAndGet();

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert: drei Checks → zwei Schleifendurchläufe → zwei Resolves
        assertEquals(2, resolves.get());
    }

    @Test
    @Timeout(1)
    void stops_when_current_player_removed_during_iteration() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw1 = TestPlayerFactory.mkPlayer(Character.BELLESTAR, 4, Role.OUTLAW);
        Player outlaw2 = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        List<Player> players = new java.util.ArrayList<>(List.of(sheriff, outlaw1, outlaw2));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, d, dis, u) -> null, (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false, // niemals "done" → Abbruch muss über players.contains(...) kommen
            ctx -> {}, done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(outlaw1)
            .withApi(api);

        AtomicInteger resolves = new AtomicInteger();
        PlayParser parser = new NoopParser();
        PlayValidator validator = new NoopValidator();
        PlayResolver resolver = (c, cmd) -> {
            resolves.incrementAndGet();
            // simuliert Effekt von play(): currentPlayer wird aus der Liste entfernt
            c.players().remove(c.currentPlayer());
        };

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert: genau ein Durchlauf
        assertDoesNotThrow(() -> new CommandPlayLoop(parser, resolver, validator).run(ctx));
        assertEquals(1, resolves.get());
    }

    @Test
    @Timeout(1)
    void throws_EndOfGameException_when_sheriff_dies() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        List<Player> players = new java.util.ArrayList<>(List.of(sheriff, outlaw));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, d, dis, u) -> null, (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false,
            ctx -> {}, done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(outlaw) // Sheriff muss in der Liste sein, current egal
            .withApi(api);

        PlayParser parser = new NoopParser();
        PlayValidator validator = new NoopValidator();
        PlayResolver resolver = (c, cmd) -> c.players().remove(sheriff); // Sheriff „stirbt“

        // Act  Assert (identische GameOver-Policy wie MainPhase)
        assertThrows(EndOfGameException.class, () ->
            new CommandPlayLoop(parser, resolver, validator).run(ctx)
        );
    }
}