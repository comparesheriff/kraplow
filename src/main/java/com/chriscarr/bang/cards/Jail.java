package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;
import java.util.Objects;

public class Jail extends Card implements Playable {

	public Jail(String name, int suit, int value, int type) {
		super(name, suit, value, type);
	}

	@Override
	public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
		return targets(player, players).size() > 1;
	}

	@Override
	public boolean play(Player currentPlayer, List<Player> players,
			UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
		Player target = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
		if(!(target instanceof CancelPlayer)){
			if (Character.JOHNNYKISCH.equals(currentPlayer.getCharacter())) {
				for (Player player : players) {
					int inPlayCount = player.getInPlay().size();
					for(int inPlayIndex = 0; inPlayIndex < inPlayCount; inPlayIndex++){
						Card peeked = player.getInPlay().get(inPlayIndex);
						if(Objects.equals(peeked.getName(), this.getName())){
							Card removed = player.getInPlay().remove(inPlayIndex);
							discard.add(removed);
							userInterface.printInfo(currentPlayer.getName() + " plays a " + this.getName() + " and forces " + player.getName() + " to discard one from play.");
						}
					}
				}
			}

            target.addInPlay(this);
			userInterface.printInfo(currentPlayer.getName() + " put " + target.getName() + " in jail.");
			return true;
		} else {
			currentPlayer.getHand().add(this);
			return false;
		}
	}

	@Override
	public List<Player> targets(Player player, List<Player> players) {
		return Turn.getJailablePlayers(player, players);
	}

}
