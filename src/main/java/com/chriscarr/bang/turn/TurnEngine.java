package com.chriscarr.bang.turn;

import java.util.List;
import java.util.Objects;

public final class TurnEngine {
    private final List<TurnPhase> phases;

    public TurnEngine(List<TurnPhase> phases) {
        this.phases = Objects.requireNonNull(phases);
    }

    public void run(TurnContext ctx) {
        for (TurnPhase phase : phases) {
            phase.carryOut(ctx);
        }
    }
}
