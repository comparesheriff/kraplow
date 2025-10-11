package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.EndOfGameException;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.play.PlayLoop;
import com.chriscarr.bang.services.GameOverService;
import com.chriscarr.bang.turn.TurnContext;

import java.util.List;

public class TurnApiPlayLoop implements PlayLoop {
    @Override
    public void run(TurnContext ctx) {
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
