package com.chriscarr.bang.cards;

import java.util.List;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

public class Springfield extends Card implements Playable {
	public Springfield(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	/* (non-Javadoc)
	 * @see main.chriscarr.bang.Playable#canPlay(main.bang.Player, java.util.List, int)
	 */
	public boolean canPlay(Player player, List<Player> players, int bangsPlayed){			
		return player.getHand().size() >= 2;
	}
	
	/* (non-Javadoc)
	 * @see main.chriscarr.bang.Playable#targets(main.bang.Player, java.util.List)
	 */
	public List<Player> targets(Player player, List<Player> players){
		return Turn.others(player, players);
	}
	
	/* (non-Javadoc)
	 * @see main.bang.Playable#play(main.bang.Player, java.util.List, main.bang.UserInterface, main.chriscarr.bang.Deck, main.chriscarr.bang.Discard)
	 */
	public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn){
		//Choose card to discard
		int cardDiscard = userInterface.askDiscard(currentPlayer);
		if(cardDiscard == -1){
			return false;
		}
		
		boolean result = this.shoot(currentPlayer, players, userInterface, deck, discard, turn, false);
		if(result){
			//discard the card
			Hand currentHand = currentPlayer.getHand();
			Card card = currentHand.remove(cardDiscard);
			discard.add(card);
		}
		return result;
	}
}
