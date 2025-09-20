package java.com.chriscarr.bang.cards;

import java.util.List;

import java.com.chriscarr.bang.Deck;
import java.com.chriscarr.bang.Discard;
import java.com.chriscarr.bang.Player;
import java.com.chriscarr.bang.Turn;
import java.com.chriscarr.bang.userinterface.UserInterface;

public class Pepperbox extends SingleUse implements Playable{

	public Pepperbox(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	public List<Player> targets(Player player, List<Player> players){
		return Turn.getPlayersWithCards(Turn.getPlayersWithinRange(player, players, player.getGunRange()));
	}

	public boolean activate(Player currentPlayer, List<Player> players,
		UserInterface userInterface, Deck deck, Discard discard, Turn turn){

		boolean result = this.shoot(currentPlayer, players, userInterface, deck, discard, turn, true);
		if(result){
			removeFromInPlay(currentPlayer);
			discard.add(this);
		}
		return result;
	}

}
