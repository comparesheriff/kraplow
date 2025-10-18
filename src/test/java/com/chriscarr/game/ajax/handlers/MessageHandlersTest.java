package com.chriscarr.game.ajax.handlers;


import com.chriscarr.bang.userinterface.Message;
import com.chriscarr.bang.userinterface.MessageImpl;
import com.chriscarr.bang.userinterface.WebGameUserInterface;
import com.chriscarr.game.WebInit;
import com.chriscarr.game.ajax.AjaxAction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mockito;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class MessageHandlersTest {

    @Nested
    class TestUI extends WebGameUserInterface {
        private final Map<String, List<Message>> msgs = new ConcurrentHashMap<>();
        private final Map<String, List<Message>> resps = new ConcurrentHashMap<>();

        TestUI() {
            super(List.of(), 0);
            msgs.put("u", new LinkedList<>());
            resps.put("u", new LinkedList<>());
        }

        @Override
        public List<Message> getMessages(String user) {
            return msgs.get(user);
        }

        @Override
        public void addResponse(String user, String message) {
            resps.get("u").add(new MessageImpl(message));
        }
    }

    @Test
    @Timeout(2)
    void sendResponse_isIdempotentOnMismatchedMessageId() throws Exception {
        // Arrange WebInit map
        TestUI ui = new TestUI();
        WebInit.TestHooks.putUI(7, ui);

        // Queue mit einer Message id=123
        List<Message> queue = ui.getMessages("u");
        queue.add(new Message() {
            public void setMessage(String m) {
            }

            public String getMessage() {
                return "m";
            }

            public int getId() {
                return 123;
            }
        });

        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        when(resp.getWriter()).thenReturn(new PrintWriter(sw));

        when(req.getParameter("user")).thenReturn("u");
        when(req.getParameter("response")).thenReturn("R");
        when(req.getParameter("gameId")).thenReturn("7");
        when(req.getParameter("messageId")).thenReturn("999"); // mismatch

        AjaxAction action = MessageHandlers.sendResponse();

        // Act
        action.handle(req, resp);

        // Assert: Queue unverändert (keine Entfernung, keine Response angenommen)
        assertEquals(1, queue.size(), "message must not be consumed on mismatch");
        assertTrue(sw.toString().contains("<ok/>"), "should answer OK");
    }
}