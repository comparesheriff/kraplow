package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.CardType;
import com.chriscarr.bang.turn.TestUI;
import com.chriscarr.bang.turn.TurnApi;
import com.chriscarr.bang.turn.TurnContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurnApiSelectorTest {

    @Test
    @Timeout(1)
    void selects_item_when_ui_points_to_hand_item() {
        Player p = new Player();
        p.setCharacter(Character.APACHEKID);
        p.setHand(new Hand());
        p.setInPlay(new CardsInPlay());
        // Hand[0] = MUSTANG (ITEM), Hand[1] = BANG (PLAY)
        Card mustang = new Card();
        mustang.setName(CardName.MUSTANG);
        mustang.setType(CardType.ITEM);
        Card bang = new Card();
        bang.setName(CardName.BANG);
        bang.setType(CardType.PLAY);
        p.getHand().add(mustang);
        p.getHand().add(bang);

        TestUI ui = new TestUI();
        ui.askPlayFn = (_) -> 0;
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TurnApi turnApi = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cs, u) -> null,
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false,
            c -> {},
            b -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), ui).withCurrentPlayer(p).withApi(turnApi);
        int idx = new TurnApiSelector().chooseEquipFromHand(p, ctx);
        assertEquals(0, idx);
    }

    @Test
    @Timeout(1)
    void returns_minus1_when_ui_points_to_non_item_in_hand() {
        Player p = new Player();
        p.setCharacter(Character.APACHEKID);
        p.setHand(new Hand());
        p.setInPlay(new CardsInPlay());
        // Hand[0] = BANG (kein Item)
        Card bang = new Card();
        bang.setName(CardName.BANG);
        bang.setType(CardType.PLAY);
        p.getHand().add(bang);

        TestUI ui = new TestUI();
        ui.askPlayFn = (_) -> 0;
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TurnApi turnApi = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cs, u) -> null,
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false,
            c -> {},
            b -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), ui).withCurrentPlayer(p).withApi(turnApi);
        int idx = new TurnApiSelector().chooseEquipFromHand(p, ctx);
        assertEquals(-1, idx);
    }

    @Test
    @Timeout(1)
    void returns_minus1_when_ui_points_to_inplay_or_special() {
        Player p = new Player();
        p.setCharacter(Character.APACHEKID);
        p.setHand(new Hand());
        p.setInPlay(new CardsInPlay());
        // Hand[0] = MUSTANG
        Card mustang = new Card();
        mustang.setName(CardName.MUSTANG);
        mustang.setType(CardType.ITEM);
        p.getHand().add(mustang);

        TestUI ui = new TestUI();
        ui.askPlayFn = (_) -> 1;
        Deck deck = new Deck();
        Discard discard = new Discard();
        deck.setDiscard(discard);
        TurnApi turnApi = TurnApi.of(
            (d, c, u) -> List.of(),
            (pl, cs, u) -> null,
            (pl, pls, u) -> pl,
            (pl, pls) -> pl,
            (pl, d, dis, u) -> null,
            (pl, pls, cur, i, dam, d, dis, u) -> {},
            () -> false,
            c -> {},
            b -> {}
        );
        TurnContext ctx = TurnContext.of(deck, discard, List.of(p), ui).withCurrentPlayer(p).withApi(turnApi);
        int idx = new TurnApiSelector().chooseEquipFromHand(p, ctx);
        assertEquals(-1, idx);
    }

}