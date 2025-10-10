package com.chriscarr.bang.turn;

import java.util.List;

public final class NoopTurnApi {
    private NoopTurnApi() {
    }

    public static TurnApi get() {
        return TurnApi.of(
            (_, _, _) -> List.of(),
            (_, cs, _) -> cs.isEmpty() ? null : cs.getFirst(),
            (pl, _, _) -> pl,
            (pl, _) -> pl,
            (_, _, _, _) -> null,
            (_, _, _, _, _, _, _, _) -> {},
            () -> true,
            ctx -> {},
            done -> {}
        );
    }
}
