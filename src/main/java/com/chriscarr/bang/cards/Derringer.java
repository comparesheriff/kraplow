package java.com.chriscarr.bang.cards;

import java.util.List;

import java.com.chriscarr.bang.Deck;
import java.com.chriscarr.bang.Discard;
import java.com.chriscarr.bang.Hand;
import java.com.chriscarr.bang.Player;
import java.com.chriscarr.bang.Turn;
import java.com.chriscarr.bang.userinterface.UserInterface;

public class Derringer extends SingleUse implements Playable{

	public Derringer(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	public List<Player> targets(Player player, List<Player> players){
		return Turn.getPlayersWithCards(Turn.getPlayersWithinRange(player, players, 1));
	}

	public boolean activate(Player currentPlayer, List<Player> players,
		UserInterface userInterface, Deck deck, Discard discard, Turn turn){

		boolean result = this.shoot(currentPlayer, players, userInterface, deck, discard, turn, true);
		if(result){
			removeFromInPlay(currentPlayer);
			discard.add(this);
			Hand currentHand = currentPlayer.getHand();
			userInterface.printInfo(currentPlayer.getName() + " draws a card");
			currentHand.add(deck.pull());
		}
		return result;
	}

}
