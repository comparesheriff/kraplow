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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AjaxServlet extends HttpServlet {

    private static final ScheduledExecutorService CLEANUP = Executors.newSingleThreadScheduledExecutor(
            r -> {
                Thread thread = new Thread(r, "Cleanup");
                thread.setDaemon(true);
                return thread;
            }
    );

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");

        String messageType = request.getParameter("messageType");
        if (messageType != null && !messageType.isEmpty()) {
            switch (messageType) {
                case "GETGAMESTATE" -> {
                    String gameId = request.getParameter("gameId");
                    JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
                    if (userInterface != null) {
                        GameState gameState = userInterface.getGameState();
                        if (gameState != null) {
                            response.getWriter().write("<gamestate>");
                            response.getWriter().write("<players>");
                            for (GameStatePlayer player : gameState.getPlayers()) {
                                if (userInterface instanceof WebGameUserInterface) {
                                    player.user = ((WebGameUserInterface) userInterface).userFigureNames.get(player.name);
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
                            GameStateCard topCard = gameState.discardTopCard();
                            if (topCard != null) {
                                response.getWriter().write("<discardtopcard>");
                                writeCard(topCard, response);
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
                case "JOIN" -> {
                    String gameId = request.getParameter("gameId");
                    String handle = request.getParameter("handle");
                    String user = WebGame.join(Integer.parseInt(gameId), handle);
                    printJoinInfoForUser(response, gameId, user);
                }
                case "JOINAI" -> {
                    String gameId = request.getParameter("gameId");
                    String handle = request.getParameter("handle");
                    String user = WebGame.joinAI(Integer.parseInt(gameId), handle);
                    printJoinInfoForUser(response, gameId, user);
                }
                case "LEAVE" -> {
                    String user = request.getParameter("user");
                    String gameId = request.getParameter("gameId");
                    WebGame.leave(Integer.parseInt(gameId), user);
                    response.getWriter().write("<ok/>");
                }
                case "COUNTPLAYERS" -> {
                    String gameId = request.getParameter("gameId");
                    response.getWriter().write("<count>");
                    response.getWriter().write("<playercount>");
                    if (gameId != null && !gameId.equals("null")) {
                        response.getWriter().write(Integer.toString(WebGame.getCountPlayers(Integer.parseInt(gameId))));
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
                case "GETGUESTCOUNTER" -> {
                    response.getWriter().write("<guestcounter>");
                    response.getWriter().write(Integer.toString(WebGame.getNextGuestCounter()));
                    response.getWriter().write("</guestcounter>");
                }
                case "AVAILABLEGAMES" -> {
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
                case "CANSTART" -> {
                    String gameId = request.getParameter("gameId");
                    if (WebGame.canStart(Integer.parseInt(gameId))) {
                        response.getWriter().write("<yes/>");
                    } else {
                        response.getWriter().write("<no/>");
                    }
                }
                case "CHAT" -> {
                    String chat = request.getParameter("chat");
                    chat = chat.replace(">", "");
                    chat = chat.replace("<", "");
                    System.out.println("chat:" + chat);
                    String gameId = request.getParameter("gameid");
                    if (gameId == null) {
                        gameId = "lobby";
                    }
                    WebGame.addChat(chat, gameId);
                    response.getWriter().write("<ok/>");
                }
                case "GETCHAT" -> {
                    String guestCounter = request.getParameter("guestCounter");
                    String handle = request.getParameter("handle");
                    String gameId = request.getParameter("gameid");
                    if (gameId == null) {
                        gameId = "lobby";
                    }
                    WebGame.updateSession(guestCounter, handle);
                    List<ChatMessage> chatLog = WebGame.getChatLog(gameId);
                    response.getWriter().write("<chats>");
                    for (ChatMessage chat : chatLog) {
                        response.getWriter().write("<chatmessage>");
                        response.getWriter().write("<chat>");
                        response.getWriter().write(chat.message);
                        response.getWriter().write("</chat>");
                        response.getWriter().write("<timestamp>");
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        response.getWriter().write(sdf.format(chat.timestamp));
                        response.getWriter().write("</timestamp>");
                        response.getWriter().write("</chatmessage>");
                    }
                    List<Session> sessions = WebGame.getSessions();
                    for (Session session : sessions) {
                        response.getWriter().write("<session>");
                        String outHandle = session.handle;
                        if (outHandle == null) {
                            outHandle = "Unknown";
                        }
                        response.getWriter().write(outHandle);
                        response.getWriter().write("</session>");
                    }
                    response.getWriter().write("</chats>");
                }
                case "START" -> {
                    String gameId = request.getParameter("gameId");
                    String aiSleepMs = request.getParameter("aiSleepMs");
                    String pRole = request.getParameter("prole");
                    Role role = Role.valueOf(pRole);
                    String pChar = request.getParameter("pchar");
                    Character character = Character.valueOf(pChar);
                    WebGame.start(Integer.parseInt(gameId), Integer.parseInt(aiSleepMs), role, character);
                    response.getWriter().write("<ok/>");
                }
                case "CREATE" -> {
                    String visibility = request.getParameter("visibility");
                    boolean sidestep = request.getParameterMap().containsKey("sidestep");
                    int gameId = WebGame.create(visibility, sidestep);
                    response.getWriter().write("<gameid>");
                    response.getWriter().write(Integer.toString(gameId));
                    response.getWriter().write("</gameid>");
                }
                case "GETMESSAGE" -> {
                    String user = request.getParameter("user");
                    String gameId = request.getParameter("gameId");
                    JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
                    if (userInterface != null) {
                        List<Message> messages = ((WebGameUserInterface) userInterface).getMessages(user);
                        if (!messages.isEmpty()) {
                            System.out.println("Got message " + messages.getFirst());
                            response.getWriter().write("<message>");
                            response.getWriter().write("<id>");
                            response.getWriter().write(Integer.toString(messages.getFirst().getId()));
                            response.getWriter().write("</id>");
                            response.getWriter().write("<text>");
                            response.getWriter().write(messages.getFirst().getMessage());
                            response.getWriter().write("</text>");
                            response.getWriter().write("<hand>");
                            if (userInterface.isPlayerAlive(((WebGameUserInterface) userInterface).getPlayerForUser(user))) {
                                Hand hand = userInterface.getHandForUser(((WebGameUserInterface) userInterface).getPlayerForUser(user));
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
                case "SENDRESPONSE" -> {
                    System.out.println("Sent Response");
                    String user = request.getParameter("user");
                    String responseMessage = request.getParameter("response");
                    String gameId = request.getParameter("gameId");
                    String messageId = request.getParameter("messageId");
                    System.out.println("Response " + messageId);
                    JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
                    if (userInterface != null) {
                        List<Message> messages = ((WebGameUserInterface) userInterface).getMessages(user);
                        if (!messages.isEmpty()) {
                            Object removed = messages.removeFirst();
                            System.out.println("Removed " + removed);
                            if (!"".equals(responseMessage)) {
                                ((WebGameUserInterface) userInterface).addResponse(user, responseMessage);
                            }
                        }
                    }
                    response.getWriter().write("<ok/>");
                }
                case "GETPLAYERINFO" -> {
                    String user = request.getParameter("user");
                    String gameId = request.getParameter("gameId");
                    JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
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
            }
        }
    }

    private void printJoinInfoForUser(HttpServletResponse response, String gameId, String user) throws IOException {
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

    private void writePlayer(GameStatePlayer player, HttpServletResponse response) throws IOException {
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
        CLEANUP.shutdownNow();   // Executor aufräumen
        super.destroy();
    }
}