package com.chriscarr.bang.play.impl;

import com.chriscarr.bang.EndOfGameException;
import com.chriscarr.bang.Player;
import com.chriscarr.bang.play.*;
import com.chriscarr.bang.services.GameOverService;
import com.chriscarr.bang.turn.TurnContext;

import java.util.List;
import java.util.Objects;

public class CommandPlayLoop implements PlayLoop {
    private final PlayParser parser;
    private final PlayResolver resolver;
    private final PlayValidator validator;

    public CommandPlayLoop(PlayParser parser, PlayResolver resolver, PlayValidator validator) {
        this.parser = Objects.requireNonNull(parser);
        this.resolver = Objects.requireNonNull(resolver);
        this.validator = Objects.requireNonNull(validator);
    }

    @Override
    public void run(TurnContext ctx) {
        List<Player> players = ctx.players();
        while (!ctx.api().checkDonePlaying() && players.contains(ctx.currentPlayer())) {
            PlayCommand cmd = parser.parse(ctx);
            ValidationResult result = validator.validate(ctx, cmd);
            if (result.valid()) {
                resolver.resolve(ctx, cmd);
            } else if (result.reason() != null) {
                ctx.ui().printInfo(result.reason());
            }
            if (GameOverService.isGameOver(players)) {
                ctx.ui().printInfo("Winners are " + GameOverService.getWinners(players) + " " + GameOverService.revealRolesOnGameEnd(players));
                throw new EndOfGameException("Game over");
            }
        }
    }
}
