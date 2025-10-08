package com.chriscarr.game.ajax.dto;

import java.util.List;

public record CountPlayersDto(int playerCount, List<String> playerHandles) {
}
