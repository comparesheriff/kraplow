package com.chriscarr.game;

import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.ajax.AjaxRegistry;
import com.chriscarr.game.ajax.MessageType;
import com.chriscarr.game.ajax.handlers.*;
import com.chriscarr.game.xml.GenericXmlWriter;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZoneId;
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

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());

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
        if (messageTypeParam == null || messageTypeParam.isEmpty()) {
            LOG.error("No message type specified");
            GenericXmlWriter.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "NO_MESSAGE_TYPE", "no message type specified");
            return;
        }
        MessageType messageType = MessageType.fromString(messageTypeParam);
        if (messageType == null) {
            LOG.error("Unknown message type: {}", messageTypeParam);
            GenericXmlWriter.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "UNKNOWN_MESSAGE_TYPE", "unknown message type");
            return;
        }
        AjaxAction ajaxAction = registry.get(messageType);
        if (ajaxAction == null) {
            LOG.error("No action registered for message type: {}", messageType);
            GenericXmlWriter.writeError(response, HttpServletResponse.SC_BAD_REQUEST, "UNREGISTERED_MESSAGE_TYPE", "no action registered for message type");
            return;
        }

        ajaxAction.handle(request, response);
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
