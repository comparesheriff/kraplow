package com.chriscarr.game;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.gamestate.GameStatePlayer;
import com.chriscarr.bang.models.servlet.MessageType;
import com.chriscarr.bang.userinterface.JSPUserInterface;
import com.chriscarr.bang.userinterface.Message;
import com.chriscarr.bang.userinterface.WebGameUserInterface;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AjaxServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");

        String messageTypeParam = request.getParameter("messageType");
        MessageType messageType = null;
        try {
            messageType = MessageType.valueOf(messageTypeParam);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (messageType == null) {
            return;
        }
        PrintWriter writer = response.getWriter();
        String gameId = request.getParameter("gameId");
        String handle = request.getParameter("handle");
        String user = request.getParameter("user");
        String chat = request.getParameter("chat");
        String guestCounter = request.getParameter("guestCounter");
        String aiSleepMs = request.getParameter("aiSleepMs");
        String pRole = request.getParameter("prole");
        String pChar = request.getParameter("pchar");
        String visibility = request.getParameter("visibility");
        boolean sidestep = request.getParameterMap().containsKey("sidestep");
        String responseMessage = request.getParameter("response");
        String messageId = request.getParameter("messageId");
        switch (messageType) {
            case GETGAMESTATE: {
                handleGetGameState(gameId, writer);
                break;
            }
            case JOIN: {
                handleJoin(gameId, handle, writer);
                break;
            }
            case JOINAI: {
                handleJoinAI(gameId, handle, writer);
                break;
            }
            case LEAVE: {
                handleLeave(gameId, user, writer);
                break;
            }
            case COUNTPLAYERS: {
                handleCountPlayers(gameId, writer);
                break;
            }
            case GETGUESTCOUNTER:
                handleGetGuestCounter(writer);
                break;
            case AVAILABLEGAMES:
                handleAvailableGames(writer);
                break;
            case CANSTART: {
                handleCanStart(gameId, writer);
                break;
            }
            case CHAT: {
                handleChat(gameId, chat, writer);
                break;
            }
            case GETCHAT: {
                handleGetChat(gameId, handle, guestCounter, writer);
                break;
            }
            case START: {
                handleStart(gameId, aiSleepMs, pRole, pChar, writer);
                break;
            }
            case CREATE: {
                handleCreate(visibility, sidestep, writer);
                break;
            }
            case GETMESSAGE: {
                handleGetMessage(gameId, user, writer);
                break;
            }
            case SENDRESPONSE: {
                handleSendResponse(gameId, user, messageId, responseMessage, writer);
                break;
            }
            case GETPLAYERINFO: {
                handleGetPlayerInfo(gameId, user, writer);
                break;
            }
        }

    }

    private void handleGetPlayerInfo(String gameId, String user, PrintWriter writer) {
        JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
        if (userInterface != null) {
            String name = ((WebGameUserInterface) userInterface).getPlayerForUser(user);
            String role = userInterface.getRoleForName(name);
            String goal = userInterface.getGoalForName(name);
            writer.write("<userinfo>");
            writer.write("<name>");
            writer.write(name);
            writer.write("</name>");
            writer.write("<role>");
            writer.write(role);
            writer.write("</role>");
            writer.write("<goal>");
            writer.write(goal);
            writer.write("</goal>");
            writer.write("</userinfo>");
        } else {
            writer.write("<ok/>");
        }
    }

    private void handleSendResponse(String gameId, String user, String messageId, String responseMessage, PrintWriter writer) {
        System.out.println("Sent Response");
        System.out.println("Response " + messageId);
        JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
        if (userInterface != null) {
            List<Message> messages = ((WebGameUserInterface) userInterface).getMessages(user);
            if (!messages.isEmpty()) {
                Object removed = messages.remove(0);
                System.out.println("Removed " + removed);
                if (!"".equals(responseMessage)) {
                    ((WebGameUserInterface) userInterface).addResponse(user, responseMessage);
                }
            }
        }
        writer.write("<ok/>");
    }

    private void handleGetMessage(String gameId, String user, PrintWriter writer) {
        JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
        if (userInterface != null) {
            List<Message> messages = ((WebGameUserInterface) userInterface).getMessages(user);
            if (!messages.isEmpty()) {
                System.out.println("Got message " + messages.get(0));
                writer.write("<message>");
                writer.write("<id>");
                writer.write(Integer.toString(messages.get(0).getId()));
                writer.write("</id>");
                writer.write("<text>");
                writer.write(messages.get(0).getMessage());
                writer.write("</text>");
                writer.write("<hand>");
                if (userInterface.isPlayerAlive(((WebGameUserInterface) userInterface).getPlayerForUser(user))) {
                    Hand hand = userInterface.getHandForUser(((WebGameUserInterface) userInterface).getPlayerForUser(user));
                    for (int i = 0; i < hand.size(); i++) {
                        Card card = hand.get(i);
                        writer.write("<card>");
                        writer.write(card.getName());
                        writer.write("</card>");
                    }
                }
                writer.write("</hand>");
                writer.write("</message>");
            } else {
                writer.write("<ok/>");
            }
        } else {
            writer.write("<ok/>");
        }
    }

    private void handleCreate(String visibility, boolean sidestep, PrintWriter writer) {
        int gameId = WebGame.create(visibility, sidestep);
        writer.write("<gameid>");
        writer.write(Integer.toString(gameId));
        writer.write("</gameid>");
    }

    private void handleStart(String gameId, String aiSleepMs, String pRole, String pChar, PrintWriter writer) {
        WebGame.start(Integer.parseInt(gameId), Integer.parseInt(aiSleepMs), pRole, pChar);
        writer.write("<ok/>");
    }

    private void handleGetChat(String gameId, String handle, String guestCounter, PrintWriter writer) {
        if (gameId == null) {
            gameId = "lobby";
        }
        WebGame.updateSession(guestCounter, handle);
        List<ChatMessage> chatLog = WebGame.getChatLog(gameId);
        writer.write("<chats>");
        for (ChatMessage chat : chatLog) {
            writer.write("<chatmessage>");
            writer.write("<chat>");
            writer.write(chat.message);
            writer.write("</chat>");
            writer.write("<timestamp>");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            writer.write(sdf.format(chat.timestamp));
            writer.write("</timestamp>");
            writer.write("</chatmessage>");
        }
        List<Session> sessions = WebGame.getSessions();
        for (Session session : sessions) {
            writer.write("<session>");
            String outHandle = session.handle;
            if (outHandle == null) {
                outHandle = "Unknown";
            }
            writer.write(outHandle);
            writer.write("</session>");
        }
        writer.write("</chats>");
    }

    private void handleChat(String gameId, String chat, PrintWriter writer) {
        chat = chat.replace(">", "");
        chat = chat.replace("<", "");
        System.out.println("chat:" + chat);
        if (gameId == null) {
            gameId = "lobby";
        }
        WebGame.addChat(chat, gameId);
        writer.write("<ok/>");
    }

    private void handleCanStart(String gameId, PrintWriter writer) {
        if (WebGame.canStart(Integer.parseInt(gameId))) {
            writer.write("<yes/>");
        } else {
            writer.write("<no/>");
        }
    }

    private void handleAvailableGames(PrintWriter writer) {
        writer.write("<gameids>");
        List<Integer> availableGames = WebGame.getAvailableGames();
        for (Integer availableGame : availableGames) {
            writer.write("<game>");
            writer.write("<gameid>");
            writer.write(Integer.toString(availableGame));
            writer.write("</gameid>");
            writer.write("<playercount>");
            writer.write(Integer.toString(WebGame.getCountPlayers(availableGame)));
            writer.write("</playercount>");
            writer.write("<canjoin>");
            writer.write(Boolean.toString(WebGame.canJoin(availableGame)));
            writer.write("</canjoin>");
            writer.write("<players>");
            List<String> joinedPlayers = WebGame.getJoinedPlayers(availableGame);
            for (String playerHandle : joinedPlayers) {
                writer.write("<playerName>");
                writer.write(playerHandle);
                writer.write("</playerName>");
            }
            writer.write("</players>");
            writer.write("</game>");
        }
        writer.write("</gameids>");
    }

    private void handleGetGuestCounter(PrintWriter writer) {
        writer.write("<guestcounter>");
        writer.write(Integer.toString(WebGame.getNextGuestCounter()));
        writer.write("</guestcounter>");
    }

    private void handleCountPlayers(String gameId, PrintWriter writer) {
        writer.write("<count>");
        writer.write("<playercount>");
        if (gameId != null && !gameId.equals("null")) {
            writer.write(Integer.toString(WebGame.getCountPlayers(Integer.parseInt(gameId))));
        } else {
            writer.write("0");
        }
        writer.write("</playercount>");
        writer.write("<players>");
        List<String> joinedPlayers = WebGame.getJoinedPlayers(Integer.parseInt(gameId)); //refactor nullcheck

        for (String playerHandle : joinedPlayers) {
            writer.write("<playerName>");
            writer.write(playerHandle);
            writer.write("</playerName>");
        }
        writer.write("</players>");
        writer.write("</count>");
    }

    private void handleLeave(String gameId, String user, PrintWriter writer) {
        WebGame.leave(Integer.parseInt(gameId), user);
        writer.write("<ok/>");
    }

    private void handleJoinAI(String gameId, String handle, PrintWriter writer) {
        String user = WebGame.joinAI(Integer.parseInt(gameId), handle);
        if (user != null) {
            writer.write("<joininfo>");
            writer.write("<user>");
            writer.write(user);
            writer.write("</user>");
            writer.write("<gameid>");
            writer.write(gameId);
            writer.write("</gameid>");
            writer.write("</joininfo>");
        } else {
            writer.write("<fail/>");
        }
    }

    private void handleJoin(String gameId, String handle, PrintWriter writer) {
        String user = WebGame.join(Integer.parseInt(gameId), handle);
        if (user != null) {
            writer.write("<joininfo>");
            writer.write("<user>");
            writer.write(user);
            writer.write("</user>");
            writer.write("<gameid>");
            writer.write(gameId);
            writer.write("</gameid>");
            writer.write("</joininfo>");
        } else {
            writer.write("<fail/>");
        }
    }

    private void handleGetGameState(String gameId, PrintWriter writer) {
        JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
        if (userInterface != null) {
            GameState gameState = userInterface.getGameState();
            if (gameState != null) {
                writer.write("<gamestate>");
                writer.write("<players>");
                for (GameStatePlayer player : gameState.getPlayers()) {
                    if (userInterface instanceof WebGameUserInterface) {
                        player.user = ((WebGameUserInterface) userInterface).userFigureNames.get(player.name);
                    }
                    writePlayer(player, writer);
                }
                writer.write("</players>");
                if (gameState.timeout() != null) {
                    writer.write("<timeout>" + gameState.timeout() + "</timeout>");
                }
                if (gameState.isGameOver()) {
                    writer.write("<gameover/>");
                    Cleanup cleanup = new Cleanup(Integer.parseInt(gameId));
                    cleanup.start();
                    WebGame.removeGame(Integer.parseInt(gameId));
                }
                writer.write("<currentname>");
                writer.write(gameState.getCurrentName());
                writer.write("</currentname>");
                writer.write("<decksize>");
                writer.write(Integer.toString(gameState.getDeckSize()));
                writer.write("</decksize>");
                GameStateCard topCard = gameState.discardTopCard();
                if (topCard != null) {
                    writer.write("<discardtopcard>");
                    writeCard(topCard, writer);
                    writer.write("</discardtopcard>");
                }
                writer.write("<roles>");
                ArrayList<String> roles = userInterface.getRoles();
                for (String role : roles) {
                    writer.write("<role>" + role + "</role>");
                }
                writer.write("</roles>");
                writer.write("</gamestate>");
            } else {
                writer.write("<gamestate/>");
            }
        } else {
            writer.write("<gamestate/>");
        }
    }

    private void writePlayer(GameStatePlayer player, PrintWriter writer) {
        writer.write("<player>");
        writer.write("<handle>");
        writer.write(player.user);
        writer.write("</handle>");
        writer.write("<name>");
        writer.write(player.name);
        writer.write("</name>");
        writer.write("<specialability>");
        writer.write(player.specialAbility);
        writer.write("</specialability>");
        writer.write("<health>");
        writer.write(Integer.toString(player.health));
        writer.write("</health>");
        writer.write("<maxhealth>");
        writer.write(Integer.toString(player.maxHealth));
        writer.write("</maxhealth>");
        writer.write("<handsize>");
        writer.write(Integer.toString(player.handSize));
        writer.write("</handsize>");
        if (player.isSheriff) {
            writer.write("<issheriff/>");
        }
        if (player.gun != null) {
            writer.write("<gun>");
            writeCard(player.gun, writer);
            writer.write("</gun>");
        }
        List<GameStateCard> inPlay = player.inPlay;
        if (inPlay != null && !inPlay.isEmpty()) {
            writer.write("<inplay>");
            for (GameStateCard inPlayCard : inPlay) {
                writer.write("<inplaycard>");
                writeCard(inPlayCard, writer);
                writer.write("</inplaycard>");
            }
            writer.write("</inplay>");
        }
        writer.write("</player>");
    }

    private void writeCard(GameStateCard card, PrintWriter writer) {
        writer.write("<name>");
        writer.write(card.name);
        writer.write("</name>");
        writer.write("<suit>");
        writer.write(card.suit);
        writer.write("</suit>");
        writer.write("<value>");
        writer.write(card.value);
        writer.write("</value>");
        writer.write("<type>");
        writer.write(card.type);
        writer.write("</type>");
    }

    class Cleanup extends Thread {
        int gameId;

        Cleanup(int gameId) {
            this.gameId = gameId;
        }

        public void run() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
            }
            WebInit.remove(gameId);
        }
    }
}