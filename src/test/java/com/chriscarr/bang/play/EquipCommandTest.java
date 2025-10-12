package com.chriscarr.bang.play;

import com.chriscarr.TestPlayerFactory;
import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.*;
import com.chriscarr.bang.play.impl.CommandPlayLoop;
import com.chriscarr.bang.play.impl.EquipParser;
import com.chriscarr.bang.play.impl.EquipResolver;
import com.chriscarr.bang.play.impl.EquipValidator;
import com.chriscarr.bang.turn.TestUI;
import com.chriscarr.bang.turn.TurnApi;
import com.chriscarr.bang.turn.TurnContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EquipCommandTest {
    @Test
    @Timeout(1)
    void equip_item_moves_from_hand_to_inplay() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        var players = new java.util.ArrayList<>(List.of(sheriff, outlaw));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        // Mustang-Karte minimal (Name/Typ reicht für In-Play)
        Card mustang = new Card();
        mustang.setName(CardName.MUSTANG);
        mustang.setType(CardType.ITEM);
        sheriff.getHand().add(mustang);

        AtomicInteger checks = new AtomicInteger(0);
        // genau eine Iteration: first false, then true
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, dsc, dis, u) -> null, (pl, pls, cur, i, dam, dsc, dis, u) -> {},
            () -> checks.getAndIncrement() > 0,
            ctx -> {}, done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(api);

        PlayParser parser = new EquipParser();
        PlayValidator validator = new EquipValidator();
        PlayResolver resolver = new EquipResolver();

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert
        assertTrue(sheriff.isInPlay(CardName.MUSTANG));
        assertEquals(0, sheriff.getHand().size());
    }

    @Test
    @Timeout(1)
    void validator_rejects_when_item_already_in_play() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        var players = new java.util.ArrayList<>(List.of(sheriff, outlaw));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        // bereits in Play
        Card mustangInPlay = new Card();
        mustangInPlay.setName(CardName.MUSTANG);
        mustangInPlay.setType(CardType.ITEM);
        mustangInPlay.setSuit(CardSuit.CLUBS);
        mustangInPlay.setValue(CardValue.QUEEN);
        sheriff.addInPlay(mustangInPlay);
        // weiterer Mustang in Hand
        Card mustangInHand = new Card();
        mustangInHand.setName(CardName.MUSTANG);
        mustangInHand.setType(CardType.ITEM);
        mustangInPlay.setSuit(CardSuit.CLUBS);
        mustangInPlay.setValue(CardValue.QUEEN);
        sheriff.getHand().add(mustangInHand);

        AtomicInteger checks = new AtomicInteger(0);
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, dsc, dis, u) -> null, (pl, pls, cur, i, dam, dsc, dis, u) -> {},
            () -> checks.getAndIncrement() > 0, // eine Iteration
            ctx -> {}, done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(api);

        PlayParser parser = new EquipParser();
        PlayValidator validator = new EquipValidator();
        // Resolver zählt, um sicherzugehen, dass er NICHT aufgerufen wird
        final int[] resolves = {0};
        PlayResolver resolver = (c, cmd) -> resolves[0]++;

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert: nichts verändert, Resolver nie aufgerufen
        assertEquals(1, sheriff.getGameStateInPlay().size());
        assertEquals(1, sheriff.getHandSize());
        assertEquals(0, resolves[0]);
    }

    @Test
    @Timeout(1)
    void parser_returns_pass_if_no_item_in_hand() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        var players = new java.util.ArrayList<>(List.of(sheriff, outlaw));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        // Hand leer oder ohne Items
        AtomicInteger checks = new AtomicInteger(0);
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, dsc, dis, u) -> null, (pl, pls, cur, i, dam, dsc, dis, u) -> {},
            () -> checks.getAndIncrement() > 0, // eine Iteration
            ctx -> {}, done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(api);

        PlayParser parser = new EquipParser();
        PlayValidator validator = new EquipValidator();
        final int[] resolves = {0};
        PlayResolver resolver = (c, cmd) -> resolves[0]++;

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert: PASS → Resolver nicht aufgerufen, keine Änderungen
        assertEquals(0, sheriff.getGameStateInPlay().size());
        assertEquals(0, resolves[0]);
    }
}
