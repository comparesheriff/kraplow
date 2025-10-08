package com.chriscarr.game.ajax;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@FunctionalInterface
public interface AjaxAction {
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
