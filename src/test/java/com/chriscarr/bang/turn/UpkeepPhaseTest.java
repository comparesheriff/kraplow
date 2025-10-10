package com.chriscarr.bang.turn;

import com.chriscarr.TestPlayerFactory;
import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.chriscarr.TestCardFactory.card;
import static org.junit.jupiter.api.Assertions.*;

class UpkeepPhaseTest {

    @Test
    @Timeout(1)
    void jail_draw_non_hearts_means_stay_in_jail_and_discards_jail() {
        // Arrange
        Player p = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        p.getCardsInPlay().add(card(CardName.JAIL));

        Player q = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4);

        List<Player> players = List.of(p, q);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // TurnApi-Stubs: nur draw/nextPlayer/damage verwendet; Rest Dummy
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),                                   // pullCards
            (pl, cards, u) -> {
                throw new UnsupportedOperationException();
            }, // chooseValidCardToPutBack
            (pl, pls, u) -> pls.getFirst(),                                // validChosenPlayer
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),  // nextPlayer
            (pl, d, dis, u) -> new Card(CardName.BANG, CardSuit.SPADES, CardValue.ACE, CardType.PLAY), // draw (SPADES != HEARTS)
            (pl, pls, cur, i, dam, d, dis, u) -> {},                   // damagePlayer (no-op)
            () -> false,                                               // checkDonePlaying
            ctx -> {},                                                 // play
            done -> {}                                                 // setDonePlaying
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(p)
            .withApi(api);

        // Act
        new UpkeepPhase().carryOut(ctx);

        // Assert
        assertTrue(ctx.inJail(), "Spieler sollte im Gefängnis bleiben");
        assertEquals(1, discard.size(), "JAIL-Karte muss auf dem Ablagestapel liegen");
        assertEquals(CardName.JAIL, discard.getLast().getName());
        assertFalse(p.getCardsInPlay().hasItem(CardName.JAIL), "JAIL muss aus In-Play entfernt sein");
    }

    @Test
    @Timeout(1)
    void jail_draw_hearts_means_inJail_false_and_discards_jail() {
        // Arrange
        Player p = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        p.getCardsInPlay().add(card(CardName.JAIL));

        Player q = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4);

        List<Player> players = List.of(p, q);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // TurnApi-Stubs: nur draw/nextPlayer/damage verwendet; Rest Dummy
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),                                   // pullCards
            (pl, cards, u) -> {
                throw new UnsupportedOperationException();
            }, // chooseValidCardToPutBack
            (pl, pls, u) -> pls.getFirst(),                                // validChosenPlayer
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),  // nextPlayer
            (pl, d, dis, u) -> new Card(CardName.BANG, CardSuit.HEARTS, CardValue.ACE, CardType.PLAY), // draw (SPADES != HEARTS)
            (pl, pls, cur, i, dam, d, dis, u) -> {},                   // damagePlayer (no-op)
            () -> false,                                               // checkDonePlaying
            ctx -> {},                                                 // play
            done -> {}                                                 // setDonePlaying
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(p)
            .withApi(api);

        // Act
        new UpkeepPhase().carryOut(ctx);

        // Assert
        assertFalse(ctx.inJail(), "Spieler sollte nicht mehr im Gefängnis sein");
        assertEquals(1, discard.size(), "JAIL-Karte muss auf dem Ablagestapel liegen");
        assertEquals(CardName.JAIL, discard.getLast().getName());
        assertFalse(p.getCardsInPlay().hasItem(CardName.JAIL), "JAIL muss aus In-Play entfernt sein");
    }


    @Test
    @Timeout(1)
    void dynamite_explodes_triggers_damage_and_may_throw_game_over() {
        // Annahme: Card.isExplode(...) folgt der Standardregel (♠ 2–9). Falls abweichend,
        // bitte die gezogene Karte unten entsprechend anpassen.
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 3, Role.SHERIFF);
        sheriff.getCardsInPlay().add(card(CardName.DYNAMITE));
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);

        // mutable, da wir im Stub den Sheriff „entfernen“, um GameOver zu triggern
        List<Player> players = new java.util.ArrayList<>(List.of(sheriff, outlaw));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {throw new UnsupportedOperationException();},
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            // Ziehe eine Karte, die 'explode' auslöst (typisch ♠8)
            (pl, d, dis, u) -> new Card(CardName.BANG, CardSuit.SPADES, CardValue.EIGHT, CardType.PLAY),
            // Schaden führt letztlich zum Tod → Spieler wird entfernt (vereinfacht)
            (pl, pls, cur, dmg, damager, d, dis, u) -> pls.remove(sheriff),
            () -> true,
            ctx -> {},
            done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff)
            .withApi(api);

        assertThrows(EndOfGameException.class, () -> new UpkeepPhase().carryOut(ctx), "Explosion Sheriff stirbt → GameOver erwartet");
        // Dynamit wurde vor dem Schaden in den Discard gelegt
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.DYNAMITE),
            "DYNAMITE muss in den Ablagestapel gehen");
    }

    @Test
    @Timeout(1)
    void dynamite_passes_to_next_player_skipping_dynamite_holder() {
        // p1 hat Dynamit, p2 hat ebenfalls eines (z. B. aus einer Expansion/Setup),
        // daher muss an p3 weitergereicht werden.
        Player p1 = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        Player p2 = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4);
        Player p3 = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4);
        p1.getCardsInPlay().add(card(CardName.DYNAMITE));
        p2.getCardsInPlay().add(card(CardName.DYNAMITE)); // bewirkt „skip“

        List<Player> players = List.of(p1, p2, p3);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {throw new UnsupportedOperationException();},
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            // Ziehe eine „nicht-explosive“ Karte, damit der Pass-Zweig ausgeführt wird
            (pl, d, dis, u) -> new Card(CardName.BEER, CardSuit.HEARTS, CardValue.ACE, CardType.PLAY),
            (pl, pls, cur, dmg, damager, d, dis, u) -> {},
            () -> true,
            ctx -> {},
            done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(p1)
            .withApi(api);

        new UpkeepPhase().carryOut(ctx);

        assertFalse(p1.getCardsInPlay().hasItem(CardName.DYNAMITE),
            "p1 sollte sein Dynamit nicht mehr haben");
        assertTrue(p2.getCardsInPlay().hasItem(CardName.DYNAMITE),
            "p2 behält sein eigenes Dynamit");
        assertTrue(p3.getCardsInPlay().hasItem(CardName.DYNAMITE),
            "p3 muss das weitergereichte Dynamit erhalten");
    }


    /* =========================
       Dynamite: Damage-Aufruf
       ========================= */
    @Test
    @Timeout(1)
    void dynamite_explodes_invokes_damage_with_3() {
        // currentPlayer überlebt → kein GameOver, Jail nicht im Spiel
        Player curr = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 5, Role.SHERIFF);
        Player other = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4, Role.OUTLAW);
        curr.getCardsInPlay().add(card(CardName.DYNAMITE));
        List<Player> players = List.of(curr, other);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        AtomicInteger seenDamage = new AtomicInteger(-1);

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {throw new UnsupportedOperationException();},
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            // Ziehe eine "explosive" Karte (typisch ♠ 2..9); Wert egal
            (pl, d, dis, u) -> card(CardName.BANG, CardSuit.SPADES),
            (pl, pls, cur, dmg, damager, d, dis, u) -> {
                seenDamage.set(dmg);
                pl.removeHealth(dmg);
            },
            () -> true, ctx -> {}, done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(curr).withApi(api);

        new UpkeepPhase().carryOut(ctx);

        assertEquals(3, seenDamage.get(), "Dynamit muss 3 Schaden verursachen");
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.DYNAMITE),
            "Explodiertes Dynamit muss in den Discard wandern");
        assertEquals(2, curr.getHealth(), "Health muss um 3 reduziert sein");
    }

    /* ============================================================
       Dynamite + Jail: alle Kombinationen (Reihenfolge: Dynamite, dann Jail)
       ============================================================ */

    @Test
    @Timeout(1)
    void dynamite_explodes_and_player_stays_in_jail() {
        Player curr = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 5, Role.OUTLAW);
        Player other = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4, Role.SHERIFF);
        curr.getCardsInPlay().add(card(CardName.DYNAMITE));
        curr.getCardsInPlay().add(card(CardName.JAIL));
        List<Player> players = List.of(curr, other);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // Sequenz: 1) Dynamit-Check (explodiert), 2) Jail-Check (kein Herz → bleibt im Jail)
        ArrayDeque<Card> draws = new ArrayDeque<>(List.of(
            card(CardName.BANG, CardSuit.SPADES),   // explodiert
            card(CardName.BANG, CardSuit.SPADES)    // kein Herz -> bleibt in Jail
        ));

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {throw new UnsupportedOperationException();},
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            (pl, d, dis, u) -> draws.removeFirst(),
            (pl, pls, cur, dmg, damager, d, dis, u) -> pl.removeHealth(dmg),
            () -> true, ctx -> {}, done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(curr).withApi(api);

        new UpkeepPhase().carryOut(ctx);

        assertTrue(ctx.inJail(), "Nach Explosion & Nicht-Herz bleibt der Spieler in Jail");
        assertFalse(curr.getCardsInPlay().hasItem(CardName.JAIL), "JAIL muss abgeworfen sein");
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.DYNAMITE));
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.JAIL));
    }

    @Test
    @Timeout(1)
    void dynamite_does_not_explode_and_player_stays_in_jail() {
        Player curr = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 5);
        Player next = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4);
        curr.getCardsInPlay().add(card(CardName.DYNAMITE));
        curr.getCardsInPlay().add(card(CardName.JAIL));
        List<Player> players = List.of(curr, next);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // 1) Dynamit nicht explodiert (Herz) → wird weitergegeben an next
        // 2) Jail-Check kein Herz → bleibt im Jail
        ArrayDeque<Card> draws = new ArrayDeque<>(List.of(
            card(CardName.BANG, CardSuit.HEARTS),   // nicht explodiert
            card(CardName.BANG, CardSuit.SPADES)    // kein Herz -> bleibt in Jail
        ));

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {throw new UnsupportedOperationException();},
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            (pl, d, dis, u) -> draws.removeFirst(),
            (pl, pls, cur, dmg, damager, d, dis, u) -> {},
            () -> true, ctx -> {}, done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(curr).withApi(api);

        new UpkeepPhase().carryOut(ctx);

        assertTrue(ctx.inJail(), "Kein Herz im Jail-Check → bleibt in Jail");
        assertFalse(curr.getCardsInPlay().hasItem(CardName.DYNAMITE), "Dynamit muss weitergereicht worden sein");
        assertTrue(next.getCardsInPlay().hasItem(CardName.DYNAMITE), "Nächster Spieler muss Dynamit erhalten");
        assertFalse(curr.getCardsInPlay().hasItem(CardName.JAIL), "JAIL muss abgeworfen sein");
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.JAIL));
    }

    @Test
    @Timeout(1)
    void dynamite_explodes_and_player_dies_triggers_game_over_when_sheriff() {
        // Sheriff stirbt → sofort GameOver
        Player sheriff = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 3, Role.SHERIFF);
        Player outlaw = TestPlayerFactory.mkPlayer(Character.APACHEKID, 4, Role.OUTLAW);
        sheriff.getCardsInPlay().add(card(CardName.DYNAMITE));
        sheriff.getCardsInPlay().add(card(CardName.JAIL));
        List<Player> players = new java.util.ArrayList<>(List.of(sheriff, outlaw));
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        ArrayDeque<Card> draws = new ArrayDeque<>(List.of(
            card(CardName.BANG, CardSuit.SPADES) // explodiert
            // (Jail wird nicht mehr erreicht, da GameOver vorher wirft)
        ));

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {throw new UnsupportedOperationException();},
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            (pl, d, dis, u) -> draws.removeFirst(),
            (pl, pls, cur, dmg, damager, d, dis, u) -> {
                // 3 Schaden → Sheriff tot → aus Liste entfernen
                pl.removeHealth(dmg);
                pls.remove(pl);
            },
            () -> true, ctx -> {}, done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(sheriff).withApi(api);

        assertThrows(EndOfGameException.class, () -> new UpkeepPhase().carryOut(ctx),
            "Sheriff tot nach Explosion → GameOver");
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.DYNAMITE),
            "Dynamit muss in den Discard wandern");
    }

    @Test
    @Timeout(1)
    void dynamite_explodes_and_player_breaks_out_of_jail() {
        Player curr = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 5, Role.OUTLAW);
        Player other = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4, Role.SHERIFF);
        curr.getCardsInPlay().add(card(CardName.DYNAMITE));
        curr.getCardsInPlay().add(card(CardName.JAIL));
        List<Player> players = List.of(curr, other);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // 1) Explosion, 2) Jail-Check: Herz -> frei
        ArrayDeque<Card> draws = new ArrayDeque<>(List.of(
            card(CardName.BANG, CardSuit.SPADES),
            card(CardName.BANG, CardSuit.HEARTS)
        ));

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {throw new UnsupportedOperationException();},
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            (pl, d, dis, u) -> draws.removeFirst(),
            (pl, pls, cur, dmg, damager, d, dis, u) -> pl.removeHealth(dmg),
            () -> true, ctx -> {}, done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(curr).withApi(api);

        new UpkeepPhase().carryOut(ctx);

        assertFalse(ctx.inJail(), "Mit Herz im Jail-Check kommt der Spieler frei");
        assertFalse(curr.getCardsInPlay().hasItem(CardName.JAIL), "JAIL muss abgeworfen sein");
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.DYNAMITE));
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.JAIL));
    }

    @Test
    @Timeout(1)
    void dynamite_does_not_explode_and_player_breaks_out_of_jail() {
        Player curr = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 5);
        Player next = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4);
        curr.getCardsInPlay().add(card(CardName.DYNAMITE));
        curr.getCardsInPlay().add(card(CardName.JAIL));
        List<Player> players = List.of(curr, next);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        // 1) kein Explodieren (Herz) → Dynamit weitergeben
        // 2) Jail-Check: Herz → frei
        ArrayDeque<Card> draws = new ArrayDeque<>(List.of(
            card(CardName.BANG, CardSuit.HEARTS),
            card(CardName.BANG, CardSuit.HEARTS)
        ));

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {throw new UnsupportedOperationException();},
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            (pl, d, dis, u) -> draws.removeFirst(),
            (pl, pls, cur, dmg, damager, d, dis, u) -> {},
            () -> true, ctx -> {}, done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(curr).withApi(api);

        new UpkeepPhase().carryOut(ctx);

        assertFalse(ctx.inJail(), "Mit Herz im Jail-Check kommt der Spieler frei");
        assertFalse(curr.getCardsInPlay().hasItem(CardName.DYNAMITE), "Dynamit muss weitergereicht sein");
        assertTrue(next.getCardsInPlay().hasItem(CardName.DYNAMITE), "Nächster Spieler erhält Dynamit");
        assertFalse(curr.getCardsInPlay().hasItem(CardName.JAIL), "JAIL muss abgeworfen sein");
        assertTrue(discard.stream().anyMatch(c -> c.getName() == CardName.JAIL));
    }

    @Test
    @Timeout(1)
    void veraCuster_copies_other_players_character() {
        // Arrange
        Player vera = TestPlayerFactory.mkPlayer(Character.VERACUSTER, 4);

        Player target = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4);

        List<Player> players = List.of(vera, target);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cards, u) -> {
                throw new UnsupportedOperationException();
            },
            (pl, pls, u) -> target,     // gezielt den zweiten Spieler wählen
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            (pl, d, dis, u) -> card(CardName.BANG, CardSuit.HEARTS), // egal
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> true,  // irrelevant
            ctx -> {},
            done -> {}
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(vera)
            .withApi(api);

        // Act
        new UpkeepPhase().carryOut(ctx);

        // Assert
        assertEquals(target.getCharacter(), vera.getCharacter(),
            "Vera Custer sollte die Fähigkeiten des gewählten Spielers übernehmen");
    }
}