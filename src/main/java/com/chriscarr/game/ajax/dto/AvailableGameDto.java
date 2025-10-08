package com.chriscarr.game.ajax.dto;

import java.util.List;

public record AvailableGameDto(int gameId, int playerCount, boolean canJoin, List<String> playerHandles) {
}
