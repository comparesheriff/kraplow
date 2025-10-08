package com.chriscarr.game.ajax.handlers;

import com.chriscarr.game.WebGame;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.ajax.dto.AvailableGameDto;
import com.chriscarr.game.ajax.dto.CountPlayersDto;
import com.chriscarr.game.xml.LobbyXmlWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LobbyHandlers {
    private LobbyHandlers() {
    }

    public static AjaxAction availableGames() {
        return (request, response) -> {
            List<Integer> availableGames = WebGame.getAvailableGames();
            ArrayList<AvailableGameDto> dtos = new ArrayList<>(availableGames.size());
            for (Integer gameId : availableGames) {
                int countPlayers = WebGame.getCountPlayers(gameId);
                boolean canJoin = WebGame.canJoin(gameId);
                List<String> players = WebGame.getJoinedPlayers(gameId);
                dtos.add(new AvailableGameDto(gameId, countPlayers, canJoin, players));
            }
            LobbyXmlWriter.writeAvailableGames(dtos, response);
        };
    }

    public static AjaxAction countPlayers() {
        return (request, response) -> {
            String gameId = request.getParameter("gameId");
            if (gameId != null && !gameId.equals("null")) {
                int count = WebGame.getCountPlayers(Integer.parseInt(gameId));
                List<String> players = WebGame.getJoinedPlayers(Integer.parseInt(gameId));
                LobbyXmlWriter.writeCountPlayers(new CountPlayersDto(count, players), response);
            } else {
                LobbyXmlWriter.writeCountPlayers(new CountPlayersDto(0, Collections.emptyList()), response);
            }
        };
    }

    public static AjaxAction getGuestCounter() {
        return (request, response) -> {
            LobbyXmlWriter.writeGuestCounter(WebGame.getNextGuestCounter(), response);
        };
    }

    public static AjaxAction canStart() {
        return (request, response) -> {
            String gameId = request.getParameter("gameId");
            LobbyXmlWriter.writeCanStart(WebGame.canStart(Integer.parseInt(gameId)), response);
        };
    }
}
