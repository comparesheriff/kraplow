package com.chriscarr.bang.cards;

@FunctionalInterface
public interface CardFactory {
    Card createCard(CardName name, CardSuit suit, CardValue value, CardType type);
}
