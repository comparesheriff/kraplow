package com.chriscarr.game.ajax.handlers;

import com.chriscarr.game.ChatMessage;
import com.chriscarr.game.Session;
import com.chriscarr.game.WebGame;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.xml.GenericXmlWriter;
import com.chriscarr.game.xml.XmlUtil;

import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ChatHandlers {
    private ChatHandlers() {
    }

    public static AjaxAction chat() {
        return (request, response) -> {
            String chat = XmlUtil.escapeXml(request.getParameter("chat"));
            String gameId = request.getParameter("gameid");
            if (gameId == null) {
                gameId = "lobby";
            }
            WebGame.addChat(chat, gameId);
            GenericXmlWriter.writeOk(response);
        };
    }

    public static AjaxAction getChat(DateTimeFormatter dateFormat) {
        return (request, response) -> {
            String guestCounter = request.getParameter("guestCounter");
            String handle = request.getParameter("handle");
            String gameId = request.getParameter("gameid");
            if (gameId == null) {
                gameId = "lobby";
            }
            WebGame.updateSession(guestCounter, handle);
            List<ChatMessage> chatLog = WebGame.getChatLog(gameId);
            if (chatLog == null || chatLog.isEmpty()) {
                response.getWriter().write("<chats/>");
                return;
            }
            response.getWriter().write("<chats>");
            for (ChatMessage chat : chatLog) {
                response.getWriter().write("<chatmessage>");
                response.getWriter().write("<chat>");
                response.getWriter().write(XmlUtil.escapeXml(chat.getMessage()));
                response.getWriter().write("</chat>");
                response.getWriter().write("<timestamp>");
                response.getWriter().write(dateFormat.format(chat.getTimestamp()));
                response.getWriter().write("</timestamp>");
                response.getWriter().write("</chatmessage>");
            }
            List<Session> sessions = WebGame.getSessions();
            for (Session session : sessions) {
                String outHandle = session.handle == null ? "Unknown" : session.handle;
                response.getWriter().write("<session>");
                response.getWriter().write(XmlUtil.escapeXml(outHandle));
                response.getWriter().write("</session>");
            }
            response.getWriter().write("</chats>");
        };
    }
}
