package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;
import java.util.List;
import java.util.Objects;

public class Gun extends Card {
  public Gun(CardName name, CardSuit suit, CardValue value, CardType type) {
    super(name, suit, value, type);
  }

  public boolean play(
      Player currentPlayer,
      List<Player> players,
      UserInterface userInterface,
      Deck deck,
      Discard discard,
      Turn turn) {
    if (currentPlayer.hasGun()) {
      discard.add(currentPlayer.removeGun());
    }
    if (Character.JOHNNYKISCH.equals(currentPlayer.getCharacter())) {
      for (Player player : players) {
        if (Objects.equals(player.getCardsInPlay().getGunName(), this.getName())) {
          Gun gun = player.getCardsInPlay().removeGun();
          discard.add(gun);
          userInterface.printInfo(
              currentPlayer.getName()
                  + " plays a "
                  + this.getName()
                  + " and forces "
                  + player.getName()
                  + " to discard one from play.");
        }
      }
    }
    currentPlayer.setGun(this);
    return true;
  }

  public int getRange() {
    return switch (getName()) {
      case REV_CARBINE -> 5;
      case WINCHESTER -> 4;
      case REMINGTON -> 3;
      case SCHOFIELD -> 2;
      default -> 1;
    };
  }
}
