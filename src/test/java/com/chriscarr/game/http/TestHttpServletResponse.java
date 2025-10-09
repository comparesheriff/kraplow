package com.chriscarr.game.http;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestHttpServletResponse extends HttpServletResponseWrapper {
    private final StringWriter buf = new StringWriter();
    private final PrintWriter writer = new PrintWriter(buf);
    private int status = 200;
    private String contentType;
    private String charset = "UTF-8";

    public TestHttpServletResponse() {
        super(Mockito.mock(HttpServletResponse.class)); // Dummy-Delegate
    }

    @Override
    public void setStatus(int sc) {
        this.status = sc;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setCharacterEncoding(String cs) {
        this.charset = cs;
    }

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    public String getBody() {
        writer.flush();
        return buf.toString();
    }

}
