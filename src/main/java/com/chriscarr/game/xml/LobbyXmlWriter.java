package com.chriscarr.game.xml;

import com.chriscarr.game.ajax.dto.AvailableGameDto;
import com.chriscarr.game.ajax.dto.CountPlayersDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public final class LobbyXmlWriter {
    private LobbyXmlWriter() {
    }

    public static void writeAvailableGames(List<AvailableGameDto> games, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<gameids>");
        for (AvailableGameDto game : games) {
            resp.getWriter().write("<game>");
            resp.getWriter().write("<gameid>");
            resp.getWriter().write(Integer.toString(game.gameId()));
            resp.getWriter().write("</gameid>");
            resp.getWriter().write("<playercount>");
            resp.getWriter().write(Integer.toString(game.playerCount()));
            resp.getWriter().write("</playercount>");
            resp.getWriter().write("<canjoin>");
            resp.getWriter().write(Boolean.toString(game.canJoin()));
            resp.getWriter().write("</canjoin>");
            resp.getWriter().write("<players>");
            for (String playerHandle : game.playerHandles()) {
                resp.getWriter().write("<playerName>");
                resp.getWriter().write(XmlUtil.escapeXml(playerHandle));
                resp.getWriter().write("</playerName>");
            }
            resp.getWriter().write("</players>");
            resp.getWriter().write("</game>");
        }
        resp.getWriter().write("</gameids>");
    }

    public static void writeCountPlayers(CountPlayersDto dto, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<count>");
        resp.getWriter().write("<playercount>");
        resp.getWriter().write(Integer.toString(dto.playerCount()));
        resp.getWriter().write("</playercount>");
        resp.getWriter().write("<players>");
        List<String> joinedPlayers = dto.playerHandles();
        for (String playerHandle : joinedPlayers) {
            resp.getWriter().write("<playerName>");
            resp.getWriter().write(XmlUtil.escapeXml(playerHandle));
            resp.getWriter().write("</playerName>");
        }
        resp.getWriter().write("</players>");
        resp.getWriter().write("</count>");
    }

    public static void writeGuestCounter(int guestCounter, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<guestcounter>");
        resp.getWriter().write(Integer.toString(guestCounter));
        resp.getWriter().write("</guestcounter>");
    }

    public static void writeCanStart(boolean canStart, HttpServletResponse resp) throws IOException {
        resp.getWriter().write(canStart ? "<yes/>" : "<no/>");
    }
}
