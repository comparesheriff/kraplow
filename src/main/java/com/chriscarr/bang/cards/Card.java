package com.chriscarr.bang.cards;

import com.chriscarr.bang.*;
import com.chriscarr.bang.models.game.Character;
import com.chriscarr.bang.userinterface.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class Card implements Playable {

    public static final int HEARTS = 0;
    public static final int CLUBS = 1;
    public static final int SPADES = 2;
    public static final int DIAMONDS = 3;
    public static final int VALUE2 = 0;
    public static final int VALUE3 = 1;
    public static final int VALUE4 = 2;
    public static final int VALUE5 = 3;
    public static final int VALUE6 = 4;
    public static final int VALUE7 = 5;
    public static final int VALUE8 = 6;
    public static final int VALUE9 = 7;
    public static final int VALUE10 = 8;
    public static final int VALUEJ = 9;
    public static final int VALUEQ = 10;
    public static final int VALUEK = 11;
    public static final int VALUEA = 12;
    public static final int TYPEGUN = 0;
    public static final int TYPEITEM = 1;
    public static final int TYPEPLAY = 2;
    public static final int TYPESINGLEUSEITEM = 3;
    public static final String CARDBARREL = "Barrel";
    public static final String CARDSCOPE = "Scope";
    public static final String CARDMUSTANG = "Mustang";
    public static final String CARDJAIL = "Jail";
    public static final String CARDDYNAMITE = "Dynamite";
    public static final String CARDSCHOFIELD = "Schofield";
    public static final String CARDVOLCANIC = "Volcanic";
    public static final String CARDREMINGTON = "Remington";
    public static final String CARDWINCHESTER = "Winchester";
    public static final String CARDREVCARBINE = "Rev. Carbine";
    public static final String CARDBANG = "Shoot";
    public static final String CARDMISSED = "Missed!";
    public static final String CARDBEER = "Beer";
    public static final String CARDPANIC = "Panic!";
    public static final String CARDCATBALOU = "Cat Balou";
    public static final String CARDDUEL = "Duel";
    public static final String CARDSTAGECOACH = "Stagecoach";
    public static final String CARDINDIANS = "Indians!";
    public static final String CARDGENERALSTORE = "General Store";
    public static final String CARDGATLING = "Gatling";
    public static final String CARDSALOON = "Saloon";
    public static final String CARDWELLSFARGO = "Wells Fargo";
    public static final String CARDRAGTIME = "Rag Time";
    public static final String CARDDODGE = "Dodge";
    public static final String CARDWHISKY = "Whisky";
    public static final String CARDHIDEOUT = "Hideout";
    public static final String CARDSILVER = "Silver";
    public static final String CARDPUNCH = "Punch";
    public static final String CARDBRAWL = "Brawl";
    public static final String CARDTEQUILA = "Tequila";
    public static final String CARDSPRINGFIELD = "Springfield";
    public static final String CARDCONESTOGA = "Conestoga";
    public static final String CARDBUFFALORIFLE = "Buffalo Rifle";
    public static final String CARDCANCAN = "Can Can";
    public static final String CARDHOWITZER = "Howitzer";
    public static final String CARDSOMBRERO = "Sombrero";
    public static final String CARDBIBLE = "Bible";
    public static final String CARDCANTEEN = "Canteen";
    public static final String CARDIRONPLATE = "Iron Plate";
    public static final String CARDKNIFE = "Knife";
    public static final String CARDPEPPERBOX = "Pepperbox";
    public static final String CARDDERRINGER = "Derringer";
    public static final String CARDTENGALLONHAT = "Ten Gallon Hat";
    public static final String CARDPONYEXPRESS = "Pony Express";


    private String name;
    private int suit;
    private int value;
    private int type;

    public Card() {

    }

    public Card(String name, int suit, int value, int type) {
        this.name = name;
        this.suit = suit;
        this.value = value;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }

    public int getSuit() {
        return suit;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static int getRange(String gunName) {
        switch (gunName) {
            case CARDREVCARBINE:
                return 5;
            case CARDWINCHESTER:
                return 4;
            case CARDREMINGTON:
                return 3;
            case CARDSCHOFIELD:
                return 2;
            default:
                return 1;
        }
    }

    public static boolean multiBang(String gunName) {
        return gunName.equals(CARDVOLCANIC);
    }

    public static boolean isExplode(Card drawnCard) {
        if (drawnCard.suit == SPADES) {
            return drawnCard.value < 8;
        }
        return false;
    }

    public static String suitToString(int suit) {
        if (suit == HEARTS) {
            return "Hearts";
        } else if (suit == CLUBS) {
            return "Clubs";
        } else if (suit == SPADES) {
            return "Spades";
        } else if (suit == DIAMONDS) {
            return "Diamonds";
        } else {
            throw new RuntimeException("Invalid Suit");
        }
    }

    public static String valueToString(int value) {
        if (value == VALUE2) {
            return "2";
        } else if (value == VALUE3) {
            return "3";
        } else if (value == VALUE4) {
            return "4";
        } else if (value == VALUE5) {
            return "5";
        } else if (value == VALUE6) {
            return "6";
        } else if (value == VALUE7) {
            return "7";
        } else if (value == VALUE8) {
            return "8";
        } else if (value == VALUE9) {
            return "9";
        } else if (value == VALUE10) {
            return "10";
        } else if (value == VALUEJ) {
            return "J";
        } else if (value == VALUEQ) {
            return "Q";
        } else if (value == VALUEK) {
            return "K";
        } else if (value == VALUEA) {
            return "A";
        } else {
            throw new RuntimeException("Invalid value");
        }
    }

    @Override
    public boolean canPlay(Player player, List<Player> players, int bangsPlayed) {
        return !player.isInPlay(name);
    }

    @Override
    public boolean play(Player currentPlayer, List<Player> players,
                        UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn) {
        if (Character.JOHNNY_KISCH.equals(currentPlayer.getCharacter())) {
            for (Player player : players) {
                int inPlayCount = player.getInPlay().count();
                for (int inPlayIndex = 0; inPlayIndex < inPlayCount; inPlayIndex++) {
                    Card peeked = player.getInPlay().peek(inPlayIndex);
                    if (peeked.getName().equals(this.getName())) {
                        Card removed = player.getInPlay().remove(inPlayIndex);
                        discardPile.add(removed);
                        userInterface.printInfo(currentPlayer.getName() + " plays a " + this.getName() + " and forces " + player.getName() + " to discard one from play.");
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

    public static String typeToString(int type) {
        if (TYPEGUN == type || TYPEITEM == type || TYPESINGLEUSEITEM == type) {
            return "Item";
        } else {
            return "Play";
        }
    }

    public boolean shoot(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn, boolean skipDiscard) {
        return shoot(currentPlayer, players, userInterface, deck, discardPile, turn, skipDiscard, null);
    }

    public boolean shoot(Player currentPlayer, List<Player> players, UserInterface userInterface, Deck deck, DiscardPile discardPile, Turn turn, boolean skipDiscard, Player targetPlayer) {
        Player otherPlayer;
        if (targetPlayer == null) {
            otherPlayer = Turn.getValidChosenPlayer(currentPlayer, targets(currentPlayer, players), userInterface);
        } else {
            otherPlayer = targetPlayer;
        }
        if (!(otherPlayer instanceof CancelPlayer)) {
            userInterface.printInfo(currentPlayer.getName() + " Shoots " + otherPlayer.getName());
            if (Character.APACHE_KID.equals(otherPlayer.getCharacter()) && this.getSuit() == Card.DIAMONDS) {
                userInterface.printInfo(otherPlayer.getName() + " is unaffected by diamond " + this.getName());
                if (!skipDiscard) {
                    discardPile.add(this);
                }
                return true;
            }
            int missesRequired = 1;
            if (this.getName().equals(Card.CARDBANG) && Character.SLAB_THE_KILLER.equals(currentPlayer.getCharacter())) {
                missesRequired = 2;
            }
            int barrelMisses = Turn.isBarrelSave(otherPlayer, deck, discardPile, userInterface, missesRequired, currentPlayer);
            missesRequired = missesRequired - barrelMisses;
            boolean canPlaySingleUse = !Character.BELLE_STAR.equals(currentPlayer.getCharacter());
            if (missesRequired <= 0) {
                if (!skipDiscard) {
                    discardPile.add(this);
                }
                return true;
            } else if (missesRequired == 1) {
                int missPlayed = Turn.validPlayMiss(otherPlayer, userInterface, canPlaySingleUse);
                if (missPlayed == -1) {
                    turn.damagePlayer(otherPlayer, players, currentPlayer, 1, currentPlayer, deck, discardPile, userInterface);
                    userInterface.printInfo(otherPlayer.getName() + " loses a health.");
                } else {
                    for (int i = 0; i < missesRequired; i++) {
                        if (missPlayed < otherPlayer.getHand().size()) {
                            Card missCard = otherPlayer.getHand().remove(missPlayed);
                            discardPile.add(missCard);
                            if (missCard.getName().equals(CARDDODGE)) {
                                Hand otherHand = otherPlayer.getHand();
                                otherHand.add(deck.pull());
                                userInterface.printInfo(otherPlayer.getName() + " plays a " + missCard.getName() + " countering " + currentPlayer.getName() + "'s " + Card.CARDBANG + " and draws a card");
                            } else {
                                if (missCard.getName().equals(Card.CARDMISSED)) {
                                    userInterface.printInfo(otherPlayer.getName() + " plays a " + missCard.getName());
                                } else {
                                    userInterface.printInfo(otherPlayer.getName() + " plays a " + missCard.getName() + " as a Missed!");
                                }
                            }
                            if (Character.MOLLY_STARK.equals(otherPlayer.getCharacter())) {
                                Hand otherHand = otherPlayer.getHand();
                                otherHand.add(deck.pull());
                                userInterface.printInfo(otherPlayer.getName() + " draws a card");
                            }
                        } else {
                            missPlayed -= otherPlayer.getHand().size();
                            InPlay inPlay = otherPlayer.getInPlay();
                            SingleUseMissed sum = (SingleUseMissed) inPlay.remove(missPlayed);
                            if (sum.getName().equals(Card.CARDBIBLE)) {
                                otherPlayer.getHand().add(deck.pull());
                            }
                            userInterface.printInfo(otherPlayer.getName() + " plays a " + sum.getName());
                            discardPile.add(sum);
                        }
                    }
                }
            } else if (missesRequired == 2) {
                Hand hand = otherPlayer.getHand();
                InPlay inPlay = otherPlayer.getInPlay();
                List<Card> cardsToDiscard;
                cardsToDiscard = Turn.validRespondTwoMiss(otherPlayer, userInterface);
                if (cardsToDiscard.size() == 0) {
                    turn.damagePlayer(otherPlayer, players, currentPlayer, 1, currentPlayer, deck, discardPile, userInterface);
                    userInterface.printInfo(otherPlayer.getName() + " loses a health.");
                } else {
                    //TODO issue here, can select more than 2 cards. Green card and missed locks up game.
                    for (Card card : cardsToDiscard) {
                        if (inPlay.hasItem(card.getName())) {
                            for (int i = 0; i < inPlay.size(); i++) {
                                Card gotCard = inPlay.get(i);
                                if (gotCard.getName().equals(card.getName())) {
                                    discardPile.add(inPlay.remove(i));
                                }
                            }
                            userInterface.printInfo(otherPlayer.getName() + " plays a " + card.getName());
                        } else {
                            hand.remove(card);
                            discardPile.add(card);
                            userInterface.printInfo(otherPlayer.getName() + " plays a " + card.getName());
                            if (Character.MOLLY_STARK.equals(otherPlayer.getCharacter())) {
                                Hand otherHand = otherPlayer.getHand();
                                otherHand.add(deck.pull());
                                userInterface.printInfo(otherPlayer.getName() + " draws a card");
                            }
                        }
                    }
                }
            }
            if (!skipDiscard) {
                discardPile.add(this);
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
