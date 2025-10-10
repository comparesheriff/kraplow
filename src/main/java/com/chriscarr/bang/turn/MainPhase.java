package com.chriscarr.bang.turn;

import com.chriscarr.bang.EndOfGameException;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.services.GameOverService;

import java.util.List;

class MainPhase implements TurnPhase {

    @Override
    public void carryOut(TurnContext ctx) {
        List<Player> players = ctx.players();
        while (!ctx.api().checkDonePlaying() && players.contains(ctx.currentPlayer())) {
            ctx.api().play(ctx);
            if (GameOverService.isGameOver(players)) {
                ctx.ui().printInfo("Winners are " + GameOverService.getWinners(players) + " " + GameOverService.revealRolesOnGameEnd(players));
                throw new EndOfGameException("Game over");
            }
        }
    }

}
