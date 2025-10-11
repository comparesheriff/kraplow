package com.chriscarr.bang;

import java.util.Collection;
import java.util.Optional;

public final class PlayerRefUtil {
    private PlayerRefUtil() {
    }

    public static PlayerRef of(Player player) {
        return new PlayerRef(player.getId());
    }

    public static Optional<Player> resolveById(Collection<Player> players, PlayerRef ref) {
        if (ref == null) {return Optional.empty();}
        int id = ref.id();
        for (Player player : players) {
            if (player.getId() == id) {
                return Optional.of(player);
            }
        }
        return Optional.empty();
    }

}
