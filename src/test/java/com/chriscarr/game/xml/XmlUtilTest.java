package com.chriscarr.game.xml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlUtilTest {
    @Test
    void escapesAll() {
        assertEquals("&lt;a&amp;b&gt;", XmlUtil.escapeXml("<a&b>"));
        assertEquals("", XmlUtil.escapeXml(null));
    }
}