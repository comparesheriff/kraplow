package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;
import java.util.List;

public class Indians extends Card implements Playable {
  public Indians(CardName name, CardSuit suit, CardValue value, CardType type) {
    super(name, suit, value, type);
  }

  /* (non-Javadoc)
   * @see main.chriscarr.bang.Playable#canPlay(main.bang.Player, java.util.List, int)
   */
  public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
    return true;
  }

  /* (non-Javadoc)
   * @see main.bang.Playable#targets(main.chriscarr.bang.Player, java.util.List)
   */
  public List<Player> targets(Player player, List<Player> players) {
    return Turn.others(player, players);
  }

  /* (non-Javadoc)
   * @see main.bang.Playable#play(main.chriscarr.bang.Player, java.util.List, main.chriscarr.bang.UserInterface, main.chriscarr.bang.Deck, main.bang.Discard)
   */
  public boolean play(
      Player currentPlayer,
      List<Player> players,
      UserInterface userInterface,
      Deck deck,
      Discard discard,
      Turn turn) {
    discard.add(this);
    Player indianPlayer = Turn.getNextPlayer(currentPlayer, players);
    while (indianPlayer != currentPlayer) {
      Player nextPlayer = Turn.getNextPlayer(indianPlayer, players);
      if (Character.APACHEKID.equals(indianPlayer.getCharacter())
          && this.getSuit() == CardSuit.DIAMONDS) {
        userInterface.printInfo(indianPlayer.getName() + " is unaffected by diamond Indians");
        indianPlayer = nextPlayer;
        continue;
      }
      int bangPlayed = Turn.validPlayBang(indianPlayer, userInterface);
      if (bangPlayed == -1) {
        turn.damagePlayer(
            indianPlayer, players, currentPlayer, 1, currentPlayer, deck, discard, userInterface);
        userInterface.printInfo(
            indianPlayer.getName()
                + " loses a health from "
                + currentPlayer.getName()
                + "'s "
                + CardName.INDIANS);
      } else {
        discard.add(indianPlayer.getHand().remove(bangPlayed));
        userInterface.printInfo(
            indianPlayer.getName()
                + " repels the attack from "
                + currentPlayer.getName()
                + "'s "
                + CardName.INDIANS);
        if (Character.MOLLYSTARK.equals(indianPlayer.getCharacter())) {
          Hand otherHand = indianPlayer.getHand();
          otherHand.add(deck.pull());
          userInterface.printInfo(indianPlayer.getName() + " draws a card");
        }
      }
      indianPlayer = nextPlayer;
    }
    return true;
  }
}
