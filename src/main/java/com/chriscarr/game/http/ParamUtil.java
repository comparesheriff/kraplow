package com.chriscarr.game.http;

import com.chriscarr.game.xml.GenericXmlWriter;
import com.chriscarr.game.xml.XmlUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.OptionalInt;

public final class ParamUtil {
    private ParamUtil() {
    }

    public static Integer intParamOr400(HttpServletRequest req, HttpServletResponse resp, String paramName) throws IOException {
        String rawParam = req.getParameter(paramName);
        OptionalInt opt = intParam(rawParam);
        if (opt.isPresent()) {
            return opt.getAsInt();
        }

        if (rawParam == null || rawParam.isEmpty()) {
            GenericXmlWriter.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "MISSING_PARAM", "missing " + XmlUtil.escapeXml(paramName));
        } else {
            GenericXmlWriter.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "INVALID_PARAM", "invalid " + XmlUtil.escapeXml(paramName));

        }
        return null;
    }

    public static OptionalInt intParam(String raw) {
        if (raw == null || raw.isEmpty()) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(raw.trim()));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}
