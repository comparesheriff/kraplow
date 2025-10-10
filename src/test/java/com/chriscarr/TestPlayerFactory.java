package com.chriscarr;

import com.chriscarr.bang.*;
import com.chriscarr.bang.Character;

public class TestPlayerFactory {
    /* ------------ Helfer ------------ */
    public static Player mkPlayer(Character c, int hp) {
        Player p = new Player();
        p.setCharacter(c);
        p.setHand(new Hand());
        p.setInPlay(new CardsInPlay());
        p.setMaxHealth(hp);
        p.setHealth(hp);
        return p;
    }

    public static Player mkPlayer(Character c, int hp, Role r) {
        Player p = mkPlayer(c, hp);
        p.setRole(r);
        return p;
    }
}
