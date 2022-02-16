package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Bang extends Card implements Playable {
    public Bang(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#canPlay(com.chriscarr.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        if (bangsPlayed > 0 && !(player.getInPlay().hasGun() && player.getInPlay().isGunVolcanic()) && !Figure.WILLYTHEKID.equals(player.getAbility())) {
            return false;
        }
        return targets(player, players).size() > 1;
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#targets(com.chriscarr.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.getPlayersWithinRange(player, players, player.getGunRange());
    }

    /* (non-Javadoc)
     * @see com.chriscarr.bang.Playable#play(com.chriscarr.bang.Player, java.util.List, com.chriscarr.bang.UserInterface, com.chriscarr.bang.Deck, com.chriscarr.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn, boolean skipDiscard) {
        return this.shoot(currentPlayer, players, userInterface, deck, discard, turn, skipDiscard);
    }

    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        return this.play(currentPlayer, players, userInterface, deck, discard, turn, false);
    }
}
