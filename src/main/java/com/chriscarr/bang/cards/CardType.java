package com.chriscarr.bang.cards;

public enum CardType {
    GUN("Gun type cards", "Item"),
    ITEM("Item type cards", "Item"),
    PLAY("Play type cards", "Play"),
    SINGLE_USE_ITEM("Single use item cards", "Item");

    private final String description;
    private final String typeName;

    CardType(String description, String typeName) {
        this.description = description;
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public String getTypeName() {
        return typeName;
    }
}
