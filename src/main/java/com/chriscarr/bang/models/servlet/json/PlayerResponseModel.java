package com.chriscarr.bang.models.servlet.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayerResponseModel {
    private String handle;
    private String name;
    @JsonProperty("specialability")
    private String specialAbility;
    private Integer health;
    @JsonProperty("maxhealth")
    private Integer maxHealth;
    @JsonProperty("handsize")
    private Integer handSize;
    @JsonProperty("issheriff")
    private Boolean isSheriff;
    private CardResponseModel gun;
    @JsonProperty("inplay")
    private List<CardResponseModel> inPlay;

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialAbility() {
        return specialAbility;
    }

    public void setSpecialAbility(String specialAbility) {
        this.specialAbility = specialAbility;
    }

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public Integer getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(Integer maxHealth) {
        this.maxHealth = maxHealth;
    }

    public Integer getHandSize() {
        return handSize;
    }

    public void setHandSize(Integer handSize) {
        this.handSize = handSize;
    }

    public Boolean getSheriff() {
        return isSheriff;
    }

    public void setSheriff(Boolean sheriff) {
        isSheriff = sheriff;
    }

    public CardResponseModel getGun() {
        return gun;
    }

    public void setGun(CardResponseModel gun) {
        this.gun = gun;
    }

    public List<CardResponseModel> getInPlay() {
        return inPlay;
    }

    public void setInPlay(List<CardResponseModel> inPlay) {
        this.inPlay = inPlay;
    }

    public void addInPlay(CardResponseModel inPlayCard){
        if(inPlay == null){
            inPlay = new ArrayList<>();
        }
        inPlay.add(inPlayCard);
    }
}
