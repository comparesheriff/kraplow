package com.chriscarr.game.ajax.handlers;

import com.chriscarr.bang.userinterface.JSPUserInterface;
import com.chriscarr.bang.userinterface.WebGameUserInterface;
import com.chriscarr.game.WebInit;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.http.ParamUtil;
import com.chriscarr.game.xml.GenericXmlWriter;
import com.chriscarr.game.xml.XmlUtil;

public final class PlayerInfoHandlers {
    private PlayerInfoHandlers() {
    }

    public static AjaxAction getPlayerInfo() {
        return (request, response) -> {
            String user = request.getParameter("user");
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            if (gameId == null) {
                return;
            }
            JSPUserInterface ui = (JSPUserInterface) WebInit.getUserInterface(gameId);
            if (ui == null) {
                GenericXmlWriter.writeOk(response);
                return;
            }
            String name = ((WebGameUserInterface) ui).getPlayerForUser(user);
            if (name == null) {
                GenericXmlWriter.writeOk(response);
                return;
            }
            String role = ui.getRoleForName(name);
            String goal = ui.getGoalForName(name);

            response.getWriter().write("<userinfo>");
            response.getWriter().write("<name>");
            response.getWriter().write(XmlUtil.escapeXml(name));
            response.getWriter().write("</name>");
            response.getWriter().write("<role>");
            response.getWriter().write(XmlUtil.escapeXml(role));
            response.getWriter().write("</role>");
            response.getWriter().write("<goal>");
            response.getWriter().write(XmlUtil.escapeXml(goal));
            response.getWriter().write("</goal>");
            response.getWriter().write("</userinfo>");
        };
    }
}
