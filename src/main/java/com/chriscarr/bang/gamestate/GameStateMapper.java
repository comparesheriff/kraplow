package com.chriscarr.bang.gamestate;

import com.chriscarr.bang.Player;
import com.chriscarr.bang.cards.Card;
import java.util.Optional;

public final class GameStateMapper {
  private GameStateMapper() {}

  public static Optional<GameStateCard> cardToGameStateCard(Card card) {
    if (card == null) {
      return Optional.empty();
    }
    GameStateCard gameStateCard = new GameStateCard();
    gameStateCard.name = card.getName();
    gameStateCard.suit = card.getSuit().getLabel();
    gameStateCard.value = card.getValue().getLabel();
    gameStateCard.type = card.getType().getTypeName();
    return Optional.of(gameStateCard);
  }

  public static GameStatePlayer playerToGameStatePlayer(Player player) {
    GameStatePlayer gameStatePlayer = new GameStatePlayer();
    gameStatePlayer.name = player.getName();
    gameStatePlayer.health = player.getHealth();
    gameStatePlayer.maxHealth = player.getMaxHealth();
    gameStatePlayer.handSize = player.getHandSize();
    gameStatePlayer.gun = player.getGameStateGun().orElse(null);
    gameStatePlayer.isSheriff = player.isSheriff();
    gameStatePlayer.specialAbility = player.getSpecialAbility();
    gameStatePlayer.inPlay = player.getGameStateInPlay();
    return gameStatePlayer;
  }
}
