package java.com.chriscarr.bang.cards;

import java.util.List;

import java.com.chriscarr.bang.Deck;
import java.com.chriscarr.bang.Discard;
import java.com.chriscarr.bang.Player;
import java.com.chriscarr.bang.Turn;
import java.com.chriscarr.bang.userinterface.UserInterface;

public interface Playable {

	boolean canPlay(Player player, List<Player> players,
                    int bangsPlayed);

	List<Player> targets(Player player, List<Player> players);

	boolean play(Player currentPlayer, List<Player> players,
                 UserInterface userInterface, Deck deck, Discard discard, Turn turn);

}