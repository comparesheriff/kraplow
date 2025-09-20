package java.com.chriscarr;

import java.util.ArrayList;
import java.util.List;

import java.com.chriscarr.bang.Hand;
import java.com.chriscarr.bang.Player;

public class TestUserInterfaceSpecial extends TestUserInterface {

	

      	@Override
	public int askPlay(Player player) {
                if(player.getHandSize() == 2){
                    return 2;
                } else {
                    return -1;
                }
	}
        
        @Override
	public List<Object> chooseTwoDiscardForLife(Player player) {
		List<Object> cards = new ArrayList<Object>();
		Hand hand = player.getHand();
		cards.add(hand.get(0));
		cards.add(hand.get(1));
		return cards;
	}
}
