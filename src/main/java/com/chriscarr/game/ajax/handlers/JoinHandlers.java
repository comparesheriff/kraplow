package com.chriscarr.game.ajax.handlers;

import com.chriscarr.game.WebGame;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.http.ParamUtil;
import com.chriscarr.game.xml.GenericXmlWriter;
import com.chriscarr.game.xml.XmlUtil;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class JoinHandlers {
    private JoinHandlers() {
    }

    public static AjaxAction leave() {
        return (request, response) -> {
            String user = request.getParameter("user");
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            if (gameId == null) {
                return;
            }
            WebGame.leave(gameId, user);
            GenericXmlWriter.writeOk(response);
        };
    }

    public static AjaxAction join() {
        return (request, response) -> {
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            if (gameId == null) {
                return;
            }
            String handle = request.getParameter("handle");
            String user = WebGame.join(gameId, handle);
            printJoinInfoForUser(response, gameId, user);
        };
    }

    public static AjaxAction joinAI() {
        return (request, response) -> {
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            if (gameId == null) {
                return;
            }
            String handle = request.getParameter("handle");
            String user = WebGame.joinAI(gameId, handle);
            printJoinInfoForUser(response, gameId, user);
        };
    }

    private static void printJoinInfoForUser(HttpServletResponse response, Integer gameId, String user)
        throws IOException {
        if (user != null) {
            response.getWriter().write("<joininfo>");
            response.getWriter().write("<user>");
            response.getWriter().write(XmlUtil.escapeXml(user));
            response.getWriter().write("</user>");
            response.getWriter().write("<gameid>");
            response.getWriter().print(gameId);
            response.getWriter().write("</gameid>");
            response.getWriter().write("</joininfo>");
        } else {
            response.getWriter().write("<fail/>");
        }
    }
}
