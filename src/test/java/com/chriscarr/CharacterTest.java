package com.chriscarr;

import com.chriscarr.bang.Character;
import junit.framework.TestCase;

public class CharacterTest extends TestCase {

    public void testCharacter() {
        assertEquals(Character.RANDOM, Character.valueOf("RANDOM"));
    }
}
