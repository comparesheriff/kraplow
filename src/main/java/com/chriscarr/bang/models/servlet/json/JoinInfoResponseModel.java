package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JoinInfoResponseModel extends JsonResponseModel {
    @JsonProperty("user")
    private final String user;
    @JsonProperty("gameid")
    private final Integer gameId;

    public JoinInfoResponseModel(String user, Integer gameId) {
        this.user = user;
        this.gameId = gameId;
    }

    public String getUser() {
        return user;
    }

    public Integer getGameId() {
        return gameId;
    }
}
