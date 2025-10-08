package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

/**
 * The Bang class represents a playable card in the game, extending the functionality
 * of the Card class and implementing the Playable interface. Bang cards are used
 * to attack other players during a game turn.
 * <p>
 * It provides methods to determine if the card can be played, to identify valid
 * targets within range, and to execute the play action.
 */
public class Bang extends Card implements Playable {
    public Bang(CardName name, CardSuit suit, CardValue value, CardType type) {
        super(name, suit, value, type);
    }

    /**
     * Determines whether the "Bang" card can be played by a given player under
     * the current game conditions.
     *
     * @param player      The player attempting to play the "Bang" card.
     * @param players     The list of all players in the game.
     * @param bangsPlayed The number of "Bang" cards already played by the player
     *                    during their current turn.
     * @return true if the "Bang" card can be played based on the player's state,
     * the rules for using multiple "Bang" cards, and the list of valid targets;
     * false otherwise.
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        boolean hasBangsPlayed = bangsPlayed > 0;
        boolean hasVolcanicInPlay = player.getCardsInPlay().hasGun() && player.getCardsInPlay().isGunVolcanic();
        boolean isWillyTheKid = Character.WILLYTHEKID.equals(player.getCharacter());
        if (hasBangsPlayed && !hasVolcanicInPlay && !isWillyTheKid) {
            return false;
        }
        return targets(player, players).size() > 1;
    }

    /**
     * Determines the list of players that are within the range of the specified player
     * to be targeted by the "Bang" card.
     *
     * @param player  The player who is playing the "Bang" card and attempting to target other players.
     * @param players The list of all players currently in the game, including potential targets.
     * @return A list of players who are within the range of the specified player's gun
     * and can be targeted by the "Bang" card.
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.getPlayersWithinRange(player, players);
    }

    /**
     * Executes the play action for the "Bang" card. This method coordinates the
     * interaction between the current player, other players, the game deck, and
     * the discard pile. It determines if the play results in a valid shooting
     * action based on the current game state.
     *
     * @param currentPlayer The player who is playing the "Bang" card.
     * @param players       The list of all players currently in the game.
     * @param userInterface The interface to interact with the user for decision-making.
     * @param deck          The deck of cards being used in the game.
     * @param discard       The discard pile where used cards are stored.
     * @param turn          The current turn in the game.
     * @param skipDiscard   A flag indicating whether discarding a card is skipped
     *                      as part of the play action.
     * @return true if the "Bang" card was played successfully and the action was
     * carried out; false otherwise.
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn, boolean skipDiscard) {
        return this.shoot(currentPlayer, players, userInterface, deck, discard, turn, skipDiscard);
    }

    /**
     * Executes the play action for a card, orchestrating the interaction between
     * the current player, other players, the game deck, and the discard pile.
     * Determines if the play is successful based on the current game state.
     *
     * @param currentPlayer The player attempting to play the card.
     * @param players       The list of all players currently in the game.
     * @param userInterface The interface used for user interactions and decision-making.
     * @param deck          The deck of cards being used in the game.
     * @param discard       The discard pile where used cards are stored.
     * @param turn          The current turn in the game.
     * @return true if the card was played successfully and the action was valid;
     * false otherwise.
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        return this.play(currentPlayer, players, userInterface, deck, discard, turn, false);
    }
}
