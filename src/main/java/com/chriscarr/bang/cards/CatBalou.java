package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.List;

public class CatBalou extends Card implements Playable {
    public CatBalou(String name, int suit, int value, int type) {
        super(name, suit, value, type);
    }

    /* (non-Javadoc)
     * @see main.chriscarr.bang.Playable#canPlay(main.chriscarr.bang.Player, java.util.List, int)
     */
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        List<Player> others = targets(player, players);
        return !others.isEmpty();
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#targets(main.bang.Player, java.util.List)
     */
    public List<Player> targets(Player player, List<Player> players) {
        return Turn.othersWithCardsToTake(player, players);
    }

    /* (non-Javadoc)
     * @see main.bang.Playable#play(main.chriscarr.bang.Player, java.util.List, main.chriscarr.bang.UserInterface, main.bang.Deck, main.chriscarr.bang.Discard)
     */
    public boolean play(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, Discard discard, Turn turn) {
        discard.add(this);
        Player other = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        if (Character.APACHEKID.equals(other.getCharacter()) && this.getSuit() == Card.DIAMONDS) {
            userInterface.printInfo(other.getName() + " is unaffected by diamond Cat Balou");
            return true;
        }
        int chosenCard = -3;
        while (chosenCard < -2 || chosenCard > other.getInPlay().size() - 1) {
            chosenCard = userInterface.askOthersCard(currentPlayer, other.getInPlay(), !other.getHand().isEmpty());
        }
        if (chosenCard == -1) {
            Card card = other.getHand().removeRandom();
            discard.add(card);
            userInterface.printInfo(currentPlayer.getName() + " discards a " + card.getName() + " from " + other.getName() + "'s hand with a Cat Balou");
        } else if (chosenCard == -2) {
            Card card = other.getInPlay().removeGun();
            discard.add(card);
            userInterface.printInfo(currentPlayer.getName() + " discards a " + card.getName() + " from " + other.getName() + " with a Cat Balou");
        } else {
            Card card = other.getInPlay().remove(chosenCard);
            discard.add(card);
            userInterface.printInfo(currentPlayer.getName() + " discards a " + card.getName() + " from " + other.getName() + " with a Cat Balou");
        }
        return true;
    }
}
