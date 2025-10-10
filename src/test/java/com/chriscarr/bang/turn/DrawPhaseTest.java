package com.chriscarr.bang.turn;

import com.chriscarr.TestPlayerFactory;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.CardSuit;
import com.chriscarr.bang.userinterface.UserInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static com.chriscarr.TestCardFactory.card;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DrawPhaseTest {

    @Test
    @Timeout(1)
    void clausTheSaint_distributes_to_other_players_then_keeps_rest() {
        // Arrange
        Player p1 = TestPlayerFactory.mkPlayer(Character.CLAUSTHESAINT, 4);
        Player p2 = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        Player p3 = TestPlayerFactory.mkPlayer(Character.JOURDONNAIS, 4);
        List<Player> players = List.of(p1, p2, p3);

        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        UserInterface ui = new TestUI();

        // pullCards liefert players.size()+1 = 4 Karten
        List<Card> pulled = List.of(card(CardName.BANG), card(CardName.MISSED), card(CardName.BEER), card(CardName.CAT_BALOU));

        TurnApi api = TurnApi.of(
            (d, cnt, u) -> new ArrayList<>(pulled),        // pullCards (kopiert, da DrawPhase die Liste mutiert)
            (pl, cards, u) -> cards.getFirst(),                // "wähle" jeweils die erste Karte zum Weitergeben
            (pl, pls, u) -> pls.getFirst(),
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()), // next
            (pl, d, dis, u) -> card(CardName.BANG),         // nicht genutzt in diesem Pfad
            (pl, pls, cur, i, dam, d, dis, u) -> {},        // damagePlayer
            () -> true,
            ctx -> {},
            done -> {}               // Main-Phase irrelevant
        );

        TurnContext ctx = TurnContext.of(deck, discard, players, ui).withCurrentPlayer(p1).withApi(api);

        // Act
        new DrawPhase().carryOut(ctx);

        // Assert: p2 und p3 bekommen je 1 Karte, p1 behält den Rest (2)
        assertEquals(1, p2.getHand().size(), "p2 bekommt 1 Karte");
        assertEquals(CardName.BANG, p2.getHand().getFirst().getName(), "p2 genommene Karte sollte SHOOT sein");
        assertEquals(1, p3.getHand().size(), "p3 bekommt 1 Karte");
        assertEquals(CardName.MISSED, p3.getHand().getFirst().getName(), "p3 genommene Karte sollte MISSED sein");
        assertEquals(2, p1.getHand().size(), "p1 bekommt 2 Karten");
        assertEquals(CardName.BEER, p1.getHand().getFirst().getName(), "p1 genommene Karte sollte BEER sein");
        assertEquals(CardName.CAT_BALOU, p1.getHand().getLast().getName(), "p1 genommene Karte sollte CAT BALOU sein");

    }

    @Test
    @Timeout(1)
    void normal_draw_two_cards_no_ability() {
        Player p = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        // removeLast()-Reihenfolge: letzte hinzugefügte Karte wird zuerst gezogen
        deck.add(card(CardName.BANG));   // A (dritte)
        deck.add(card(CardName.MISSED)); // B (zweite)
        deck.add(card(CardName.BEER));   // C (erste gezogen)
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), new TestUI())
            .withCurrentPlayer(p).withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(2, p.getHand().size());
        assertEquals(CardName.BEER, p.getHand().getFirst().getName(), "p1 genommene Karte sollte BEER sein");
        assertEquals(CardName.MISSED, p.getHand().getLast().getName(), "p1 genommene Karte sollte MISSED sein");
    }

    @Test
    @Timeout(1)
    void blackjack_draws_third_on_red_second_card() {
        Player p = TestPlayerFactory.mkPlayer(Character.BLACKJACK, 4);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        // Ziehfolge: C -> B -> (dritte, weil B rot) -> A
        deck.add(card(CardName.BANG, CardSuit.SPADES));    // A
        deck.add(card(CardName.BEER, CardSuit.HEARTS));    // B (rot)
        deck.add(card(CardName.MISSED, CardSuit.CLUBS));   // C
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), new TestUI())
            .withCurrentPlayer(p).withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(3, p.getHand().size());
        assertEquals(CardName.MISSED, p.getHand().getFirst().getName(), "p1 genommene Karte sollte MISSED sein");
        assertEquals(CardName.BEER, p.getHand().get(1).getName(), "p1 genommene Karte sollte BEER sein");
        assertEquals(CardName.BANG, p.getHand().getLast().getName(), "p1 genommene Karte sollte BANG sein");
    }

    @Test
    @Timeout(1)
    void blackjack_does_not_draw_third_on_black_second_card() {
        Player p = TestPlayerFactory.mkPlayer(Character.BLACKJACK, 4);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        deck.add(card(CardName.BANG, CardSuit.SPADES));    // A
        deck.add(card(CardName.BEER, CardSuit.SPADES));    // B (schwarz)
        deck.add(card(CardName.MISSED, CardSuit.CLUBS));   // C
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), new TestUI())
            .withCurrentPlayer(p).withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(2, p.getHand().size());
        assertEquals(CardName.MISSED, p.getHand().getFirst().getName(), "p genommene Karte sollte MISSED sein");
        assertEquals(CardName.BEER, p.getHand().getLast().getName(), "p genommene Karte sollte BEER sein");
    }

    @Test
    @Timeout(1)
    void pixiePete_draws_three() {
        Player p = TestPlayerFactory.mkPlayer(Character.PIXIEPETE, 4);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        deck.add(card(CardName.BANG));
        deck.add(card(CardName.MISSED));
        deck.add(card(CardName.BEER));
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), new TestUI())
            .withCurrentPlayer(p).withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(3, p.getHand().size());
        assertEquals(CardName.BEER, p.getHand().getFirst().getName(), "p genommene Karte sollte BEER sein");
        assertEquals(CardName.MISSED, p.getHand().get(1).getName(), "p genommene Karte sollte MISSED sein");
        assertEquals(CardName.BANG, p.getHand().getLast().getName(), "p genommene Karte sollte BANG sein");
    }

    @Test
    @Timeout(1)
    void billNoFace_draws_1_plus_missing_health() {
        Player p = TestPlayerFactory.mkPlayer(Character.BILLNOFACE, 5);
        p.setHealth(3); // fehlt 2 Leben → 1 + 2 = 3 Karten
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        deck.add(card(CardName.BANG));
        deck.add(card(CardName.MISSED));
        deck.add(card(CardName.BEER));
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), new TestUI())
            .withCurrentPlayer(p).withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(3, p.getHand().size());
        assertEquals(CardName.BEER, p.getHand().getFirst().getName(), "p genommene Karte sollte BEER sein");
        assertEquals(CardName.MISSED, p.getHand().get(1).getName(), "p genommene Karte sollte MISSED sein");
        assertEquals(CardName.BANG, p.getHand().getLast().getName(), "p genommene Karte sollte BANG sein");
    }

    @Test
    @Timeout(1)
    void pedroRamirez_prefers_discard_when_chosen() {
        Player p = TestPlayerFactory.mkPlayer(Character.PEDRORAMIREZ, 4);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        discard.add(card(CardName.CAT_BALOU)); // oberste Ablage
        deck.add(card(CardName.BANG));         // zweite Karte vom Deck
        // UI: wählt die Ablage
        TestUI ui = new TestUI();
        ui.chooseDiscardFn = (_, _) -> true;

        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), ui)
            .withCurrentPlayer(p).withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(2, p.getHand().size(), "1x Ablage + 1x Deck");
        assertEquals(0, discard.size(), "Ablage wurde aufgenommen");
        assertEquals(CardName.CAT_BALOU, p.getHand().getFirst().getName(), "p genommene Karte sollte CAT_BALOU sein");
        assertEquals(CardName.BANG, p.getHand().getLast().getName(), "p genommene Karte sollte BANG sein");
    }

    @Test
    @Timeout(1)
    void pedroRamirez_draws_from_deck_if_declined_or_empty_discard() {
        Player p = TestPlayerFactory.mkPlayer(Character.PEDRORAMIREZ, 4);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        discard.add(card(CardName.CAT_BALOU));           // bleibt liegen, da UI false
        deck.add(card(CardName.BANG));
        deck.add(card(CardName.MISSED)); // zwei vom Deck
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), new TestUI())
            .withCurrentPlayer(p).withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(2, p.getHand().size(), "2x Deck");
        assertEquals(1, discard.size(), "Ablage unverändert");
        assertEquals(CardName.MISSED, p.getHand().getFirst().getName(), "p genommene Karte sollte MISSED sein");
        assertEquals(CardName.BANG, p.getHand().getLast().getName(), "p genommene Karte sollte BANG sein");
    }

    @Test
    @Timeout(1)
    void jesseJones_from_player_then_from_deck() {
        Player j = TestPlayerFactory.mkPlayer(Character.JESSEJONES, 4);
        Player o = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        o.getHand().add(card(CardName.BEER)); // genau 1 Karte → removeRandom deterministisch
        List<Player> players = List.of(j, o);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        deck.add(card(CardName.MISSED)); // zweite Karte
        TestUI ui = new TestUI();
        ui.chooseFromPlayerFn = player -> true;
        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> o,                   // validChosenPlayer → Gegner
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> true,
            ctx -> {},
            done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, ui).withCurrentPlayer(j).withApi(api);

        new DrawPhase().carryOut(ctx);
        assertEquals(2, j.getHand().size(), "1 von Spieler + 1 vom Deck");
        assertEquals(0, o.getHand().size(), "Gegner verliert die eine Handkarte");
        assertEquals(CardName.BEER, j.getHand().getFirst().getName(), "Erste genommene Karte sollte Beer sein");
        assertEquals(CardName.MISSED, j.getHand().getLast().getName(), "Zweite genommene Karte sollte Missed sein");
    }

    @Test
    @Timeout(1)
    void jesseJones_no_other_hands_falls_back_to_deck() {
        Player j = TestPlayerFactory.mkPlayer(Character.JESSEJONES, 4);
        Player o = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4); // leere Hand
        List<Player> players = List.of(j, o);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        deck.add(card(CardName.BANG));
        deck.add(card(CardName.MISSED));
        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(j).withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(2, j.getHand().size(), "2x Deck, weil keine fremden Handkarten verfügbar");
        assertEquals(CardName.MISSED, j.getHand().getFirst().getName(), "Erste genommene Karte sollte Missed sein");
        assertEquals(CardName.BANG, j.getHand().getLast().getName(), "Zweite genommene Karte sollte Bang sein");
    }

    @Test
    @Timeout(1)
    void jesseJones_declines_from_player_even_if_available_draws_from_deck() {
        Player j = TestPlayerFactory.mkPlayer(Character.JESSEJONES, 4);
        Player o = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        o.getHand().add(card(CardName.CAT_BALOU)); // Gegner hat Handkarte
        List<Player> players = List.of(j, o);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        deck.add(card(CardName.BANG));
        deck.add(card(CardName.MISSED));

        TurnContext ctx = TurnContext.of(deck, discard, players, new TestUI())
            .withCurrentPlayer(j)
            .withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(2, j.getHand().size(), "2 Karten vom Deck");
        assertEquals(1, o.getHand().size(), "Gegner-Hand bleibt unverändert");
        assertEquals(CardName.MISSED, j.getHand().getFirst().getName());
        assertEquals(CardName.BANG, j.getHand().getLast().getName());
    }

    @Test
    @Timeout(1)
    void patBrennan_takes_in_play_card_when_chosen() {
        Player pb = TestPlayerFactory.mkPlayer(Character.PATBRENNAN, 4);
        Player other = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4);
        other.getCardsInPlay().add(card(CardName.CAT_BALOU)); // irgendeine In-Play-Karte
        List<Player> players = List.of(pb, other);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        // UI: wählt „von Spieler“ und als Index 0 aus dessen In-Play
        TestUI ui = new TestUI();
        ui.chooseFromPlayerFn = player -> true;

        TurnApi api = TurnApi.of(
            (d, c, u) -> List.of(), (pl, cs, u) -> null,
            (pl, pls, u) -> other,                // validChosenPlayer → other
            (pl, pls) -> pls.get((pls.indexOf(pl) + 1) % pls.size()),
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> true,
            ctx -> {},
            done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, players, ui).withCurrentPlayer(pb).withApi(api);

        new DrawPhase().carryOut(ctx);
        assertEquals(1, pb.getHand().size(), "Pat nimmt genau 1 Karte");
        assertTrue(other.getCardsInPlay().isEmpty(), "Andere verliert die In-Play-Karte");
        assertEquals(CardName.CAT_BALOU, pb.getHand().getFirst().getName(), "p genommene Karte sollte CAT_BALOU sein");
    }

    @Test
    @Timeout(1)
    void patBrennan_draws_two_from_deck_when_no_targets_have_in_play() {
        Player pb = TestPlayerFactory.mkPlayer(Character.PATBRENNAN, 4);
        Player other = TestPlayerFactory.mkPlayer(Character.BARTCASSIDY, 4); // keine In-Play-Karten, kein Gun
        List<Player> players = List.of(pb, other);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        deck.add(card(CardName.BANG));     // wird als zweites gezogen
        deck.add(card(CardName.MISSED));   // wird als erstes gezogen
        TestUI ui = new TestUI();
        ui.chooseFromPlayerFn = p -> true; // Spieler versucht, von Gegner zu nehmen

        TurnContext ctx = TurnContext.of(deck, discard, players, ui)
            .withCurrentPlayer(pb)
            .withApi(NoopTurnApi.get());

        new DrawPhase().carryOut(ctx);
        assertEquals(2, pb.getHand().size(), "Fallback: 2 Karten vom Deck");
        assertEquals(CardName.MISSED, pb.getHand().getFirst().getName());
        assertEquals(CardName.BANG, pb.getHand().getLast().getName());
    }

    @Test
    @Timeout(1)
    void kitCarlson_puts_one_back_and_keeps_two() {
        Player kc = TestPlayerFactory.mkPlayer(Character.KITCARLSON, 4);
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        var pulled = List.of(card(CardName.BANG), card(CardName.MISSED), card(CardName.BEER));
        UserInterface ui = new TestUI();
        // PullCards liefert 3, chooseValidCardToPutBack wählt das erste
        TurnApi api = TurnApi.of(
            (d, c, u) -> new ArrayList<>(pulled),
            (pl, cs, u) -> cs.getFirst(),            // diese Karte kommt zurück aufs Deck
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null, (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> true,
            ctx -> {},
            done -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, List.of(kc), ui).withCurrentPlayer(kc).withApi(api);

        new DrawPhase().carryOut(ctx);
        assertEquals(2, kc.getHand().size(), "Zwei Karten behalten");
        assertEquals(1, deck.size(), "Eine Karte aufs Deck zurückgelegt");
        assertEquals(CardName.MISSED, kc.getHand().getFirst().getName(), "p genommene Karte sollte MISSED sein");
        assertEquals(CardName.BEER, kc.getHand().getLast().getName(), "p genommene Karte sollte BEER sein");
        assertEquals(CardName.BANG, deck.pull().getName(), "deck genommene Karte sollte BANG sein");
    }

}
