package com.chriscarr.bang.cards;

import com.chriscarr.bang.Deck;
import com.chriscarr.bang.Discard;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.Turn;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Gatling extends Card implements Playable {
	public Gatling(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	/* (non-Javadoc)
	 * @see main.chriscarr.bang.Playable#canPlay(main.chriscarr.bang.Player, java.util.List, int)
	 */
	public boolean canPlay(Player player, List<Player> players, int bangsPlayed){			
		return true;
	}
	
	/* (non-Javadoc)
	 * @see main.bang.Playable#targets(main.chriscarr.bang.Player, java.util.List)
	 */
	public List<Player> targets(Player player, List<Player> players){
		return Turn.others(player, players);
	}
	
	/* (non-Javadoc)
	 * @see main.bang.Playable#play(main.bang.Player, java.util.List, main.chriscarr.bang.UserInterface, main.chriscarr.bang.Deck, main.bang.Discard)
	 */
	public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn){
		discard.add(this);
		Player gatlingPlayer = Turn.getNextPlayer(currentPlayer, players);
		while(gatlingPlayer != currentPlayer){
			Player nextPlayer = Turn.getNextPlayer(gatlingPlayer, players);
			shoot(currentPlayer, players, userInterface, deck, discard, turn, true, gatlingPlayer);
			gatlingPlayer = nextPlayer;
		}
		return true;
	}
}
