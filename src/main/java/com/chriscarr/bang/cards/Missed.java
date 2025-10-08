package com.chriscarr.bang.cards;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Player;
import java.util.List;

public class Missed extends Bang implements Playable {

  public Missed(CardName name, CardSuit suit, CardValue value, CardType type) {
    super(name, suit, value, type);
  }

  public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
    if (!Character.CALAMITYJANET.equals(player.getCharacter())) {
      return false;
    } else {
      return super.canPlay(player, players, bangsPlayed);
    }
  }
}
