package java.com.chriscarr;

import java.com.chriscarr.bang.Player;

public class TestUserInterfaceSpecial extends TestUserInterface {


    @Override
    public int askPlay(Player player) {
        if (player.getHandSize() == 2) {
            return 2;
        } else {
            return -1;
        }
    }
}
