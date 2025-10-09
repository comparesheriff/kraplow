package com.chriscarr.game.xml;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class GenericXmlWriter {
    private GenericXmlWriter() {
    }

    public static void writeOk(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<ok/>");
    }

    public static void writeError(HttpServletResponse resp, int status, String code, String message) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write("<error ");
        if (code != null && !code.isEmpty()) {
            resp.getWriter().write(" code=\"");
            resp.getWriter().write(XmlUtil.escapeXml(code));
            resp.getWriter().write("\"");
        }
        resp.getWriter().write(">");
        resp.getWriter().write(XmlUtil.escapeXml(message));
        resp.getWriter().write("</error>");
    }
}
