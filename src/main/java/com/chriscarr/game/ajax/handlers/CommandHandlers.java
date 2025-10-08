package com.chriscarr.game.ajax.handlers;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Role;
import com.chriscarr.game.WebGame;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.xml.MessageXmlWriter;

public final class CommandHandlers {
    private CommandHandlers() {
    }

    public static AjaxAction createGame() {
        return (request, response) -> {
            String visibility = request.getParameter("visibility");
            boolean sidestep = request.getParameterMap().containsKey("sidestep");
            int gameId = WebGame.create(visibility, sidestep);
            response.getWriter().write("<gameid>");
            response.getWriter().write(Integer.toString(gameId));
            response.getWriter().write("</gameid>");
        };
    }

    public static AjaxAction startGame() {
        return (request, response) -> {
            String gameId = request.getParameter("gameId");
            String aiSleepMs = request.getParameter("aiSleepMs");
            Role role = Role.valueOf(request.getParameter("prole"));
            com.chriscarr.bang.Character character = Character.valueOf(request.getParameter("pchar"));
            WebGame.start(Integer.parseInt(gameId), Integer.parseInt(aiSleepMs), role, character);
            MessageXmlWriter.writeOk(response);
        };
    }
}
