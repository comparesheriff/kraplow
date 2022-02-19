package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class GameResponseModel extends JsonResponseModel {
    @JsonProperty("gameid")
    private Integer gameId;
    @JsonProperty("playercount")
    private Integer playerCount;
    @JsonProperty("canjoin")
    private Boolean canJoin;
    @JsonProperty("players")
    private List<String> players;

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public Boolean getCanJoin() {
        return canJoin;
    }

    public void setCanJoin(Boolean canJoin) {
        this.canJoin = canJoin;
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
