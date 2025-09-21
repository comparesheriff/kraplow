package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class Duel extends Card implements Playable {
    public Duel(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#canPlay(main.chriscarr.bang.Player, java.util.List, int)
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
     * @see main.chriscarr.bang.Playable#play(main.bang.Player, java.util.List, main.chriscarr.bang.UserInterface, main.bang.Deck, main.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        discard.add(this);
        List<Player> targets = Turn.others(currentPlayer, players);
        CancelPlayer cancelPlayer = new CancelPlayer();
        targets.addFirst(cancelPlayer);
        Player other = Turn.getValidChosenPlayer(currentPlayer, targets, userInterface);
        if (!(other instanceof CancelPlayer)) {
            if (Character.APACHEKID.equals(other.getCharacter()) && this.getSuit() == Card.DIAMONDS) {
                userInterface.printInfo(other.getName() + " is unaffected by diamond Duel");
                return true;
            }
            userInterface.printInfo(currentPlayer.getName() + " duels " + other.getName());
            int mollyStarkAmount = 0;
            while (true) {
                int bangPlayed = Turn.validPlayBang(other, userInterface);
                if (bangPlayed == -1) {
                    turn.damagePlayer(other, players, currentPlayer, 1, currentPlayer, deck, discard, userInterface);
                    userInterface.printInfo(other.getName() + " loses a health");
                    if (Character.MOLLYSTARK.equals(other.getCharacter())) {
                        giveMollyStarkCards(other, deck, mollyStarkAmount, userInterface);
                    }
                    return true;
                } else {
                    Card card = other.getHand().remove(bangPlayed);
                    discard.add(card);
                    userInterface.printInfo(other.getName() + " plays a " + card.getName());
                    if (Character.MOLLYSTARK.equals(other.getCharacter())) {
                        mollyStarkAmount += 1;
                    }
                }
                int currentBangPlayed = Turn.validPlayBang(currentPlayer, userInterface);
                if (currentBangPlayed == -1) {
                    turn.damagePlayer(currentPlayer, players, currentPlayer, 1, null, deck, discard, userInterface);
                    userInterface.printInfo(currentPlayer.getName() + " loses a health");
                    if (Character.MOLLYSTARK.equals(other.getCharacter())) {
                        giveMollyStarkCards(other, deck, mollyStarkAmount, userInterface);
                    }
                    return true;
                } else {
                    Card card = currentPlayer.getHand().remove(currentBangPlayed);
                    discard.add(card);
                    userInterface.printInfo(currentPlayer.getName() + " plays a " + card.getName());
                }
            }
        } else {
            currentPlayer.getHand().add(this);
            return false;
        }
    }

    private void giveMollyStarkCards(Player player, Deck deck, int amountCards, UserInterface userInterface) {
        for (int i = 0; i < amountCards; i++) {
            Hand hand = player.getHand();
            hand.add(deck.pull());
        }
        if (amountCards > 0) {
            userInterface.printInfo(player.getName() + " draws " + amountCards + " card(s)");
        }
    }
}
