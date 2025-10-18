package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.EndOfGameException;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.play.PlayLoop;
import com.chriscarr.bang.services.GameOverService;
import com.chriscarr.bang.turn.TurnContext;
import com.chriscarr.bang.turn.ports.PlayerControlPort;
import com.chriscarr.bang.turn.ports.TurnApiPorts;

import java.util.List;

public class PortsPlayLoop implements PlayLoop {
    @Override
    public void run(TurnContext ctx) {
        PlayerControlPort controlPort = TurnApiPorts.from(ctx.api());
        List<Player> players = ctx.players();
        while (!controlPort.checkDonePlaying() && players.contains(ctx.currentPlayer())) {
            controlPort.play(ctx);
            if (GameOverService.isGameOver(players)) {
                ctx.ui().printInfo("Winners are " + GameOverService.getWinners(players) + " " + GameOverService.revealRolesOnGameEnd(players));
                throw new EndOfGameException("Game over");
            }
        }
    }
}
