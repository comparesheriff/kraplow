package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class CountPlayersResponseModel extends JsonResponseModel {
    @JsonProperty("playercount")
    private Integer playerCount;
    @JsonProperty("players")
    private List<String> players;

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void addPlayer(String playerName) {
        if (players == null) {
            players = new ArrayList<>();
        }
        players.add(playerName);
    }
}
