package com.chriscarr.game.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class ParamUtil {
    private ParamUtil() {
    }

    public static Integer intParamOr400(HttpServletRequest req, HttpServletResponse resp, String paramName) throws IOException {
        String raw = req.getParameter(paramName);
        if (raw == null || raw.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("<error>missing " + paramName + "</error>");
            return null;
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("<error>invalid " + paramName + "</error>");
            return null;
        }
    }
}
