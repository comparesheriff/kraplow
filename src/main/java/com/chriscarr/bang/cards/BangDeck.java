package com.chriscarr.bang.cards;

import java.util.ArrayList;


public class BangDeck {
    public static ArrayList<Card> makeDeck() {
        ArrayList<Card> cards = new ArrayList<>();
        //Blue cards
        cards.add(new Card(Card.CARDBARREL, CardSuit.SPADES, CardValue.QUEEN, CardType.ITEM));
        cards.add(new Card(Card.CARDSCOPE, CardSuit.SPADES, CardValue.ACE, CardType.ITEM));
        cards.add(new Card(Card.CARDMUSTANG, CardSuit.HEARTS, CardValue.EIGHT, CardType.ITEM));
        cards.add(new Card(Card.CARDMUSTANG, CardSuit.HEARTS, CardValue.NINE, CardType.ITEM));
        cards.add(new Jail(Card.CARDJAIL, CardSuit.SPADES, CardValue.JACK, CardType.ITEM));
        cards.add(new Jail(Card.CARDJAIL, CardSuit.SPADES, CardValue.TEN, CardType.ITEM));
        cards.add(new Jail(Card.CARDJAIL, CardSuit.HEARTS, CardValue.FOUR, CardType.ITEM));
        cards.add(new Card(Card.CARDDYNAMITE, CardSuit.HEARTS, CardValue.TWO, CardType.ITEM));
        cards.add(new Gun(Card.CARDSCHOFIELD, CardSuit.SPADES, CardValue.KING, CardType.GUN));
        cards.add(new Gun(Card.CARDSCHOFIELD, CardSuit.CLUBS, CardValue.JACK, CardType.GUN));
        cards.add(new Gun(Card.CARDSCHOFIELD, CardSuit.CLUBS, CardValue.QUEEN, CardType.GUN));
        cards.add(new Gun(Card.CARDVOLCANIC, CardSuit.SPADES, CardValue.TEN, CardType.GUN));
        cards.add(new Gun(Card.CARDVOLCANIC, CardSuit.CLUBS, CardValue.TEN, CardType.GUN));
        cards.add(new Gun(Card.CARDREMINGTON, CardSuit.CLUBS, CardValue.KING, CardType.GUN));
        cards.add(new Gun(Card.CARDWINCHESTER, CardSuit.SPADES, CardValue.EIGHT, CardType.GUN));
        cards.add(new Card(Card.CARDBARREL, CardSuit.SPADES, CardValue.KING, CardType.ITEM));
        cards.add(new Gun(Card.CARDREVCARBINE, CardSuit.CLUBS, CardValue.ACE, CardType.GUN));
        //Brown cards
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.SEVEN, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.EIGHT, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.NINE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.THREE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.KING, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.SIX, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.TEN, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.FOUR, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.ACE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.QUEEN, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.FIVE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.HEARTS, CardValue.ACE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.NINE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.FIVE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.SIX, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.TWO, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.HEARTS, CardValue.QUEEN, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.JACK, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.HEARTS, CardValue.KING, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.EIGHT, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.FOUR, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.TWO, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.SPADES, CardValue.ACE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.THREE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.DIAMONDS, CardValue.SEVEN, CardType.PLAY));

        cards.add(new Missed(Card.CARDMISSED, CardSuit.SPADES, CardValue.SEVEN, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.SPADES, CardValue.THREE, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.SPADES, CardValue.FIVE, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.CLUBS, CardValue.KING, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.CLUBS, CardValue.ACE, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.SPADES, CardValue.EIGHT, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.CLUBS, CardValue.JACK, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.CLUBS, CardValue.QUEEN, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.SPADES, CardValue.SIX, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.CLUBS, CardValue.TEN, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.SPADES, CardValue.TWO, CardType.PLAY));
        cards.add(new Missed(Card.CARDMISSED, CardSuit.SPADES, CardValue.FOUR, CardType.PLAY));

        cards.add(new Beer(Card.CARDBEER, CardSuit.HEARTS, CardValue.SIX, CardType.PLAY));
        cards.add(new Beer(Card.CARDBEER, CardSuit.HEARTS, CardValue.SEVEN, CardType.PLAY));
        cards.add(new Beer(Card.CARDBEER, CardSuit.HEARTS, CardValue.EIGHT, CardType.PLAY));
        cards.add(new Beer(Card.CARDBEER, CardSuit.HEARTS, CardValue.NINE, CardType.PLAY));
        cards.add(new Beer(Card.CARDBEER, CardSuit.HEARTS, CardValue.TEN, CardType.PLAY));
        cards.add(new Beer(Card.CARDBEER, CardSuit.HEARTS, CardValue.JACK, CardType.PLAY));

        cards.add(new Panic(Card.CARDPANIC, CardSuit.DIAMONDS, CardValue.EIGHT, CardType.PLAY));
        cards.add(new Panic(Card.CARDPANIC, CardSuit.HEARTS, CardValue.JACK, CardType.PLAY));
        cards.add(new Panic(Card.CARDPANIC, CardSuit.HEARTS, CardValue.QUEEN, CardType.PLAY));
        cards.add(new Panic(Card.CARDPANIC, CardSuit.HEARTS, CardValue.ACE, CardType.PLAY));

        cards.add(new CatBalou(Card.CARDCATBALOU, CardSuit.DIAMONDS, CardValue.TEN, CardType.PLAY));
        cards.add(new CatBalou(Card.CARDCATBALOU, CardSuit.DIAMONDS, CardValue.NINE, CardType.PLAY));
        cards.add(new CatBalou(Card.CARDCATBALOU, CardSuit.DIAMONDS, CardValue.JACK, CardType.PLAY));
        cards.add(new CatBalou(Card.CARDCATBALOU, CardSuit.HEARTS, CardValue.KING, CardType.PLAY));

        cards.add(new Duel(Card.CARDDUEL, CardSuit.CLUBS, CardValue.EIGHT, CardType.PLAY));
        cards.add(new Duel(Card.CARDDUEL, CardSuit.DIAMONDS, CardValue.QUEEN, CardType.PLAY));
        cards.add(new Duel(Card.CARDDUEL, CardSuit.SPADES, CardValue.JACK, CardType.PLAY));

        cards.add(new Stagecoach(Card.CARDSTAGECOACH, CardSuit.SPADES, CardValue.NINE, CardType.PLAY));
        cards.add(new Stagecoach(Card.CARDSTAGECOACH, CardSuit.SPADES, CardValue.NINE, CardType.PLAY));

        cards.add(new Indians(Card.CARDINDIANS, CardSuit.DIAMONDS, CardValue.KING, CardType.PLAY));
        cards.add(new Indians(Card.CARDINDIANS, CardSuit.DIAMONDS, CardValue.ACE, CardType.PLAY));

        cards.add(new GeneralStore(Card.CARDGENERALSTORE, CardSuit.SPADES, CardValue.QUEEN, CardType.PLAY));
        cards.add(new GeneralStore(Card.CARDGENERALSTORE, CardSuit.SPADES, CardValue.QUEEN, CardType.PLAY));

        cards.add(new Gatling(Card.CARDGATLING, CardSuit.HEARTS, CardValue.TEN, CardType.PLAY));

        cards.add(new Saloon(Card.CARDSALOON, CardSuit.HEARTS, CardValue.FIVE, CardType.PLAY));

        cards.add(new WellsFargo(Card.CARDWELLSFARGO, CardSuit.HEARTS, CardValue.THREE, CardType.PLAY));
        return cards;
    }

    public static ArrayList<Card> makeSidestepDeck() {
        ArrayList<Card> cards = new ArrayList<>();
        //Blue cards
        cards.add(new Gun(Card.CARDREMINGTON, CardSuit.DIAMONDS, CardValue.SIX, CardType.GUN));
        cards.add(new Gun(Card.CARDREVCARBINE, CardSuit.SPADES, CardValue.FIVE, CardType.GUN));
        cards.add(new Card(Card.CARDBARREL, CardSuit.CLUBS, CardValue.ACE, CardType.ITEM));
        cards.add(new Card(Card.CARDDYNAMITE, CardSuit.CLUBS, CardValue.TEN, CardType.ITEM));
        cards.add(new Card(Card.CARDMUSTANG, CardSuit.HEARTS, CardValue.FIVE, CardType.ITEM));
        cards.add(new Card(Card.CARDSILVER, CardSuit.DIAMONDS, CardValue.TEN, CardType.ITEM));
        cards.add(new Card(Card.CARDHIDEOUT, CardSuit.DIAMONDS, CardValue.KING, CardType.ITEM));
        cards.add(new Card(Card.CARDHIDEOUT, CardSuit.DIAMONDS, CardValue.KING, CardType.ITEM));

        //Green cards
        cards.add(new Conestoga(Card.CARDCONESTOGA, CardSuit.DIAMONDS, CardValue.NINE, CardType.SINGLE_USE_ITEM));
        cards.add(new BuffaloRifle(Card.CARDBUFFALORIFLE, CardSuit.CLUBS, CardValue.QUEEN, CardType.SINGLE_USE_ITEM));
        cards.add(new CanCan(Card.CARDCANCAN, CardSuit.CLUBS, CardValue.JACK, CardType.SINGLE_USE_ITEM));
        cards.add(new Howitzer(Card.CARDHOWITZER, CardSuit.SPADES, CardValue.NINE, CardType.SINGLE_USE_ITEM));
        cards.add(new Canteen(Card.CARDCANTEEN, CardSuit.HEARTS, CardValue.SEVEN, CardType.SINGLE_USE_ITEM));
        cards.add(new Knife(Card.CARDKNIFE, CardSuit.HEARTS, CardValue.EIGHT, CardType.SINGLE_USE_ITEM));
        cards.add(new Pepperbox(Card.CARDPEPPERBOX, CardSuit.CLUBS, CardValue.TEN, CardType.SINGLE_USE_ITEM));
        cards.add(new Derringer(Card.CARDDERRINGER, CardSuit.SPADES, CardValue.SEVEN, CardType.SINGLE_USE_ITEM));
        cards.add(new PonyExpress(Card.CARDPONYEXPRESS, CardSuit.DIAMONDS, CardValue.QUEEN, CardType.SINGLE_USE_ITEM));
        cards.add(new SingleUseMissed(Card.CARDSOMBRERO, CardSuit.CLUBS, CardValue.SEVEN, CardType.SINGLE_USE_ITEM));
        cards.add(new SingleUseMissed(Card.CARDBIBLE, CardSuit.HEARTS, CardValue.TEN, CardType.SINGLE_USE_ITEM));
        cards.add(new SingleUseMissed(Card.CARDIRONPLATE, CardSuit.DIAMONDS, CardValue.ACE, CardType.SINGLE_USE_ITEM));
        cards.add(new SingleUseMissed(Card.CARDIRONPLATE, CardSuit.SPADES, CardValue.QUEEN, CardType.SINGLE_USE_ITEM));
        cards.add(new SingleUseMissed(Card.CARDTENGALLONHAT, CardSuit.DIAMONDS, CardValue.JACK, CardType.SINGLE_USE_ITEM));

        //Brown cards
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.FIVE, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.KING, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.CLUBS, CardValue.SIX, CardType.PLAY));
        cards.add(new Bang(Card.CARDBANG, CardSuit.SPADES, CardValue.EIGHT, CardType.PLAY));

        cards.add(new Missed(Card.CARDMISSED, CardSuit.DIAMONDS, CardValue.EIGHT, CardType.PLAY));

        cards.add(new Beer(Card.CARDBEER, CardSuit.HEARTS, CardValue.SIX, CardType.PLAY));
        cards.add(new Beer(Card.CARDBEER, CardSuit.SPADES, CardValue.SIX, CardType.PLAY));

        cards.add(new Panic(Card.CARDPANIC, CardSuit.HEARTS, CardValue.JACK, CardType.PLAY));

        cards.add(new CatBalou(Card.CARDCATBALOU, CardSuit.CLUBS, CardValue.EIGHT, CardType.PLAY));

        cards.add(new Indians(Card.CARDINDIANS, CardSuit.DIAMONDS, CardValue.FIVE, CardType.PLAY));

        cards.add(new RagTime(Card.CARDRAGTIME, CardSuit.HEARTS, CardValue.NINE, CardType.PLAY));

        cards.add(new Dodge(Card.CARDDODGE, CardSuit.DIAMONDS, CardValue.SEVEN, CardType.PLAY));
        cards.add(new Dodge(Card.CARDDODGE, CardSuit.HEARTS, CardValue.KING, CardType.PLAY));

        cards.add(new Whisky(Card.CARDWHISKY, CardSuit.HEARTS, CardValue.QUEEN, CardType.PLAY));

        cards.add(new Punch(Card.CARDPUNCH, CardSuit.SPADES, CardValue.TEN, CardType.PLAY));

        cards.add(new Tequila(Card.CARDTEQUILA, CardSuit.CLUBS, CardValue.NINE, CardType.PLAY));

        cards.add(new Brawl(Card.CARDBRAWL, CardSuit.SPADES, CardValue.JACK, CardType.PLAY));

        cards.add(new Springfield(Card.CARDSPRINGFIELD, CardSuit.SPADES, CardValue.JACK, CardType.PLAY));

        return cards;
    }
}
