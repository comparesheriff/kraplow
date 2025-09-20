package java.com.chriscarr.bang.cards;

import java.util.List;
import java.util.Objects;

import java.com.chriscarr.bang.Deck;
import java.com.chriscarr.bang.Discard;
import java.com.chriscarr.bang.Player;
import java.com.chriscarr.bang.Turn;
import java.com.chriscarr.bang.Figure;
import java.com.chriscarr.bang.userinterface.UserInterface;

public class Gun extends Card {
	public Gun(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	public boolean play(Player currentPlayer, List<Player> players,
			UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
		if(currentPlayer.hasGun()){
			discard.add(currentPlayer.removeGun());
		}
		if (Figure.JOHNNYKISCH.equals(currentPlayer.getAbility())) {
			for (Player player : players) {
				if(Objects.equals(player.getInPlay().getGunName(), this.getName())){
					Object gun = player.getInPlay().removeGun();
					discard.add(gun);
					userInterface.printInfo(currentPlayer.getName() + " plays a " + this.getName() + " and forces " + player.getName() + " to discard one from play.");
				}
			}
		}
		currentPlayer.setGun(this);
		return true;
	}
}
