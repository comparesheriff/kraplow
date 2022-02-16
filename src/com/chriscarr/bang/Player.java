package com.chriscarr.bang;

import java.util.List;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.models.Role;

public class Player {

	public static final int SHERIFF = 0;
	public static final int OUTLAW = 1;
	public static final int DEPUTY = 2;
	public static final int RENEGADE = 3;
	
	private Figure figure;
	private Hand hand;
	private InPlay inPlay;
	private Role role;
	private int maxHealth;
	private int health;	
	private String ability;

	public void setInPlay(InPlay inPlay) {
		this.inPlay = inPlay;
	}

	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void setFigure(Figure figure) {
		this.figure = figure;
		this.setAbility(figure.getName());
	}

	public Figure getFigure() {
		return figure;
	}

	public Hand getHand() {
		return hand;
	}

	public InPlay getInPlay() {
		return inPlay;
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
	
	public int getHealth(){
		return health;
	}

	public Role getRole(){
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void addInPlay(Card card) {
		inPlay.add(card);
	}

	public boolean isInPlay(String name) {
		return inPlay.hasItem(name) || inPlay.getGunName().equals(name);
	}

	public String getName() {
		return figure.getName();
	}

	public String getAbility() {
		return this.ability;
	}

	public void setAbility(String ability) {
		this.ability = ability;
	}

	public int getGunRange() {
		return inPlay.getGunRange();
	}

	public void addHealth(int toAdd) {
		health = health + toAdd;
	}

	public Object removeRandom() {
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

	public void setGun(Card card) {
		inPlay.setGun(card);
	}

	public Object getGunName() {
		return inPlay.getGunName();
	}

	public boolean hasGun() {
		return inPlay.hasGun();
	}

	public Object removeGun() {
		return inPlay.removeGun();
	}

	public int getHandSize() {
		return hand.size();
	}

	public boolean isSheriff() {
		return Role.SHERIFF.equals(role);
	}

	public GameStateCard getGameStateGun() {
		return Turn.cardToGameStateCard((Card)inPlay.getGun());
	}

	public String getSpecialAbility() {
		return Figure.getSpecialAbilityText(getName());
	}

	public List<GameStateCard> getGameStateInPlay() {
		return inPlay.getGameStateInPlay();
	}


}
