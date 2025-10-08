package com.chriscarr.game.xml;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public final class MessageXmlWriter {
    private MessageXmlWriter() {
    }

    public static void writeOk(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<ok/>");
    }

    public static void writeMessage(int id, String text, List<String> cardDisplayNames, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<message>");
        resp.getWriter().write("<id>");
        resp.getWriter().write(Integer.toString(id));
        resp.getWriter().write("</id>");
        resp.getWriter().write("<text>");
        resp.getWriter().write(XmlUtil.escapeXml(text));
        resp.getWriter().write("</text>");
        resp.getWriter().write("<hand>");
        for (String cardName : cardDisplayNames) {
            resp.getWriter().write("<card>");
            resp.getWriter().write(XmlUtil.escapeXml(cardName));
            resp.getWriter().write("</card>");
        }
        resp.getWriter().write("</hand>");
        resp.getWriter().write("</message>");
    }
}
