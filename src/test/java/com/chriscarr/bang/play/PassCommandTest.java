package com.chriscarr.bang.play;

import com.chriscarr.TestPlayerFactory;
import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.play.impl.CommandPlayLoop;
import com.chriscarr.bang.play.impl.NoopValidator;
import com.chriscarr.bang.play.impl.PassOnlyParser;
import com.chriscarr.bang.play.impl.PassResolver;
import com.chriscarr.bang.turn.TestUI;
import com.chriscarr.bang.turn.TurnApi;
import com.chriscarr.bang.turn.TurnContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassCommandTest {
    @Test
    @Timeout(1)
    void pass_sets_done_and_stops_after_one_iteration() {
        // Players/Stacks (minimal)
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        List<Player> players = List.of(sheriff, outlaw);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // done-Flag an TurnApi binden
        AtomicBoolean done = new AtomicBoolean(false);
        AtomicInteger playCalls = new AtomicInteger(0);
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, d, dis, u) -> null, (pl, pls, cur, i, dam, d, dis, u) -> {},
            done::get,                             // checkDonePlaying()
            ctx -> playCalls.incrementAndGet(),    // play(ctx) (wird hier nicht genutzt)
            done::set                              // setDonePlaying(boolean)
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(api);

        PlayParser parser = new PassOnlyParser();
        PlayValidator validator = new NoopValidator();
        PlayResolver resolver = new PassResolver();

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert: PASS wurde genau einmal resolved und hat done=true gesetzt
        assertTrue(done.get(), "PASS must set done=true");
        assertEquals(0, playCalls.get(), "TurnApi.play(ctx) is not used in command loop");
    }
}
