package com.chriscarr.bang.cards;

public enum CardSuit {
    HEARTS("Hearts"),
    CLUBS("Clubs"),
    SPADES("Spades"),
    DIAMONDS("Diamonds");

    private final String label;

    CardSuit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
