package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.Gun;
import com.chriscarr.bang.gamestate.GameStateCard;

import java.util.List;

public class Player {
	public static final int SHERIFF = 0;
	public static final int OUTLAW = 1;
	public static final int DEPUTY = 2;
	public static final int RENEGADE = 3;
	
	private Character character;
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

	public void setRole(Role role) {
		this.role = role;
	}

	public void setCharacter(Character character) {
		this.character = character;
		this.setAbility(character.getName());
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

	public void addInPlay(Card card) {
		inPlay.add(card);
	}

	public boolean isInPlay(String name) {
		return inPlay.hasItem(name) || inPlay.getGunName().equals(name);
	}

	public String getName() {
		return character.getName();
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

	public Card removeRandom() {
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
		inPlay.setGun(card);
	}

	public String getGunName() {
		return inPlay.getGunName();
	}

	public boolean hasGun() {
		return inPlay.hasGun();
	}

	public Gun removeGun() {
		return inPlay.removeGun();
	}

	public int getHandSize() {
		return hand.size();
	}

	public boolean isSheriff() {
		return Role.SHERIFF.equals(role);
	}

	public GameStateCard getGameStateGun() {
		return Turn.cardToGameStateCard(inPlay.getGun());
	}

	public String getSpecialAbility() {
		return character.getSpecialAbilityText();
	}

	public List<GameStateCard> getGameStateInPlay() {
		return inPlay.getGameStateInPlay();
	}

}
