package com.chriscarr.bang.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class BangDeck {

  // <editor-fold> BASE DECK INITIALIZATION
  private static final Row[] BASE_ROWS =
          new Row[]{
                  // blue
                  new Row(CardName.BARREL, CardSuit.SPADES, CardValue.QUEEN),
                  new Row(CardName.SCOPE, CardSuit.SPADES, CardValue.ACE),
                  new Row(CardName.MUSTANG, CardSuit.HEARTS, CardValue.EIGHT),
                  new Row(CardName.MUSTANG, CardSuit.HEARTS, CardValue.NINE),
                  new Row(CardName.JAIL, CardSuit.SPADES, CardValue.JACK),
                  new Row(CardName.JAIL, CardSuit.SPADES, CardValue.TEN),
                  new Row(CardName.JAIL, CardSuit.HEARTS, CardValue.FOUR),
                  new Row(CardName.DYNAMITE, CardSuit.HEARTS, CardValue.TWO),
                  new Row(CardName.SCHOFIELD, CardSuit.SPADES, CardValue.KING),
                  new Row(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.JACK),
                  new Row(CardName.SCHOFIELD, CardSuit.CLUBS, CardValue.QUEEN),
                  new Row(CardName.VOLCANIC, CardSuit.SPADES, CardValue.TEN),
                  new Row(CardName.VOLCANIC, CardSuit.CLUBS, CardValue.TEN),
                  new Row(CardName.REMINGTON, CardSuit.CLUBS, CardValue.KING),
                  new Row(CardName.WINCHESTER, CardSuit.SPADES, CardValue.EIGHT),
                  new Row(CardName.BARREL, CardSuit.SPADES, CardValue.KING),
                  new Row(CardName.REV_CARBINE, CardSuit.CLUBS, CardValue.ACE),

                  // brown
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.SEVEN),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.EIGHT),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.NINE),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.THREE),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.KING),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.SIX),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.TEN),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.FOUR),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.ACE),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.QUEEN),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.FIVE),
                  new Row(CardName.BANG, CardSuit.HEARTS, CardValue.ACE),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.NINE),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.FIVE),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.SIX),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.TWO),
                  new Row(CardName.BANG, CardSuit.HEARTS, CardValue.QUEEN),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.JACK),
                  new Row(CardName.BANG, CardSuit.HEARTS, CardValue.KING),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.EIGHT),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.FOUR),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.TWO),
                  new Row(CardName.BANG, CardSuit.SPADES, CardValue.ACE),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.THREE),
                  new Row(CardName.BANG, CardSuit.DIAMONDS, CardValue.SEVEN),

                  // brown continued
                  new Row(CardName.MISSED, CardSuit.SPADES, CardValue.SEVEN),
                  new Row(CardName.MISSED, CardSuit.SPADES, CardValue.THREE),
                  new Row(CardName.MISSED, CardSuit.SPADES, CardValue.FIVE),
                  new Row(CardName.MISSED, CardSuit.CLUBS, CardValue.KING),
                  new Row(CardName.MISSED, CardSuit.CLUBS, CardValue.ACE),
                  new Row(CardName.MISSED, CardSuit.SPADES, CardValue.EIGHT),
                  new Row(CardName.MISSED, CardSuit.CLUBS, CardValue.JACK),
                  new Row(CardName.MISSED, CardSuit.CLUBS, CardValue.QUEEN),
                  new Row(CardName.MISSED, CardSuit.SPADES, CardValue.SIX),
                  new Row(CardName.MISSED, CardSuit.CLUBS, CardValue.TEN),
                  new Row(CardName.MISSED, CardSuit.SPADES, CardValue.TWO),
                  new Row(CardName.MISSED, CardSuit.SPADES, CardValue.FOUR),
                  new Row(CardName.BEER, CardSuit.HEARTS, CardValue.SIX),
                  new Row(CardName.BEER, CardSuit.HEARTS, CardValue.SEVEN),
                  new Row(CardName.BEER, CardSuit.HEARTS, CardValue.EIGHT),
                  new Row(CardName.BEER, CardSuit.HEARTS, CardValue.NINE),
                  new Row(CardName.BEER, CardSuit.HEARTS, CardValue.TEN),
                  new Row(CardName.BEER, CardSuit.HEARTS, CardValue.JACK),
                  new Row(CardName.PANIC, CardSuit.DIAMONDS, CardValue.EIGHT),
                  new Row(CardName.PANIC, CardSuit.HEARTS, CardValue.JACK),
                  new Row(CardName.PANIC, CardSuit.HEARTS, CardValue.QUEEN),
                  new Row(CardName.PANIC, CardSuit.HEARTS, CardValue.ACE),
                  new Row(CardName.CAT_BALOU, CardSuit.DIAMONDS, CardValue.TEN),
                  new Row(CardName.CAT_BALOU, CardSuit.DIAMONDS, CardValue.NINE),
                  new Row(CardName.CAT_BALOU, CardSuit.DIAMONDS, CardValue.JACK),
                  new Row(CardName.CAT_BALOU, CardSuit.HEARTS, CardValue.KING),
                  new Row(CardName.DUEL, CardSuit.CLUBS, CardValue.EIGHT),
                  new Row(CardName.DUEL, CardSuit.DIAMONDS, CardValue.QUEEN),
                  new Row(CardName.DUEL, CardSuit.SPADES, CardValue.JACK),
                  new Row(CardName.STAGECOACH, CardSuit.SPADES, CardValue.NINE),
                  new Row(CardName.STAGECOACH, CardSuit.SPADES, CardValue.NINE),
                  new Row(CardName.INDIANS, CardSuit.DIAMONDS, CardValue.KING),
                  new Row(CardName.INDIANS, CardSuit.DIAMONDS, CardValue.ACE),
                  new Row(CardName.GENERAL_STORE, CardSuit.SPADES, CardValue.QUEEN),
                  new Row(CardName.GENERAL_STORE, CardSuit.SPADES, CardValue.QUEEN),
                  new Row(CardName.GATLING, CardSuit.HEARTS, CardValue.TEN),
                  new Row(CardName.SALOON, CardSuit.HEARTS, CardValue.FIVE),
                  new Row(CardName.WELLS_FARGO, CardSuit.HEARTS, CardValue.THREE)
          };
  // </editor-fold>

  // <editor-fold> SIDESTEP DECK INITIALIZATION
  private static final Row[] SIDESTEP_ROWS =
          new Row[]{
                  // Blue
                  new Row(CardName.REMINGTON, CardSuit.DIAMONDS, CardValue.SIX),
                  new Row(CardName.REV_CARBINE, CardSuit.SPADES, CardValue.FIVE),
                  new Row(CardName.BARREL, CardSuit.CLUBS, CardValue.ACE),
                  new Row(CardName.DYNAMITE, CardSuit.CLUBS, CardValue.TEN),
                  new Row(CardName.MUSTANG, CardSuit.HEARTS, CardValue.FIVE),
                  new Row(CardName.SILVER, CardSuit.DIAMONDS, CardValue.TEN),
                  new Row(CardName.HIDEOUT, CardSuit.DIAMONDS, CardValue.KING),
                  new Row(CardName.HIDEOUT, CardSuit.DIAMONDS, CardValue.KING),

                  // Green
                  new Row(CardName.CONESTOGA, CardSuit.DIAMONDS, CardValue.NINE),
                  new Row(CardName.BUFFALO_RIFLE, CardSuit.CLUBS, CardValue.QUEEN),
                  new Row(CardName.CAN_CAN, CardSuit.CLUBS, CardValue.JACK),
                  new Row(CardName.HOWITZER, CardSuit.SPADES, CardValue.NINE),
                  new Row(CardName.CANTEEN, CardSuit.HEARTS, CardValue.SEVEN),
                  new Row(CardName.KNIFE, CardSuit.HEARTS, CardValue.EIGHT),
                  new Row(CardName.PEPPERBOX, CardSuit.CLUBS, CardValue.TEN),
                  new Row(CardName.DERRINGER, CardSuit.SPADES, CardValue.SEVEN),
                  new Row(CardName.PONY_EXPRESS, CardSuit.DIAMONDS, CardValue.QUEEN),
                  new Row(CardName.SOMBRERO, CardSuit.CLUBS, CardValue.SEVEN),
                  new Row(CardName.BIBLE, CardSuit.HEARTS, CardValue.TEN),
                  new Row(CardName.IRON_PLATE, CardSuit.DIAMONDS, CardValue.ACE),
                  new Row(CardName.IRON_PLATE, CardSuit.SPADES, CardValue.QUEEN),
                  new Row(CardName.TEN_GALLON_HAT, CardSuit.DIAMONDS, CardValue.JACK),

                  // Brown
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.FIVE),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.KING),
                  new Row(CardName.BANG, CardSuit.CLUBS, CardValue.SIX),
                  new Row(CardName.BANG, CardSuit.SPADES, CardValue.EIGHT),
                  new Row(CardName.MISSED, CardSuit.DIAMONDS, CardValue.EIGHT),
                  new Row(CardName.BEER, CardSuit.HEARTS, CardValue.SIX),
                  new Row(CardName.BEER, CardSuit.SPADES, CardValue.SIX),
                  new Row(CardName.PANIC, CardSuit.HEARTS, CardValue.JACK),
                  new Row(CardName.CAT_BALOU, CardSuit.CLUBS, CardValue.EIGHT),
                  new Row(CardName.INDIANS, CardSuit.DIAMONDS, CardValue.FIVE),
                  new Row(CardName.RAG_TIME, CardSuit.HEARTS, CardValue.NINE),
                  new Row(CardName.DODGE, CardSuit.DIAMONDS, CardValue.SEVEN),
                  new Row(CardName.DODGE, CardSuit.HEARTS, CardValue.KING),
                  new Row(CardName.WHISKY, CardSuit.HEARTS, CardValue.QUEEN),
                  new Row(CardName.PUNCH, CardSuit.SPADES, CardValue.TEN),
                  new Row(CardName.TEQUILA, CardSuit.CLUBS, CardValue.NINE),
                  new Row(CardName.BRAWL, CardSuit.SPADES, CardValue.JACK),
                  new Row(CardName.SPRINGFIELD, CardSuit.SPADES, CardValue.JACK),
          };

  // </editor-fold>

  private record Row(CardName name, CardSuit suit, CardValue value) {
  }

  public static ArrayList<Card> makeDeck() {
    return build(BASE_ROWS);
  }

  public static ArrayList<Card> makeSidestepDeck() {
    return build(SIDESTEP_ROWS);
  }

  private static ArrayList<Card> build(Row[] rows) {
    return Arrays.stream(rows)
            .map(row -> row.name.newCard(row.suit, row.value))
            .collect(Collectors.toCollection(() -> new ArrayList<>(rows.length)));
  }
}
