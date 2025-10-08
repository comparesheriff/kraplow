package com.chriscarr.game;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Hand;
import com.chriscarr.bang.Role;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.gamestate.GameStatePlayer;
import com.chriscarr.bang.userinterface.JSPUserInterface;
import com.chriscarr.bang.userinterface.Message;
import com.chriscarr.bang.userinterface.WebGameUserInterface;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.ajax.AjaxRegistry;
import com.chriscarr.game.ajax.MessageType;
import com.chriscarr.game.ajax.handlers.ChatHandlers;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        .register(MessageType.GETCHAT, ChatHandlers.getChat(dateFormat));


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
                case "GETGAMESTATE" -> handleGetGameState(request, response);
                case "JOIN" -> handleJoin(request, response);
                case "JOINAI" -> handleJoinAI(request, response);
                case "LEAVE" -> handleLeave(request, response);
                case "COUNTPLAYERS" -> handleCountPlayers(request, response);
                case "GETGUESTCOUNTER" -> handleGetGuestCounter(response);
                case "AVAILABLEGAMES" -> handleAvailableGames(response);
                case "CANSTART" -> handleCanStart(request, response);
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

    private void handleCanStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String gameId = request.getParameter("gameId");
        if (WebGame.canStart(Integer.parseInt(gameId))) {
            response.getWriter().write("<yes/>");
        } else {
            response.getWriter().write("<no/>");
        }
    }

    private void handleAvailableGames(HttpServletResponse response) throws IOException {
        response.getWriter().write("<gameids>");
        List<Integer> availableGames = WebGame.getAvailableGames();
        for (Integer availableGame : availableGames) {
            response.getWriter().write("<game>");
            response.getWriter().write("<gameid>");
            response.getWriter().write(Integer.toString(availableGame));
            response.getWriter().write("</gameid>");
            response.getWriter().write("<playercount>");
            response.getWriter().write(Integer.toString(WebGame.getCountPlayers(availableGame)));
            response.getWriter().write("</playercount>");
            response.getWriter().write("<canjoin>");
            response.getWriter().write(Boolean.toString(WebGame.canJoin(availableGame)));
            response.getWriter().write("</canjoin>");
            response.getWriter().write("<players>");
            List<String> joinedPlayers = WebGame.getJoinedPlayers(availableGame);
            for (String playerHandle : joinedPlayers) {
                response.getWriter().write("<playerName>");
                response.getWriter().write(playerHandle);
                response.getWriter().write("</playerName>");
            }
            response.getWriter().write("</players>");
            response.getWriter().write("</game>");
        }
        response.getWriter().write("</gameids>");
    }

    private void handleGetGuestCounter(HttpServletResponse response) throws IOException {
        response.getWriter().write("<guestcounter>");
        response.getWriter().write(Integer.toString(WebGame.getNextGuestCounter()));
        response.getWriter().write("</guestcounter>");
    }

    private void handleCountPlayers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String gameId = request.getParameter("gameId");
        response.getWriter().write("<count>");
        response.getWriter().write("<playercount>");
        if (gameId != null && !gameId.equals("null")) {
            response
                .getWriter()
                .write(Integer.toString(WebGame.getCountPlayers(Integer.parseInt(gameId))));
        } else {
            response.getWriter().write("0");
        }
        response.getWriter().write("</playercount>");
        response.getWriter().write("<players>");
        List<String> joinedPlayers = null;
        if (gameId != null) {
            joinedPlayers = WebGame.getJoinedPlayers(Integer.parseInt(gameId));
        }
        if (joinedPlayers != null) {
            for (String playerHandle : joinedPlayers) {
                response.getWriter().write("<playerName>");
                response.getWriter().write(playerHandle);
                response.getWriter().write("</playerName>");
            }
        }
        response.getWriter().write("</players>");
        response.getWriter().write("</count>");
    }

    private void handleLeave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String user = request.getParameter("user");
        String gameId = request.getParameter("gameId");
        WebGame.leave(Integer.parseInt(gameId), user);
        response.getWriter().write("<ok/>");
    }

    private void handleJoinAI(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String gameId = request.getParameter("gameId");
        String handle = request.getParameter("handle");
        String user = WebGame.joinAI(Integer.parseInt(gameId), handle);
        printJoinInfoForUser(response, gameId, user);
    }

    private void handleJoin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String gameId = request.getParameter("gameId");
        String handle = request.getParameter("handle");
        String user = WebGame.join(Integer.parseInt(gameId), handle);
        printJoinInfoForUser(response, gameId, user);
    }

    private void handleGetGameState(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String gameId = request.getParameter("gameId");
        JSPUserInterface userInterface =
            (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
        if (userInterface != null) {
            GameState gameState = userInterface.getGameState();
            if (gameState != null) {
                response.getWriter().write("<gamestate>");
                response.getWriter().write("<players>");
                for (GameStatePlayer player : gameState.getPlayers()) {
                    if (userInterface instanceof WebGameUserInterface) {
                        player.user =
                            ((WebGameUserInterface) userInterface).userFigureNames.get(player.name);
                    }
                    writePlayer(player, response);
                }
                response.getWriter().write("</players>");
                if (gameState.timeout() != null) {
                    response.getWriter().write("<timeout>" + gameState.timeout() + "</timeout>");
                }
                if (gameState.isGameOver()) {
                    response.getWriter().write("<gameover/>");
                    int gameIdInt = Integer.parseInt(gameId);
                    WebGame.removeGame(Integer.parseInt(gameId));
                    CLEANUP.schedule(() -> WebInit.remove(gameIdInt), 10, TimeUnit.SECONDS);
                }
                response.getWriter().write("<currentname>");
                response.getWriter().write(gameState.getCurrentName());
                response.getWriter().write("</currentname>");
                response.getWriter().write("<decksize>");
                response.getWriter().write(Integer.toString(gameState.getDeckSize()));
                response.getWriter().write("</decksize>");
                Optional<GameStateCard> topCard = gameState.discardTopCard();
                if (topCard.isPresent()) {
                    response.getWriter().write("<discardtopcard>");
                    writeCard(topCard.get(), response);
                    response.getWriter().write("</discardtopcard>");
                }
                response.getWriter().write("<roles>");
                ArrayList<String> roles = userInterface.getRoles();
                for (String role : roles) {
                    response.getWriter().write("<role>" + role + "</role>");
                }
                response.getWriter().write("</roles>");
                response.getWriter().write("</gamestate>");
            } else {
                response.getWriter().write("<gamestate/>");
            }
        } else {
            response.getWriter().write("<gamestate/>");
        }
    }

    private void printJoinInfoForUser(HttpServletResponse response, String gameId, String user)
        throws IOException {
        if (user != null) {
            response.getWriter().write("<joininfo>");
            response.getWriter().write("<user>");
            response.getWriter().write(user);
            response.getWriter().write("</user>");
            response.getWriter().write("<gameid>");
            response.getWriter().write(gameId);
            response.getWriter().write("</gameid>");
            response.getWriter().write("</joininfo>");
        } else {
            response.getWriter().write("<fail/>");
        }
    }

    private void writePlayer(GameStatePlayer player, HttpServletResponse response)
        throws IOException {
        response.getWriter().write("<player>");
        response.getWriter().write("<handle>");
        response.getWriter().write(player.user);
        response.getWriter().write("</handle>");
        response.getWriter().write("<name>");
        response.getWriter().write(player.name);
        response.getWriter().write("</name>");
        response.getWriter().write("<specialability>");
        response.getWriter().write(player.specialAbility);
        response.getWriter().write("</specialability>");
        response.getWriter().write("<health>");
        response.getWriter().write(Integer.toString(player.health));
        response.getWriter().write("</health>");
        response.getWriter().write("<maxhealth>");
        response.getWriter().write(Integer.toString(player.maxHealth));
        response.getWriter().write("</maxhealth>");
        response.getWriter().write("<handsize>");
        response.getWriter().write(Integer.toString(player.handSize));
        response.getWriter().write("</handsize>");
        if (player.isSheriff) {
            response.getWriter().write("<issheriff/>");
        }
        if (player.gun != null) {
            response.getWriter().write("<gun>");
            writeCard(player.gun, response);
            response.getWriter().write("</gun>");
        }
        List<GameStateCard> inPlay = player.inPlay;
        if (inPlay != null && !inPlay.isEmpty()) {
            response.getWriter().write("<inplay>");
            for (GameStateCard inPlayCard : inPlay) {
                response.getWriter().write("<inplaycard>");
                writeCard(inPlayCard, response);
                response.getWriter().write("</inplaycard>");
            }
            response.getWriter().write("</inplay>");
        }
        response.getWriter().write("</player>");
    }

    private void writeCard(GameStateCard card, HttpServletResponse response) throws IOException {
        response.getWriter().write("<name>");
        response.getWriter().write(card.name.getDisplayName());
        response.getWriter().write("</name>");
        response.getWriter().write("<suit>");
        response.getWriter().write(card.suit);
        response.getWriter().write("</suit>");
        response.getWriter().write("<value>");
        response.getWriter().write(card.value);
        response.getWriter().write("</value>");
        response.getWriter().write("<type>");
        response.getWriter().write(card.type);
        response.getWriter().write("</type>");
    }

    @Override
    public void destroy() {
        CLEANUP.shutdownNow(); // Executor aufräumen
        super.destroy();
    }
}
