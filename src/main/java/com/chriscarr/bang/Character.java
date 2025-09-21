package com.chriscarr.bang;

import java.util.List;

public enum Character {
    CALAMITYJANET("Calamity Janet", "Shoots can be misses and misses can be shoots", 4),
    JOURDONNAIS("Jourdonnais", "Has a barrel at all times", 4),
    PAULREGRET("Paul Regret", "Has a mustang at all times", 3),
    KITCARLSON("Kit Carlson", "Draws 3 cards, returns 1 to deck", 4),
    BARTCASSIDY("Bart Cassidy", "When damaged draws from the deck", 4),
    JESSEJONES("Jesse Jones", "Can draw first card from other player or deck", 4),
    PEDRORAMIREZ("Pedro Ramirez", "Can draw first card from discard", 4),
    ELGRINGO("El Gringo", "Draws a card from the hand of the player that damaged him", 3),
    ROSEDOOLAN("Rose Doolan", "Has an Scope at all times", 4),
    SUZYLAFAYETTE("Suzy Lafayette", "Draws a card when hand is empty", 4),
    BLACKJACK("Black Jack", "Shows second draw card, if Diamond or Heart, draws another card", 4),
    SIDKETCHUM("Sid Ketchum", "Can discard 2 cards to gain 1 life", 4),
    SLABTHEKILLER("Slab the Killer", "2 misses required to cancel his Shoots", 4),
    LUCKYDUKE("Lucky Duke", "Chooses between 2 drawn cards instead of 1", 4),
    VULTURESAM("Vulture Sam", "Takes dead players cards", 4),
    WILLYTHEKID("Willy the Kid", "Not restricted to 1 Shoot", 4),
    UNCLEWILL("Uncle Will", "Any card can be a general store, once in a turn", 4),
    JOHNNYKISCH("Johnny Kisch", "When playing blue or green card, discard all cards in play with same name", 4),
    CLAUSTHESAINT("Claus The Saint", "Draw one card for every player plus one, everyone else gets 1", 3),
    CHUCKWENGAM("Chuck Wengam", "Change one life for 2 cards", 4),
    ELENAFUENTE("Elena Fuente", "Any card can be a miss", 3),
    BELLESTAR("Belle Star", "On her turn others cards do not work", 4),
    JOSEDELGADO("Jose Delgado", "On his turn discard blue card to draw 2", 4),
    GREGDIGGER("Greg Digger", "When other player killed, get 2 life", 4),
    DOCHOLYDAY("Doc Holyday", "On his turn, discard 2 cards to Shoot", 4),
    PIXIEPETE("Pixie Pete", "Draws 3 cards", 3),
    MOLLYSTARK("Molly Stark", "When playing cards out of turn, draw 1", 4),
    APACHEKID("Apache Kid", "Others diamond cards do not work", 3),
    HERBHUNTER("Herb Hunter", "When other dies, draw 2 cards", 4),
    BILLNOFACE("Bill Noface", "Draws 1 card and an extra for each missing health", 4),
    PATBRENNAN("Pat Brennan", "Can draw a card infront of another player", 4),
    TEQUILAJOE("Tequila Joe", "Beer gives him 2 life", 4),
    VERACUSTER("Vera Custer", "Copies abilities of others", 3),
    SEANMALLORY("Sean Mallory", "Has handsize limit of 10", 3),
    RANDOM("RANDOM", "RANDOM", 4);

    private final String name;
    private final String specialAbility;
    private final int startingHealth;

    public static final List<Character> CHARACTERS = List.of(CALAMITYJANET, JOURDONNAIS, PAULREGRET,
            KITCARLSON, BARTCASSIDY, JESSEJONES, PEDRORAMIREZ, ELGRINGO, ROSEDOOLAN, SUZYLAFAYETTE,
            BLACKJACK, SIDKETCHUM, SLABTHEKILLER, LUCKYDUKE, VULTURESAM, WILLYTHEKID, UNCLEWILL,
            JOHNNYKISCH, CLAUSTHESAINT);

    public static final List<Character> CHARACTERSSIDESTEP = List.of(CHUCKWENGAM, ELENAFUENTE,
            BELLESTAR, JOSEDELGADO, GREGDIGGER, DOCHOLYDAY, PIXIEPETE, MOLLYSTARK, APACHEKID,
            HERBHUNTER, BILLNOFACE, PATBRENNAN, TEQUILAJOE, VERACUSTER, SEANMALLORY);

    Character(String name, String specialAbility, int startingHealth) {
        this.name = name;
        this.specialAbility = specialAbility;
        this.startingHealth = startingHealth;
    }

    public String getName() {
        return name;
    }

    public String getSpecialAbilityText() {
        return specialAbility;
    }

    public int getStartingHealth() {
        return startingHealth;
    }
}
