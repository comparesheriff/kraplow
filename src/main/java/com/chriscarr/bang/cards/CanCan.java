package java.com.chriscarr.bang.cards;

import java.util.List;

import java.com.chriscarr.bang.CancelPlayer;
import java.com.chriscarr.bang.Deck;
import java.com.chriscarr.bang.Discard;
import java.com.chriscarr.bang.Figure;
import java.com.chriscarr.bang.Player;
import java.com.chriscarr.bang.Turn;
import java.com.chriscarr.bang.userinterface.UserInterface;

public class CanCan extends SingleUse implements Playable{

	public CanCan(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	public List<Player> targets(Player player, List<Player> players){
		return Turn.othersWithCardsToTake(player, players);
	}

	public boolean activate(Player currentPlayer, List<Player> players,
		UserInterface userInterface, Deck deck, Discard discard, Turn turn){

		Player otherPlayer = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
		if(Figure.APACHEKID.equals(otherPlayer.getAbility()) && this.getSuit() == Card.DIAMONDS){
			userInterface.printInfo(otherPlayer.getName() + " is unaffected by diamond "+this.getName());
			return true;
		}
		if(!(otherPlayer instanceof CancelPlayer)){
			int chosenCard = -3;
			while(chosenCard < -2 || chosenCard > otherPlayer.getInPlay().size() - 1){
				chosenCard = userInterface.askOthersCard(currentPlayer, otherPlayer.getInPlay(), !otherPlayer.getHand().isEmpty());
			}
			if(chosenCard == -1){
				Card discardedCard = (Card)otherPlayer.getHand().removeRandom();
				discard.add(discardedCard);
				userInterface.printInfo(currentPlayer.getName() + " discards a "+discardedCard.getName()+" from " + otherPlayer.getName() + "'s hand with a "+this.getName());
			} else if(chosenCard == -2){
				Object card = otherPlayer.getInPlay().removeGun();
				discard.add(card);
				userInterface.printInfo(currentPlayer.getName() + " discards a " + ((Card)card).getName() + " from " + otherPlayer.getName() + " with a "+this.getName());
			} else {
				Object card = otherPlayer.getInPlay().remove(chosenCard);
				discard.add(card);
				userInterface.printInfo(currentPlayer.getName() + " discards a " + ((Card)card).getName() + " from " + otherPlayer.getName() + " with a "+this.getName());
			}
			removeFromInPlay(currentPlayer);
			discard.add(this);
			return true;
		} else {
			return false;
		}
	}

}
