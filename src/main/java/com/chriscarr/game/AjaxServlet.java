package com.chriscarr.game;

import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.ajax.AjaxRegistry;
import com.chriscarr.game.ajax.MessageType;
import com.chriscarr.game.ajax.handlers.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AjaxServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(AjaxServlet.class);

    private static final ScheduledExecutorService CLEANUP =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
                Thread thread = new Thread(r, "Cleanup");
                thread.setDaemon(true);
                return thread;
            });

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final AjaxRegistry registry = new AjaxRegistry()
        .register(MessageType.CHAT, ChatHandlers.chat())
        .register(MessageType.GETCHAT, ChatHandlers.getChat(dateFormat))
        .register(MessageType.GETGAMESTATE, GameStateHandlers.getGameState(CLEANUP))
        .register(MessageType.JOIN, JoinHandlers.join())
        .register(MessageType.JOINAI, JoinHandlers.joinAI())
        .register(MessageType.LEAVE, JoinHandlers.leave())
        .register(MessageType.AVAILABLEGAMES, LobbyHandlers.availableGames())
        .register(MessageType.COUNTPLAYERS, LobbyHandlers.countPlayers())
        .register(MessageType.GETGUESTCOUNTER, LobbyHandlers.getGuestCounter())
        .register(MessageType.CANSTART, LobbyHandlers.canStart())
        .register(MessageType.GETMESSAGE, MessageHandlers.getMessage())
        .register(MessageType.SENDRESPONSE, MessageHandlers.sendResponse())
        .register(MessageType.GETPLAYERINFO, PlayerInfoHandlers.getPlayerInfo())
        .register(MessageType.START, CommandHandlers.startGame())
        .register(MessageType.CREATE, CommandHandlers.createGame());


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setResponseHeaders(response);

        String messageTypeParam = request.getParameter("messageType");
        if (messageTypeParam != null && !messageTypeParam.isEmpty()) {
            MessageType messageType = MessageType.fromString(messageTypeParam);
            AjaxAction ajaxAction = registry.get(messageType);
            if (ajaxAction != null) {
                ajaxAction.handle(request, response);
                return;
            }

            LOG.error("Unknown message type: {}", messageTypeParam);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<error>unknown message type</error>");
        } else {
            LOG.error("No message type specified");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<error>no message type specified</error>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    private void setResponseHeaders(HttpServletResponse response) {
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("X-Content-Type-Options", "nosniff");
    }

    @Override
    public void destroy() {
        CLEANUP.shutdownNow(); // Executor aufräumen
        super.destroy();
    }
}
