package com.chriscarr.game;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Role;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.JSPUserInterface;
import com.chriscarr.bang.userinterface.Message;
import com.chriscarr.bang.userinterface.WebGameUserInterface;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.ajax.AjaxRegistry;
import com.chriscarr.game.ajax.MessageType;
import com.chriscarr.game.ajax.handlers.ChatHandlers;
import com.chriscarr.game.ajax.handlers.GameStateHandlers;
import com.chriscarr.game.ajax.handlers.JoinHandlers;
import com.chriscarr.game.ajax.handlers.LobbyHandlers;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
        .register(MessageType.CANSTART, LobbyHandlers.canStart());


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

            switch (messageTypeParam) {
                case "START" -> handleStart(request, response);
                case "CREATE" -> handleCreate(request, response);
                case "GETMESSAGE" -> handleGetMessage(request, response);
                case "SENDRESPONSE" -> handleSendResponse(request, response);
                case "GETPLAYERINFO" -> handleGetPlayerInfo(request, response);
                default -> {
                    LOG.error("Unknown message type: {}", messageTypeParam);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("<error>unknown message type</error>");
                }
            }
        } else {
            LOG.error("No message type specified");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("<error>no message type specified</error>");
        }
    }

    private void setResponseHeaders(HttpServletResponse response) {
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
    }

    private void handleGetPlayerInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String gameId = request.getParameter("gameId");
        JSPUserInterface userInterface =
            (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
        if (userInterface != null) {
            String name = ((WebGameUserInterface) userInterface).getPlayerForUser(user);
            String role = userInterface.getRoleForName(name);
            String goal = userInterface.getGoalForName(name);
            response.getWriter().write("<userinfo>");
            response.getWriter().write("<name>");
            response.getWriter().write(name);
            response.getWriter().write("</name>");
            response.getWriter().write("<role>");
            response.getWriter().write(role);
            response.getWriter().write("</role>");
            response.getWriter().write("<goal>");
            response.getWriter().write(goal);
            response.getWriter().write("</goal>");
            response.getWriter().write("</userinfo>");
        } else {
            response.getWriter().write("<ok/>");
        }
    }

    private void handleSendResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.info("Sent Response");
        String user = request.getParameter("user");
        String responseMessage = request.getParameter("response");
        String gameId = request.getParameter("gameId");
        String messageId = request.getParameter("messageId");
        LOG.info("Response {}", messageId);
        JSPUserInterface userInterface =
            (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
        if (userInterface != null) {
            List<Message> messages = ((WebGameUserInterface) userInterface).getMessages(user);
            if (!messages.isEmpty()) {
                Object removed = messages.removeFirst();
                LOG.info("Removed {}", removed);
                if (!"".equals(responseMessage)) {
                    ((WebGameUserInterface) userInterface).addResponse(user, responseMessage);
                }
            }
        }
        response.getWriter().write("<ok/>");
    }

    private void handleGetMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String gameId = request.getParameter("gameId");
        JSPUserInterface userInterface =
            (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
        if (userInterface != null) {
            List<Message> messages = ((WebGameUserInterface) userInterface).getMessages(user);
            if (!messages.isEmpty()) {
                LOG.info("Got message {}", messages.getFirst());
                response.getWriter().write("<message>");
                response.getWriter().write("<id>");
                response.getWriter().write(Integer.toString(messages.getFirst().getId()));
                response.getWriter().write("</id>");
                response.getWriter().write("<text>");
                response.getWriter().write(messages.getFirst().getMessage());
                response.getWriter().write("</text>");
                response.getWriter().write("<hand>");
                if (userInterface.isPlayerAlive(
                    ((WebGameUserInterface) userInterface).getPlayerForUser(user))) {
                    Hand hand =
                        userInterface.getHandForUser(
                            ((WebGameUserInterface) userInterface).getPlayerForUser(user));
                    for (Card value : hand) {
                        response.getWriter().write("<card>");
                        response.getWriter().write(value.getName().getDisplayName());
                        response.getWriter().write("</card>");
                    }
                }
                response.getWriter().write("</hand>");
                response.getWriter().write("</message>");
            } else {
                response.getWriter().write("<ok/>");
            }
        } else {
            response.getWriter().write("<ok/>");
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String visibility = request.getParameter("visibility");
        boolean sidestep = request.getParameterMap().containsKey("sidestep");
        int gameId = WebGame.create(visibility, sidestep);
        response.getWriter().write("<gameid>");
        response.getWriter().write(Integer.toString(gameId));
        response.getWriter().write("</gameid>");
    }

    private void handleStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String gameId = request.getParameter("gameId");
        String aiSleepMs = request.getParameter("aiSleepMs");
        String pRole = request.getParameter("prole");
        Role role = Role.valueOf(pRole);
        String pChar = request.getParameter("pchar");
        Character character = Character.valueOf(pChar);
        WebGame.start(Integer.parseInt(gameId), Integer.parseInt(aiSleepMs), role, character);
        response.getWriter().write("<ok/>");
    }

    @Override
    public void destroy() {
        CLEANUP.shutdownNow(); // Executor aufräumen
        super.destroy();
    }
}
