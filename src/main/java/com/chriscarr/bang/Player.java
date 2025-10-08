package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.CardName;
import com.chriscarr.bang.cards.Gun;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.gamestate.GameStateMapper;

import java.util.List;
import java.util.Optional;

public class Player {
    private Character character;
    private Hand hand;
    private CardsInPlay cardsInPlay;
    private Role role;
    private int maxHealth;
    private int health;

    public void setInPlay(CardsInPlay cardsInPlay) {
        this.cardsInPlay = cardsInPlay;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }

    public Role getRole() {
        return role;
    }

    public Hand getHand() {
        return hand;
    }

    public CardsInPlay getCardsInPlay() {
        return cardsInPlay;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void addInPlay(Card card) {
        cardsInPlay.add(card);
    }

    public boolean isInPlay(CardName name) {
        return cardsInPlay.hasItem(name) || cardsInPlay.getGunName().equals(name);
    }

    public String getName() {
        return character.getName();
    }

    public int getGunRange() {
        return cardsInPlay.getGunRange();
    }

    public void addHealth(int toAdd) {
        health = health + toAdd;
    }

    public Optional<Card> removeRandom() {
        return hand.removeRandom();
    }

    public int countBeers() {
        return hand.countBeers();
    }

    public int countBangs() {
        return hand.countBangs();
    }

    public int countMisses() {
        return hand.countMisses();
    }

    public void setGun(Gun card) {
        cardsInPlay.setGun(card);
    }

    public CardName getGunName() {
        return cardsInPlay.getGunName();
    }

    public boolean hasGun() {
        return cardsInPlay.hasGun();
    }

    public Gun removeGun() {
        return cardsInPlay.removeGun();
    }

    public int getHandSize() {
        return hand.size();
    }

    public boolean isSheriff() {
        return Role.SHERIFF.equals(role);
    }

    public Optional<GameStateCard> getGameStateGun() {
        return GameStateMapper.cardToGameStateCard(cardsInPlay.getGun());
    }

    public String getSpecialAbility() {
        return character.getSpecialAbilityText();
    }

    public List<GameStateCard> getGameStateInPlay() {
        return cardsInPlay.getGameStateInPlay();
    }

}
