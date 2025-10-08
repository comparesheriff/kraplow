package com.chriscarr.bang.services;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.Role;

import java.util.List;

public final class GameOverService {
    private GameOverService() {
    }

    /**
     * Checks whether the game is over. Game is over if:
     * <li>
     *     <ol>The Sheriff is dead</ol>
     *     <ol>The Renegade and all Outlaws are dead</ol>
     * </li>
     *
     * @param players list of all living players
     * @return true if game should end
     */
    public static boolean isGameOver(List<Player> players) {
        return isDead(Role.SHERIFF, players)
            || (isDead(Role.RENEGADE, players) && isDead(Role.OUTLAW, players));
    }

    /**
     * Calculate the winning Team:
     * <li>
     *     <ol>The Renegade wins if they are the last player alive</ol>
     *     <ol>The Outlaws win if the Sheriff is dead</ol>
     *     <ol>The Sheriff and the Deputies win if the Renegade and all Outlaws are dead</ol>
     * </li>
     *
     * @param players list of all living players
     * @return a String representation of the winning team
     */
    public static String getWinners(List<Player> players) {
        if (isDead(Role.DEPUTY, players)
            && isDead(Role.OUTLAW, players)
            && isDead(Role.SHERIFF, players)
            && players.size() == 1) {
            return "Renegade";
        } else if (isDead(Role.SHERIFF, players)) {
            return "Outlaws";
        } else if (isDead(Role.OUTLAW, players) && isDead(Role.RENEGADE, players)) {
            return "Sheriff and Deputies";
        } else {
            throw new RuntimeException("No Winner");
        }
    }

    /**
     * Helper Method to build a string revealing each players role
     *
     * @param players list of all players
     * @return String in the format '$CHARACTER was a $ROLE.' for each player
     */
    public static String revealRolesOnGameEnd(List<Player> players) {
        StringBuilder result = new StringBuilder();
        for (Player player : players) {
            result
                .append(player.getCharacter().getName())
                .append(" was a ")
                .append(player.getRole().getRoleName())
                .append(". ");
        }
        return result.toString();
    }

    private static boolean isDead(Role role, List<Player> players) {
        return players.stream().noneMatch(player -> player.getRole() == role);
    }
}
