package java.com.chriscarr.bang.cards;

import java.util.List;

import java.com.chriscarr.bang.Deck;
import java.com.chriscarr.bang.Discard;
import java.com.chriscarr.bang.Player;
import java.com.chriscarr.bang.Turn;
import java.com.chriscarr.bang.userinterface.UserInterface;

public class Punch extends Card implements Playable {
	public Punch(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	/* (non-Javadoc)
	 * @see main.bang.Playable#canPlay(main.chriscarr.bang.Player, java.util.List, int)
	 */
	public boolean canPlay(Player player, List<Player> players, int bangsPlayed){			
		return targets(player, players).size() > 1;
	}
	
	/* (non-Javadoc)
	 * @see main.bang.Playable#targets(main.chriscarr.bang.Player, java.util.List)
	 */
	public List<Player> targets(Player player, List<Player> players){
		return Turn.getPlayersWithCards(Turn.getPlayersWithinRange(player, players, 1));
	}
	
	/* (non-Javadoc)
	 * @see main.chriscarr.bang.Playable#play(main.chriscarr.bang.Player, java.util.List, main.chriscarr.bang.UserInterface, main.bang.Deck, main.bang.Discard)
	 */
	public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn){
		return this.shoot(currentPlayer, players, userInterface, deck, discard, turn, false);
	}
}
