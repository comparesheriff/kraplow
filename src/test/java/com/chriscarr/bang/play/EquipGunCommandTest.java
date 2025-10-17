package com.chriscarr.bang.play;

import com.chriscarr.TestPlayerFactory;
import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.*;
import com.chriscarr.bang.play.impl.*;
import com.chriscarr.bang.turn.TestUI;
import com.chriscarr.bang.turn.TurnApi;
import com.chriscarr.bang.turn.TurnContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EquipGunCommandTest {

    private static TurnContext ctxForSingleIteration(Player current, List<Player> players, Deck deck, Discard discard) {
        AtomicInteger checks = new AtomicInteger(0);
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> pl, (pl, pls) -> pl,
            (pl, dsc, dis, u) -> null, (pl, pls, cur, i, dam, dsc, dis, u) -> {},
            () -> checks.getAndIncrement() > 0, // genau eine Iteration
            ctx -> {}, done -> {}
        );
        return TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(current)
            .withApi(api);
    }

    @Test
    @Timeout(1)
    void equips_gun_and_discards_old_one_if_present() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        var players = List.of(sheriff, outlaw);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // alte Gun bereits ausgerüstet
        Gun schofield = new Gun(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.FIVE, CardType.GUN);
        sheriff.setGun(schofield);
        assertTrue(sheriff.hasGun());

        // neue Gun auf der Hand
        Gun winchester = new Gun(CardName.WINCHESTER, CardSuit.HEARTS, CardValue.KING, CardType.GUN);
        sheriff.getHand().add(winchester);

        TurnContext ctx = ctxForSingleIteration(sheriff, players, deck, discard);

        // Parser, Validator, Resolver (Gun-Variante)
        PlayParser parser = new EquipParser(new FirstGunSelector());
        PlayValidator validator = new EquipGunValidator();
        PlayResolver resolver = new EquipGunResolver();

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert
        assertTrue(sheriff.hasGun());
        assertEquals(CardName.WINCHESTER, sheriff.getGunName());
        assertEquals(0, sheriff.getHand().size());
        assertEquals(1, discard.size(), "old gun must be discarded");
    }

    @Test
    @Timeout(1)
    void equips_gun_when_no_previous_gun() {
        // Arrange
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        var players = List.of(sheriff, outlaw);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        assertFalse(sheriff.hasGun());
        Gun remington = new Gun(CardName.REMINGTON, CardSuit.DIAMONDS, CardValue.QUEEN, CardType.GUN);
        sheriff.getHand().add(remington);

        TurnContext ctx = ctxForSingleIteration(sheriff, players, deck, discard);
        PlayParser parser = new EquipParser(new FirstGunSelector());
        PlayValidator validator = new EquipGunValidator();
        PlayResolver resolver = new EquipGunResolver();

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert
        assertTrue(sheriff.hasGun());
        assertEquals(CardName.REMINGTON, sheriff.getGunName());
        assertEquals(0, discard.size(), "no old gun to discard");
    }

    @Test
    @Timeout(1)
    void johnny_kisch_forces_others_with_same_gun_to_discard() {
        // Arrange
        Player johnny = TestPlayerFactory.mkPlayer(Character.JOHNNYKISCH, 4, Role.OUTLAW);
        Player p1 = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4, Role.SHERIFF);
        Player p2 = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        var players = new java.util.ArrayList<>(List.of(johnny, p1, p2));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        // p1 hat SCHOFIELD im Slot, p2 hat REMINGTON
        Gun schofield1 = new Gun(CardName.SCHOFIELD, CardSuit.HEARTS, CardValue.NINE, CardType.GUN);
        Gun remington2 = new Gun(CardName.REMINGTON, CardSuit.DIAMONDS, CardValue.QUEEN, CardType.GUN);
        p1.getCardsInPlay().setGun(schofield1);
        p2.getCardsInPlay().setGun(remington2);
        // Johnny spielt SCHOFIELD aus der Hand
        Gun johnnySchofield = new Gun(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.ACE, CardType.GUN);
        johnny.getHand().add(johnnySchofield);

        TurnContext ctx = ctxForSingleIteration(johnny, players, deck, discard);

        PlayParser parser = new EquipParser(new FirstGunSelector());
        PlayValidator validator = new EquipGunValidator();
        PlayResolver resolver = new EquipGunResolver();

        // Act
        new CommandPlayLoop(parser, resolver, validator).run(ctx);

        // Assert
        assertEquals(CardName.SCHOFIELD, johnny.getCardsInPlay().getGunName(), "Johnny should equip SCHOFIELD");
        assertFalse(p1.getCardsInPlay().hasGun(), "p1 should be forced to discard matching SCHOFIELD");
        assertTrue(p2.getCardsInPlay().hasGun(), "p2 keeps REMINGTON (different gun)");
        assertTrue(discard.contains(schofield1), "p1's gun should be in discard");
    }

}
