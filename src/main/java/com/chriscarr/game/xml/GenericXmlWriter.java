package com.chriscarr.game.xml;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class GenericXmlWriter {
    private GenericXmlWriter() {
    }

    public static void writeOk(HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<ok/>");
    }
}
