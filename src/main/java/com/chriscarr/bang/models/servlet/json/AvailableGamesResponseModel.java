package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;
import java.util.List;

public class AvailableGamesResponseModel extends JsonResponseModel {
    @JsonUnwrapped
    List<GameResponseModel> games;

    public List<GameResponseModel> getGames() {
        return games;
    }

    public void setGames(List<GameResponseModel> games) {
        this.games = games;
    }

    public void addGame(GameResponseModel game){
        if (games == null) {
            games = new ArrayList<>();
        }
        games.add(game);
    }
}
