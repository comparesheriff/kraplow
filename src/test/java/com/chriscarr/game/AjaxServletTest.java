package com.chriscarr.game;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AjaxServletTest {

    @Test
    void unknownMessageTypeYields400AndXmlError() throws Exception {
        AjaxServlet servlet = new AjaxServlet();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        var resp = new com.chriscarr.game.http.TestHttpServletResponse(); // exists in tests

        Mockito.when(req.getParameter("messageType")).thenReturn("FOO_BAR");

        servlet.doGet(req, resp);

        assertEquals(400, resp.getStatus());
        String body = resp.getBody();
        assertTrue(body.contains("<error"));
        assertTrue(body.contains("UNKNOWN_MESSAGE_TYPE"));
    }

    @Test
    void missingMessageTypeYields400AndXmlError() throws Exception {
        AjaxServlet servlet = new AjaxServlet();
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        var resp = new com.chriscarr.game.http.TestHttpServletResponse();

        Mockito.when(req.getParameter("messageType")).thenReturn(null);

        servlet.doGet(req, resp);

        assertEquals(400, resp.getStatus());
        String body = resp.getBody();
        assertTrue(body.contains("<error"));
        assertTrue(body.contains("NO_MESSAGE_TYPE"));
    }
}
