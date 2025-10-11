package com.chriscarr.bang.play;

import com.chriscarr.bang.PlayerRef;
import com.chriscarr.bang.cards.Card;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record PlayCommand(
    Type type,
    PlayerRef sourcePlayer,
    Card playedCard,
    List<String> targetPlayers,
    Map<String, String> params
) {
    public enum Type {
        PASS,
        PLAY_CARD,
        ABILITY,
        DISCARD,
        EQUIP,
        UNKNOWN
    }

    public PlayCommand {
        Objects.requireNonNull(type, "type must not be null");
        targetPlayers = List.copyOf(targetPlayers == null ? Collections.emptyList() : targetPlayers);
        params = Map.copyOf(params == null ? Collections.emptyMap() : params);
    }

    public static PlayCommand pass(PlayerRef sourcePlayer) {
        return new PlayCommand(Type.PASS, sourcePlayer, null, Collections.emptyList(), Collections.emptyMap());
    }

    public static PlayCommand unknown(PlayerRef sourcePlayer) {
        return new PlayCommand(Type.UNKNOWN, sourcePlayer, null, Collections.emptyList(), Collections.emptyMap());
    }
}
