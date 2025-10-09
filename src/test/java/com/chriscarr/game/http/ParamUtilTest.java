package com.chriscarr.game.http;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ParamUtilTest {
    @Test
    void missingParamYields400() throws Exception {
        var req = Mockito.mock(HttpServletRequest.class);
        when(req.getParameter("gameId")).thenReturn(null);

        var resp = new TestHttpServletResponse();

        assertNull(ParamUtil.intParamOr400(req, resp, "gameId"));
        assertEquals(400, resp.getStatus());
        assertTrue(resp.getBody().contains("<error"));
        assertTrue(resp.getBody().contains("MISSING_PARAM"));
    }

    @Test
    void invalidParamYields400() throws Exception {
        var req = Mockito.mock(HttpServletRequest.class);
        var resp = new TestHttpServletResponse();
        when(req.getParameter("gameId")).thenReturn("x");
        assertNull(ParamUtil.intParamOr400(req, resp, "gameId"));
        assertEquals(400, resp.getStatus());
        assertTrue(resp.getBody().contains("<error"));
        assertTrue(resp.getBody().contains("INVALID_PARAM"));
    }


    @Test
    void validParamYieldsInt() throws Exception {
        var req = Mockito.mock(HttpServletRequest.class);
        var resp = new TestHttpServletResponse();
        when(req.getParameter("gameId")).thenReturn("22");
        assertEquals(22, ParamUtil.intParamOr400(req, resp, "gameId"));
    }
}