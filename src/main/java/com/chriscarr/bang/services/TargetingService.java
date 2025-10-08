package com.chriscarr.bang.services;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.cards.CardName;

import java.util.ArrayList;
import java.util.List;

public final class TargetingService {
    private TargetingService() {
    }

    public static List<Player> others(Player player, List<Player> others) {
        List<Player> othersCopy = new ArrayList<>(others);
        othersCopy.remove(player);
        return othersCopy;
    }

    public static List<Player> getPlayersWithinRange(Player player, List<Player> players) {
        List<Player> others = new ArrayList<>();
        Player cancelPlayer = new CancelPlayer();
        cancelPlayer.setHand(new Hand());
        cancelPlayer.setInPlay(new CardsInPlay());
        others.add(cancelPlayer);
        int range = player.getGunRange();
        for (Player otherPlayer : players) {
            int distance =
                AlivePlayers.getDistance(
                    players.indexOf(player), players.indexOf(otherPlayer), players.size());
            if (otherPlayer.getCardsInPlay().hasItem(CardName.MUSTANG)) {
                if (!Character.BELLESTAR.equals(player.getCharacter())) {
                    distance = distance + 1;
                }
            }
            if (otherPlayer.getCardsInPlay().hasItem(CardName.HIDEOUT)) {
                if (!Character.BELLESTAR.equals(player.getCharacter())) {
                    distance = distance + 1;
                }
            }
            if (Character.PAULREGRET.equals(otherPlayer.getCharacter())) {
                distance = distance + 1;
            }
            if (player.getCardsInPlay().hasItem(CardName.SCOPE)) {
                distance = distance - 1;
            }
            if (player.getCardsInPlay().hasItem(CardName.SILVER)) {
                distance = distance - 1;
            }
            if (Character.ROSEDOOLAN.equals(player.getCharacter())) {
                distance = distance - 1;
            }
            if (distance <= range) {
                others.add(otherPlayer);
            }
        }
        return others(player, others);
    }
}
