package com.chriscarr.game;

import java.util.ArrayList;
import java.util.List;

public class GamePrep {
    private final List<String> joinedPlayers;
    private Long lastUpdated;
    private final String visibility;
    private final boolean sidestep;
    private final int maxPlayers;

    public GamePrep(String visibility, boolean sidestep) {
        this.visibility = visibility;
        this.sidestep = sidestep;
        maxPlayers = sidestep ? 8 : 7;
        joinedPlayers = new ArrayList<>();
        lastUpdated = System.currentTimeMillis();
    }

    public boolean isSideStep() {
        return this.sidestep;
    }

    public boolean canJoin() {
        if (joinedPlayers.size() < maxPlayers) {
            return true;
        } else {
            return isAIPlaying();
        }
    }

    private boolean isAIPlaying() {
        for (String joinedPlayer : joinedPlayers) {
            if (joinedPlayer.endsWith("AI")) {
                return true;
            }
        }
        return false;
    }

    public boolean canJoinAI() {
        return joinedPlayers.size() < maxPlayers;
    }

    public String join(String handle) {
        if (canJoin()) {
            lastUpdated = System.currentTimeMillis();
            if (joinedPlayers.size() == maxPlayers) removeAIPlayer();
            joinedPlayers.add(handle);
            return handle;
        } else {
            return null;
        }
    }

    private void removeAIPlayer() {
        for (String joinedPlayer : joinedPlayers) {
            if (joinedPlayer.endsWith("AI")) {
                joinedPlayers.remove(joinedPlayer);
                break;
            }
        }
    }

    public String joinAI(String handle) {
        if (canJoinAI()) {
            lastUpdated = System.currentTimeMillis();
            handle = handle + "AI";
            joinedPlayers.add(handle);
            return handle;
        } else {
            return null;
        }
    }

    public void leave(String handle) {
        joinedPlayers.remove(handle);
        lastUpdated = System.currentTimeMillis();
    }

    public int getCountPlayers() {
        return joinedPlayers.size();
    }

    public boolean canStart() {
        return joinedPlayers.size() > 3;
    }

    public List<String> getJoinedPlayers() {
        return joinedPlayers;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public String getVisibility() {
        return this.visibility;
    }
}
