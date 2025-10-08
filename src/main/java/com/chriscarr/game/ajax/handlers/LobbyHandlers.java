package com.chriscarr.game.ajax.handlers;

import com.chriscarr.game.WebGame;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.ajax.dto.AvailableGameDto;
import com.chriscarr.game.ajax.dto.CountPlayersDto;
import com.chriscarr.game.http.ParamUtil;
import com.chriscarr.game.xml.LobbyXmlWriter;

import java.util.ArrayList;
import java.util.List;

public final class LobbyHandlers {
    private LobbyHandlers() {
    }

    public static AjaxAction availableGames() {
        return (_, response) -> {
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
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            if (gameId == null) {
                return;
            }
            int count = WebGame.getCountPlayers(gameId);
            List<String> players = WebGame.getJoinedPlayers(gameId);
            LobbyXmlWriter.writeCountPlayers(new CountPlayersDto(count, players), response);
        };
    }

    public static AjaxAction getGuestCounter() {
        return (_, response) -> LobbyXmlWriter.writeGuestCounter(WebGame.getNextGuestCounter(), response);
    }

    public static AjaxAction canStart() {
        return (request, response) -> {
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            if (gameId == null) {
                return;
            }
            LobbyXmlWriter.writeCanStart(WebGame.canStart(gameId), response);
        };
    }
}
