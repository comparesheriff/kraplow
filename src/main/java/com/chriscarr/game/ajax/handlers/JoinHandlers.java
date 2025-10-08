package com.chriscarr.game.ajax.handlers;

import com.chriscarr.game.WebGame;
import com.chriscarr.game.ajax.AjaxAction;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class JoinHandlers {
    private JoinHandlers() {
    }

    public static AjaxAction join() {
        return (request, response) -> {
            String gameId = request.getParameter("gameId");
            String handle = request.getParameter("handle");
            String user = WebGame.join(Integer.parseInt(gameId), handle);
            printJoinInfoForUser(response, gameId, user);
        };
    }

    public static AjaxAction leave() {
        return (request, response) -> {
            String user = request.getParameter("user");
            String gameId = request.getParameter("gameId");
            WebGame.leave(Integer.parseInt(gameId), user);
            response.getWriter().write("<ok/>");
        };
    }

    public static AjaxAction joinAI() {
        return (request, response) -> {
            String gameId = request.getParameter("gameId");
            String handle = request.getParameter("handle");
            String user = WebGame.joinAI(Integer.parseInt(gameId), handle);
            printJoinInfoForUser(response, gameId, user);
        };
    }

    private static void printJoinInfoForUser(HttpServletResponse response, String gameId, String user)
        throws IOException {
        if (user != null) {
            response.getWriter().write("<joininfo>");
            response.getWriter().write("<user>");
            response.getWriter().write(user);
            response.getWriter().write("</user>");
            response.getWriter().write("<gameid>");
            response.getWriter().write(gameId);
            response.getWriter().write("</gameid>");
            response.getWriter().write("</joininfo>");
        } else {
            response.getWriter().write("<fail/>");
        }
    }
}
