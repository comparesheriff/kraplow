package com.chriscarr.bang.turn;

import com.chriscarr.bang.CardsInPlay;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Sehr einfache Fake-UI für Tests (ohne Mockito).
 * Default-Rückgaben sind deterministisch und können je Test überschrieben werden.
 */
public final class TestUI implements UserInterface {
    private static final Logger LOG = LoggerFactory.getLogger(TestUI.class);

    public Function<Player, Integer> askDiscardFn = p -> 0;
    Function<Player, Integer> askBlueDiscardFn = p -> -1;
    Function<Player, Integer> askPlayFn = p -> -1; // "passen"
    Function<Player, Integer> askPlayerFn = p -> 0;
    Function<Player, Integer> respondBangFn = p -> -1;
    Function<Player, Integer> respondMissFn = p -> -1;
    Function<Player, List<Card>> respondTwoMissFn = p -> List.of();
    Function<Player, Integer> chooseGeneralStoreCardFn = p -> 0;
    TriFunction<Player, CardsInPlay, Boolean, Integer> askOthersCardFn = (p, inPlay, hasHand) -> 0;
    Function<Player, Integer> respondBeerFn = p -> -1;
    BiFunction2<Player, Card, Boolean> chooseDiscardFn = (p, c) -> false;
    Function<Player, Boolean> chooseFromPlayerFn = p -> false;
    Function<Player, List<Card>> chooseTwoDiscardForLifeFn = p -> new ArrayList<>();
    Function<Player, List<Card>> chooseTwoDiscardForShootFn = p -> new ArrayList<>();
    Function<String, String> getRoleForNameFn = s -> null;
    Function<Player, Integer> chooseDrawCardFn = p -> 0;
    Function<Player, Integer> chooseCardToPutBackFn = p -> 0;
    String timeout = "0";

    @Override
    public int askBlueDiscard(Player player) {
        return askBlueDiscardFn.apply(player);
    }

    @Override
    public int askDiscard(Player player) {
        return askDiscardFn.apply(player);
    }

    @Override
    public int askPlay(Player player) {
        return askPlayFn.apply(player);
    }

    @Override
    public int askPlayer(Player player, List<String> otherPlayers) {
        return askPlayerFn.apply(player);
    }

    @Override
    public int respondBang(Player player) {
        return respondBangFn.apply(player);
    }

    @Override
    public int respondMiss(Player player, boolean canSingleUse) {
        return respondMissFn.apply(player);
    }

    @Override
    public List<Card> respondTwoMiss(Player player) {
        return respondTwoMissFn.apply(player);
    }

    @Override
    public int chooseGeneralStoreCard(Player player, List<Card> cards) {
        return chooseGeneralStoreCardFn.apply(player);
    }

    @Override
    public int askOthersCard(Player player, CardsInPlay cardsInPlay, boolean hasHand) {
        return askOthersCardFn.apply(player, cardsInPlay, hasHand);
    }

    @Override
    public int respondBeer(Player player) {
        return respondBeerFn.apply(player);
    }

    @Override
    public boolean chooseDiscard(Player player, Card card) {
        return chooseDiscardFn.apply(player, card);
    }

    @Override
    public boolean chooseFromPlayer(Player player) {
        return chooseFromPlayerFn.apply(player);
    }

    @Override
    public List<Card> chooseTwoDiscardForLife(Player player) {
        return chooseTwoDiscardForLifeFn.apply(player);
    }

    @Override
    public List<Card> chooseTwoDiscardForShoot(Player player) {
        return chooseTwoDiscardForShootFn.apply(player);
    }

    @Override
    public void printInfo(String info) {
        LOG.debug(info);
    }

    @Override
    public int chooseDrawCard(Player player, List<Card> cards) {
        return chooseDrawCardFn.apply(player);
    }

    @Override
    public int chooseCardToPutBack(Player player, List<Card> cards) {
        return chooseCardToPutBackFn.apply(player);
    }

    @Override
    public String getRoleForName(String name) {
        return getRoleForNameFn.apply(name);
    }

    @Override
    public String getTimeout() {
        return timeout;
    }

    /**
     * kleine Funktions-Typen, um nicht java.util.function zu verbiegen
     */
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    interface BiFunction2<A, B, R> {
        R apply(A a, B b);
    }
}