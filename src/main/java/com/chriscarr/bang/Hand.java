package com.chriscarr.bang;

import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.cards.SingleUse;

import java.util.ArrayList;
import java.util.List;

public class Hand extends ArrayList<Card> {

	List<Card> cards = new ArrayList<>();

	@Override
	public boolean add(Card card) {
		if(card instanceof SingleUse singleUseCard){
            singleUseCard.setReadyToPlay(false);
		}
		return super.add(card);
	}

	public int countBangs() {
		int bangs = 0;
		for(Card card : cards){
			if(card.getName().equals(Card.CARDBANG)){
				bangs = bangs + 1;
			}
		}
		return bangs;
	}

	public int countMisses() {
		int bangs = 0;
		for(Card card : cards){
			if(card.getName().equals(Card.CARDMISSED)){
				bangs = bangs + 1;
			}
		}
		return bangs;
	}

	public Card removeMiss() {
		for(Card card : cards){
			if(card.getName().equals(Card.CARDMISSED)){
				cards.remove(card); 
				return card;
			}
		}
		return null;
	}

	public Card removeRandom() {
        return cards.remove((int)(Math.random() * cards.size()));
	}

	public int countBeers() {
		int beers = 0;
		for(Card card : cards){
			if(card.getName().equals(Card.CARDBEER)){
				beers = beers + 1;
			}
		}
		return beers;
	}

	public Card removeBeer() {
		for(Card card : cards){
			if(card.getName().equals(Card.CARDBEER)){
				cards.remove(card); 
				return card;
			}
		}
		return null;
	}
}
