package java.com.chriscarr.bang.cards;

import java.util.List;

import java.com.chriscarr.bang.Deck;
import java.com.chriscarr.bang.Discard;
import java.com.chriscarr.bang.Player;
import java.com.chriscarr.bang.Turn;
import java.com.chriscarr.bang.userinterface.UserInterface;

public class Canteen extends SingleUse implements Playable{

	public Canteen(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	public List<Player> targets(Player player, List<Player> players){
		return Turn.othersWithCardsToTake(player, players);
	}

	public boolean activate(Player currentPlayer, List<Player> players,
		UserInterface userInterface, Deck deck, Discard discard, Turn turn){

		if(!Turn.isMaxHealth(currentPlayer)){
			currentPlayer.addHealth(1);
		}
		removeFromInPlay(currentPlayer);
		discard.add(this);
		return true;
	}

}
