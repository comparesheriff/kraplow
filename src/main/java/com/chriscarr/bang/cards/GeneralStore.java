package java.com.chriscarr.bang.cards;

import java.util.ArrayList;
import java.util.List;

import java.com.chriscarr.bang.Deck;
import java.com.chriscarr.bang.Discard;
import java.com.chriscarr.bang.Player;
import java.com.chriscarr.bang.Turn;
import java.com.chriscarr.bang.userinterface.UserInterface;

public class GeneralStore extends Card implements Playable {
	public GeneralStore(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	/* (non-Javadoc)
	 * @see main.chriscarr.bang.Playable#canPlay(main.bang.Player, java.util.List, int)
	 */
	public boolean canPlay(Player player, List<Player> players, int bangsPlayed){			
		return true;
	}
	
	/* (non-Javadoc)
	 * @see main.bang.Playable#targets(main.chriscarr.bang.Player, java.util.List)
	 */
	public List<Player> targets(Player player, List<Player> players){
		return players;
	}
	
	/* (non-Javadoc)
	 * @see main.bang.Playable#play(main.bang.Player, java.util.List, main.bang.UserInterface, main.chriscarr.bang.Deck, main.chriscarr.bang.Discard)
	 */
	public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn){
		discard.add(this);
		List<Object> generalStoreCards = new ArrayList<>();
		for(int i = 0; i < players.size(); i++){
			if(deck.isEmpty()){
				userInterface.printInfo("Shuffling the deck");
			}
			generalStoreCards.add(deck.pull());
		}
		Player generalPlayer = currentPlayer;
		while(!generalStoreCards.isEmpty()){
			int chosenCard = -1;
			while(chosenCard < 0 || chosenCard > generalStoreCards.size() - 1){
				chosenCard = userInterface.chooseGeneralStoreCard(generalPlayer, generalStoreCards);
			}
			Object card = generalStoreCards.remove(chosenCard);
			userInterface.printInfo(generalPlayer.getName() + " chooses " + ((Card)card).getName() + " from " + Card.CARDGENERALSTORE);
			generalPlayer.getHand().add(card);
			generalPlayer = Turn.getNextPlayer(generalPlayer, players);
		}
		return true;
	}
}
