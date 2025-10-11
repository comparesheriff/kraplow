package com.chriscarr.bang;

import com.chriscarr.infra.testing.DeterministicRng;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DeterministicRng(seed = 7)
class PlayerRefUtilTest {
    @Test
    @Timeout(1)
    void of_and_resolve_roundtrip() {
        List<Player> players = Setup.getPlayers(4);
        Player p2 = players.get(1);
        PlayerRef ref = PlayerRefUtil.of(p2);
        assertTrue(PlayerRefUtil.resolveById(players, ref).isPresent());
        assertSame(p2, PlayerRefUtil.resolveById(players, ref).orElseThrow());
    }

    @Test
    @Timeout(1)
    void resolve_handles_missing() {
        List<Player> players = Setup.getPlayers(3);
        assertTrue(PlayerRefUtil.resolveById(players, new PlayerRef(999)).isEmpty());
    }
}