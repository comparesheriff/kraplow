package com.chriscarr.bang.models.game;

import java.util.ArrayList;
import java.util.List;

public enum Character {
    CALAMITY_JANET("Calamity Janet", "Shoots can be misses and misses can be shoots", false, 4),
    JOURDONNAIS("Jourdonnais", "Has a barrel at all times", false, 4),
    PAUL_REGRET("Paul Regret", "Has a mustang at all times", false, 3),
    KIT_CARLSON("Kit Carlson", "Draws 3 cards, returns 1 to deck", false, 4),
    BART_CASSIDY("Bart Cassidy", "When damaged draws from the deck", false, 4),
    JESSE_JONES("Jesse Jones", "Can draw first card from other player or deck", false, 4),
    PEDRO_RAMIREZ("Pedro Ramirez", "Can draw first card from discard", false, 4),
    EL_GRINGO("El Gringo", "Draws a card from the hand of the player that damaged him", false, 3),
    ROSE_DOOLAN("Rose Doolan", "Has an Scope at all times", false, 4),
    SUZY_LAFAYETTE("Suzy Lafayette", "Draws a card when hand is empty", false, 4),
    BLACK_JACK("Black Jack", "Shows second draw card, if Diamond or Heart, draws another card", false, 4),
    SID_KETCHUM("Sid Ketchum", "Can discard 2 cards to gain 1 life", false, 4),
    SLAB_THE_KILLER("Slab the Killer", "2 misses required to cancel his Shoots", false, 4),
    LUCKY_DUKE("Lucky Duke", "Chooses between 2 drawn cards instead of 1", false, 4),
    VULTURE_SAM("Vulture Sam", "Takes dead players cards", false, 4),
    WILLY_THE_KID("Willy the Kid", "Not restricted to 1 Shoot", false, 4),
    UNCLE_WILL("Uncle Will", "Any card can be a general store, once in a turn", false, 4),
    JOHNNY_KISCH("Johnny Kisch", "When playing blue or green card, discard all cards in play with same name", false, 4),
    CLAUS_THE_SAINT("Claus The Saint", "Draw one card for every player plus one, everyone else gets 1", false, 3),

    CHUCK_WENGAM("Chuck Wengam", "Change one life for 2 cards", true, 4),
    ELENA_FUENTE("Elena Fuente", "Any card can be a miss", true, 3),
    BELLE_STAR("Belle Star", "On her turn others cards do not work", true, 4),
    JOSE_DELGADO("Jose Delgado", "On his turn discard blue card to draw 2", true, 4),
    GREG_DIGGER("Greg Digger", "When other player killed, get 2 life", true, 4),
    DOC_HOLYDAY("Doc Holyday", "On his turn, discard 2 cards to Shoot", true, 4),
    PIXIE_PETE("Pixie Pete", "Draws 3 cards", true, 3),
    MOLLY_STARK("Molly Stark", "When playing cards out of turn, draw 1", true, 4),
    APACHE_KID("Apache Kid", "Others diamond cards do not work", true, 3),
    HERB_HUNTER("Herb Hunter", "When other dies, draw 2 cards", true, 4),
    BILL_NOFACE("Bill Noface", "Draws 1 card and an extra for each missing health", true, 4),
    PAT_BRENNAN("Pat Brennan", "Can draw a card infront of another player", true, 4),
    TEQUILA_JOE("Tequila Joe", "Beer gives him 2 life", true, 4),
    VERA_CUSTER("Vera Custer", "Copies abilities of others", true, 3),
    SEAN_MALLORY("Sean Mallory", "Has handsize limit of 10", true, 3),

    RANDOM("random", "random", true, 4);

    private final String characterName;
    private final String abilityText;
    private final boolean isSideStepCharacter;
    private final int startingHealth;

    Character(String characterName, String abilityText, boolean isSideStepCharacter, int startingHealth) {
        this.characterName = characterName;
        this.abilityText = abilityText;
        this.isSideStepCharacter = isSideStepCharacter;
        this.startingHealth = startingHealth;
    }

    public static List<Character> getCharacters(boolean includeSideStep) {
        List<Character> mainCharacters = new ArrayList<>();
        for (Character character : values()) {
            if (!character.isSideStepCharacter || includeSideStep) {
                mainCharacters.add(character);
            }
        }
        return mainCharacters;
    }

    public static Character getCharacter(String pChar) {
        for (Character character : values()) {
            if (character.characterName.equals(pChar)) {
                return character;
            }
        }
        return RANDOM;
    }

    public String characterName() {
        return characterName;
    }

    public String abilityText() {
        return abilityText;
    }

    public boolean isSideStepCharacter() {
        return isSideStepCharacter;
    }


    public int startingHealth() {
        return startingHealth;
    }
}
