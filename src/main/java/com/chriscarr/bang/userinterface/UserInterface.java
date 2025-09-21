package com.chriscarr.bang.userinterface;

import com.chriscarr.bang.InPlay;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;

import java.util.List;

public interface UserInterface {

    int askBlueDiscard(Player player);

    int askDiscard(Player player);

    int askPlay(Player player);

    int askPlayer(Player player, List<String> otherPlayers);

    int respondBang(Player player);

    int respondMiss(Player player, boolean canSingleUse);

    List<Card> respondTwoMiss(Player player);

    int chooseGeneralStoreCard(Player player,
                               List<Card> cards);

    int askOthersCard(Player player, InPlay inPlay, boolean hasHand);

    int respondBeer(Player player);

    boolean chooseDiscard(Player player, Card card);

    boolean chooseFromPlayer(Player player);

    List<Card> chooseTwoDiscardForLife(Player player);

    List<Card> chooseTwoDiscardForShoot(Player player);

    void printInfo(String info);

    int chooseDrawCard(Player player, List<Card> cards);

    int chooseCardToPutBack(Player player, List<Card> cards);

    String getRoleForName(String name);

    String getTimeout();
}
