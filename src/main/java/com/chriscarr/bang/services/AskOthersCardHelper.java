package com.chriscarr.bang.services;

import com.chriscarr.bang.CardsInPlay;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.userinterface.UserInterface;

public final class AskOthersCardHelper {
    private AskOthersCardHelper() {
    }

    public static int askOthersCard(UserInterface ui, Player actor, Player target) {
        final CardsInPlay inPlay = target.getCardsInPlay();
        int chosen = -3;
        while (chosen < -2 || chosen > inPlay.size() - 1) {
            chosen = ui.askOthersCard(actor, inPlay, !target.getHand().isEmpty());
        }
        return chosen;
    }
}
