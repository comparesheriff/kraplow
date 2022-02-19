package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class GameStateResponseModel extends JsonResponseModel {
    @JsonProperty("players")
    private List<PlayerResponseModel> players;
    @JsonProperty("timeout")
    private Boolean timeOut;
    @JsonProperty("gameover")
    private Boolean gameOver;
    @JsonProperty("currentname")
    private String currentName ;
    @JsonProperty("decksize")
    private Integer deckSize;
    @JsonProperty("discardtopcard")
    private CardResponseModel discardTopCard;
    @JsonProperty("roles")
    private List<RoleResponseModel> roles;

    public List<PlayerResponseModel> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerResponseModel> players) {
        this.players = players;
    }

    public void addPlayer(PlayerResponseModel player){
        if(players == null){
            players = new ArrayList<>();
        }
        players.add(player);
    }

    public Boolean getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Boolean timeOut) {
        this.timeOut = timeOut;
    }

    public Boolean getGameOver() {
        return gameOver;
    }

    public void setGameOver(Boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getCurrentName() {
        return currentName;
    }

    public void setCurrentName(String currentName) {
        this.currentName = currentName;
    }

    public Integer getDeckSize() {
        return deckSize;
    }

    public void setDeckSize(Integer deckSize) {
        this.deckSize = deckSize;
    }

    public CardResponseModel getDiscardTopCard() {
        return discardTopCard;
    }

    public void setDiscardTopCard(CardResponseModel discardTopCard) {
        this.discardTopCard = discardTopCard;
    }

    public List<RoleResponseModel> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleResponseModel> roles) {
        this.roles = roles;
    }

    public void addRole(RoleResponseModel role){
        if(roles == null){
            roles = new ArrayList<>();
        }
        roles.add(role);
    }


}
