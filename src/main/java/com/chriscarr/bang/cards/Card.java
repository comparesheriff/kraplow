package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Card implements Playable {
    private CardName name;
    private CardSuit suit;
    private CardValue value;
    private CardType type;

    public Card() {
    }

    public Card(CardName name, CardSuit suit, CardValue value, CardType type) {
        this.name = name;
        this.suit = suit;
        this.value = value;
        this.type = type;
    }

    public void setName(CardName name) {
        this.name = name;
    }

    public CardName getName() {
        return name;
    }

    public void setSuit(CardSuit suit) {
        this.suit = suit;
    }

    public CardSuit getSuit() {
        return suit;
    }

    public void setValue(CardValue value) {
        this.value = value;
    }

    public CardValue getValue() {
        return value;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public CardType getType() {
        return type;
    }

    public static boolean multiBang(CardName gunName) {
        return gunName.equals(CardName.VOLCANIC);
    }

    public static boolean isExplode(Card drawnCard) {
        if (drawnCard.suit == CardSuit.SPADES) {
            return drawnCard.value.getValue() <= 9;
        }
        return false;
    }

    @Override
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return !player.isInPlay(name);
    }

    @Override
    public boolean play(
        Player currentPlayer,
        List<Player> players,
        UserInterface userInterface,
        Deck deck,
        Discard discard,
        Turn turn) {
        if (Character.JOHNNYKISCH.equals(currentPlayer.getCharacter())) {
            for (Player player : players) {
                int inPlayCount = player.getCardsInPlay().size();
                for (int inPlayIndex = 0; inPlayIndex < inPlayCount; inPlayIndex++) {
                    Card peeked = player.getCardsInPlay().get(inPlayIndex);
                    if (Objects.equals(peeked.getName(), this.getName())) {
                        Card removed = player.getCardsInPlay().remove(inPlayIndex);
                        discard.add(removed);
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
        }
        currentPlayer.addInPlay(this);
        return true;
    }

    @Override
    public List<Player> targets(Player player, List<Player> players) {
        List<Player> targets = new ArrayList<>();
        targets.add(player);
        return targets;
    }

    public boolean shoot(
        Player currentPlayer,
        List<Player> players,
        UserInterface userInterface,
        Deck deck,
        Discard discard,
        Turn turn,
        boolean skipDiscard) {
        return shoot(currentPlayer, players, userInterface, deck, discard, turn, skipDiscard, null);
    }

    public boolean shoot(
        Player currentPlayer,
        List<Player> players,
        UserInterface userInterface,
        Deck deck,
        Discard discard,
        Turn turn,
        boolean skipDiscard,
        Player targetPlayer) {
        Player otherPlayer;
        if (targetPlayer == null) {
            otherPlayer =
                Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        } else {
            otherPlayer = targetPlayer;
        }
        if (!(otherPlayer instanceof CancelPlayer)) {
            userInterface.printInfo(currentPlayer.getName() + " Shoots " + otherPlayer.getName());
            if (Character.APACHEKID.equals(otherPlayer.getCharacter())
                && this.getSuit() == CardSuit.DIAMONDS) {
                userInterface.printInfo(
                    otherPlayer.getName() + " is unaffected by diamond " + this.getName());
                if (!skipDiscard) {
                    discard.add(this);
                }
                return true;
            }
            int missesRequired = 1;
            if (Objects.equals(this.getName(), CardName.BANG)
                && Character.SLABTHEKILLER.equals(currentPlayer.getCharacter())) {
                missesRequired = 2;
            }
            int barrelMisses =
                Turn.isBarrelSave(
                    otherPlayer, deck, discard, userInterface, missesRequired, currentPlayer);
            missesRequired = missesRequired - barrelMisses;
            boolean canPlaySingleUse = !Character.BELLESTAR.equals(currentPlayer.getCharacter());
            if (missesRequired <= 0) {
                if (!skipDiscard) {
                    discard.add(this);
                }
                return true;
            } else if (missesRequired == 1) {
                int missPlayed = Turn.validPlayMiss(otherPlayer, userInterface, canPlaySingleUse);
                if (missPlayed == -1) {
                    turn.damagePlayer(
                        otherPlayer, players, currentPlayer, 1, currentPlayer, deck, discard, userInterface);
                    userInterface.printInfo(otherPlayer.getName() + " loses a health.");
                } else {
                    for (int i = 0; i < missesRequired; i++) {
                        if (missPlayed < otherPlayer.getHand().size()) {
                            Card missCard = otherPlayer.getHand().remove(missPlayed);
                            discard.add(missCard);
                            if (missCard.getName().equals(CardName.DODGE)) {
                                Hand otherHand = otherPlayer.getHand();
                                otherHand.add(deck.pull());
                                userInterface.printInfo(
                                    otherPlayer.getName()
                                        + " plays a "
                                        + missCard.getName()
                                        + " countering "
                                        + currentPlayer.getName()
                                        + "'s "
                                        + CardName.BANG
                                        + " and draws a card");
                            } else {
                                if (missCard.getName().equals(CardName.MISSED)) {
                                    userInterface.printInfo(otherPlayer.getName() + " plays a " + missCard.getName());
                                } else {
                                    userInterface.printInfo(
                                        otherPlayer.getName() + " plays a " + missCard.getName() + " as a Missed!");
                                }
                            }
                            if (Character.MOLLYSTARK.equals(otherPlayer.getCharacter())) {
                                Hand otherHand = otherPlayer.getHand();
                                otherHand.add(deck.pull());
                                userInterface.printInfo(otherPlayer.getName() + " draws a card");
                            }
                        } else {
                            missPlayed -= otherPlayer.getHand().size();
                            CardsInPlay cardsInPlay = otherPlayer.getCardsInPlay();
                            SingleUseMissed sum = (SingleUseMissed) cardsInPlay.remove(missPlayed);
                            if (sum.getName().equals(CardName.BIBLE)) {
                                otherPlayer.getHand().add(deck.pull());
                            }
                            userInterface.printInfo(otherPlayer.getName() + " plays a " + sum.getName());
                            discard.add(sum);
                        }
                    }
                }
            } else if (missesRequired == 2) {
                Hand hand = otherPlayer.getHand();
                CardsInPlay cardsInPlay = otherPlayer.getCardsInPlay();
                List<Card> cardsToDiscard;
                cardsToDiscard = Turn.validRespondTwoMiss(otherPlayer, userInterface);
                if (cardsToDiscard.isEmpty()) {
                    turn.damagePlayer(
                        otherPlayer, players, currentPlayer, 1, currentPlayer, deck, discard, userInterface);
                    userInterface.printInfo(otherPlayer.getName() + " loses a health.");
                } else {
                    // TODO issue here, can select more than 2 cards. Green card and missed locks up game.
                    for (Card card : cardsToDiscard) {
                        if (cardsInPlay.hasItem(card.getName())) {
                            for (int i = 0; i < cardsInPlay.size(); i++) {
                                Card gotCard = cardsInPlay.get(i);
                                if (gotCard.getName().equals(card.getName())) {
                                    discard.add(cardsInPlay.remove(i));
                                }
                            }
                            userInterface.printInfo(otherPlayer.getName() + " plays a " + card.getName());
                        } else {
                            hand.remove(card);
                            discard.add(card);
                            userInterface.printInfo(otherPlayer.getName() + " plays a " + card.getName());
                            if (Character.MOLLYSTARK.equals(otherPlayer.getCharacter())) {
                                Hand otherHand = otherPlayer.getHand();
                                otherHand.add(deck.pull());
                                userInterface.printInfo(otherPlayer.getName() + " draws a card");
                            }
                        }
                    }
                }
            }
            if (!skipDiscard) {
                discard.add(this);
            }
            return true;
        } else {
            if (!skipDiscard) {
                currentPlayer.getHand().add(this);
            }
            return false;
        }
    }
}
