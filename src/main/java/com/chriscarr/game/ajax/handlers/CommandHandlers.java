package com.chriscarr.game.ajax.handlers;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Role;
import com.chriscarr.game.WebGame;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.http.ParamUtil;
import com.chriscarr.game.xml.GenericXmlWriter;

public final class CommandHandlers {
    private CommandHandlers() {
    }

    public static AjaxAction createGame() {
        return (request, response) -> {
            String visibility = request.getParameter("visibility");
            boolean sidestep = request.getParameterMap().containsKey("sidestep");
            int gameId = WebGame.create(visibility, sidestep);
            response.getWriter().write("<gameid>");
            response.getWriter().write(gameId);
            response.getWriter().write("</gameid>");
        };
    }

    public static AjaxAction startGame() {
        return (request, response) -> {
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            Integer aiSleepMs = ParamUtil.intParamOr400(request, response, "aiSleepMs");
            if (gameId == null || aiSleepMs == null) {
                return;
            }
            Role role = Role.valueOf(request.getParameter("prole"));
            Character character = Character.valueOf(request.getParameter("pchar"));
            WebGame.start(gameId, aiSleepMs, role, character);
            GenericXmlWriter.writeOk(response);
        };
    }
}
