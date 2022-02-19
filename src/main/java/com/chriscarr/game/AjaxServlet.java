package com.chriscarr.game;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.gamestate.GameStatePlayer;
import com.chriscarr.bang.models.servlet.MessageType;
import com.chriscarr.bang.models.servlet.json.*;
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
        int gameId = -1;
        try{
            gameId = Integer.parseInt(request.getParameter("gameId"));
        }catch (NumberFormatException ignored){}
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
        JsonResponseModel responseModel = null;
        switch (messageType) {
            case GETGAMESTATE: {
                responseModel = handleGetGameState(gameId);
                break;
            }
            case JOIN: {
                responseModel = handleJoin(gameId, handle);
                break;
            }
            case JOINAI: {
                responseModel = handleJoinAI(gameId, handle);
                break;
            }
            case LEAVE: {
                responseModel = handleLeave(gameId, user);
                break;
            }
            case COUNTPLAYERS: {
                responseModel = handleCountPlayers(gameId);
                break;
            }
            case GETGUESTCOUNTER:
                responseModel = handleGetGuestCounter();
                break;
            case AVAILABLEGAMES:
                responseModel = handleAvailableGames();
                break;
            case CANSTART: {
                responseModel = handleCanStart(gameId);
                break;
            }
            case CHAT: {
                responseModel = handleChat(gameId, chat);
                break;
            }
            case GETCHAT: {
                responseModel = handleGetChat(gameId, handle, guestCounter);
                break;
            }
            case START: {
                responseModel = handleStart(gameId, aiSleepMs, pRole, pChar);
                break;
            }
            case CREATE: {
                responseModel = handleCreate(visibility, sidestep);
                break;
            }
            case GETMESSAGE: {
                responseModel = handleGetMessage(gameId, user);
                break;
            }
            case SENDRESPONSE: {
                responseModel = handleSendResponse(gameId, user, messageId, responseMessage);
                break;
            }
            case GETPLAYERINFO: {
                responseModel = handleGetPlayerInfo(gameId, user);
                break;
            }
        }
        if(responseModel != null){
            writer.write(responseModel.toJsonString());
        }
    }

    private JsonResponseModel handleGetPlayerInfo(int gameId, String user) {
        JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(gameId);
        if (userInterface instanceof WebGameUserInterface) {
            String name = ((WebGameUserInterface) userInterface).getPlayerForUser(user);
            PlayerInfoResponseModel playerInfoResponseModel = new PlayerInfoResponseModel();
            playerInfoResponseModel.setName(name);
            playerInfoResponseModel.setRole(userInterface.getRoleForName(name));
            playerInfoResponseModel.setGoal(userInterface.getGoalForName(name));
            return playerInfoResponseModel;
        } else {
            JsonResponseModel jsonResponseModel = new JsonResponseModel();
            jsonResponseModel.setOverwriteJson("{\"ok\":\"true\"}");
            return jsonResponseModel;
        }
    }

    private JsonResponseModel handleSendResponse(int gameId, String user, String messageId, String responseMessage) {
        System.out.println("Sent Response");
        System.out.println("Response " + messageId);
        JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(gameId);
        if (userInterface instanceof WebGameUserInterface) {
            List<Message> messages = ((WebGameUserInterface) userInterface).getMessages(user);
            if (!messages.isEmpty()) {
                Object removed = messages.remove(0);
                System.out.println("Removed " + removed);
                if (!"".equals(responseMessage)) {
                    ((WebGameUserInterface) userInterface).addResponse(user, responseMessage);
                }
            }
        }
        JsonResponseModel jsonResponseModel = new JsonResponseModel();
        jsonResponseModel.setOverwriteJson("{\"ok\":\"true\"}");
        return jsonResponseModel;
    }

    private JsonResponseModel handleGetMessage(int gameId, String user) {
        JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(gameId);
        if (userInterface != null) {
            List<Message> messages = ((WebGameUserInterface) userInterface).getMessages(user);
            if (!messages.isEmpty()) {
                MessageResponseModel messageResponseModel = new MessageResponseModel();
                messageResponseModel.setId(messages.get(0).getId());
                messageResponseModel.setText(messages.get(0).getMessage());
                if (userInterface.isPlayerAlive(((WebGameUserInterface) userInterface).getPlayerForUser(user))) {
                    Hand hand = userInterface.getHandForUser(((WebGameUserInterface) userInterface).getPlayerForUser(user));
                    HandResponseModel handResponseModel = new HandResponseModel();
                    for (int i = 0; i < hand.size(); i++) {
                        Card card = hand.get(i);
                        handResponseModel.addCard(card.getName());
                    }
                    messageResponseModel.setHand(handResponseModel);
                }
                return messageResponseModel;
            }
        }
        JsonResponseModel jsonResponseModel = new JsonResponseModel();
        jsonResponseModel.setOverwriteJson("{\"ok\":\"true\"}");
        return jsonResponseModel;
    }

    private JsonResponseModel handleCreate(String visibility, boolean sidestep) {
        int gameId = WebGame.create(visibility, sidestep);
        JsonResponseModel jsonResponseModel = new JsonResponseModel();
        jsonResponseModel.setOverwriteJson("{\"gameid\":\""+gameId+"\"}");
        return jsonResponseModel;
    }

    private JsonResponseModel handleStart(int gameId, String aiSleepMs, String pRole, String pChar) {
        WebGame.start(gameId, Integer.parseInt(aiSleepMs), pRole, pChar);
        JsonResponseModel jsonResponseModel = new JsonResponseModel();
        jsonResponseModel.setOverwriteJson("{\"ok\":\"true\"}");
        return jsonResponseModel;
    }

    private JsonResponseModel handleGetChat(int gameId, String handle, String guestCounter) {
        WebGame.updateSession(guestCounter, handle);
        List<ChatMessage> chatLog = WebGame.getChatLog(gameId);
        ChatsResponseModel chatsResponseModel = new ChatsResponseModel();
        for (ChatMessage chat : chatLog) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            ChatMessageResponseModel chatMessageResponseModel = new ChatMessageResponseModel(chat.message, sdf.format(chat.timestamp));
            chatsResponseModel.addChatMessage(chatMessageResponseModel);
        }
        List<Session> sessions = WebGame.getSessions();
        for (Session session : sessions) {
            String outHandle = session.handle == null ? "Unknown" : session.handle;
            chatsResponseModel.addSession(outHandle);
        }
        return chatsResponseModel;
    }

    private JsonResponseModel handleChat(int gameId, String chat) {
        chat = chat.replace(">", "");
        chat = chat.replace("<", "");
        System.out.println("chat:" + chat);
        WebGame.addChat(chat, gameId);
        JsonResponseModel jsonResponseModel = new JsonResponseModel();
        jsonResponseModel.setOverwriteJson("{\"ok\":\"true\"}");
        return jsonResponseModel;
    }

    private JsonResponseModel handleCanStart(int gameId) {
        JsonResponseModel jsonResponseModel = new JsonResponseModel();
        if (WebGame.canStart(gameId)) {
            jsonResponseModel.setOverwriteJson("{\"yes\":\"true\"}");
        } else {
            jsonResponseModel.setOverwriteJson("{\"no\":\"true\"}");
        }
        return jsonResponseModel;
    }

    private JsonResponseModel handleAvailableGames() {
        AvailableGamesResponseModel availableGamesResponseModel = new AvailableGamesResponseModel();
        List<Integer> availableGames = WebGame.getAvailableGames();
        for (Integer availableGame : availableGames) {
            GameResponseModel gameResponseModel = new GameResponseModel();
            gameResponseModel.setGameId(availableGame);
            gameResponseModel.setPlayerCount(WebGame.getCountPlayers(availableGame));
            gameResponseModel.setCanJoin(WebGame.canJoin(availableGame));
            List<String> joinedPlayers = WebGame.getJoinedPlayers(availableGame);
            for (String playerHandle : joinedPlayers) {
                gameResponseModel.addPlayer(playerHandle);
            }
            availableGamesResponseModel.addGame(gameResponseModel);
        }
        return availableGamesResponseModel;
    }

    private JsonResponseModel handleGetGuestCounter() {
        JsonResponseModel jsonResponseModel = new JsonResponseModel();
        jsonResponseModel.setOverwriteJson("{\"guestcounter\":\""+WebGame.getNextGuestCounter()+"\"}");
        return jsonResponseModel;
    }

    private JsonResponseModel handleCountPlayers(int gameId) {
        CountPlayersResponseModel countPlayersResponseModel = new CountPlayersResponseModel();
        countPlayersResponseModel.setPlayerCount(WebGame.getCountPlayers(gameId));
        List<String> joinedPlayers = WebGame.getJoinedPlayers(gameId);
        for (String playerHandle : joinedPlayers) {
            countPlayersResponseModel.addPlayer(playerHandle);
        }
        return countPlayersResponseModel;
    }

    private JsonResponseModel handleLeave(int gameId, String user) {
        WebGame.leave(gameId, user);
        JsonResponseModel jsonResponseModel = new JsonResponseModel();
        jsonResponseModel.setOverwriteJson("{\"ok\":\"true\"}");
        return jsonResponseModel;
    }

    private JsonResponseModel handleJoinAI(int gameId, String handle) {
        String user = WebGame.joinAI(gameId, handle);
        return getJoinInfoForUser(user, gameId);
    }

    private JsonResponseModel handleJoin(int gameId, String handle) {
        String user = WebGame.join(gameId, handle);
        return getJoinInfoForUser(user, gameId);
    }

    private JsonResponseModel getJoinInfoForUser(String user, int gameId) {
        if (user != null) {
            return new JoinInfoResponseModel(user, gameId);
        } else {
            JsonResponseModel jsonResponseModel = new JsonResponseModel();
            jsonResponseModel.setOverwriteJson("{\"fail\":\"true\"}");
            return jsonResponseModel;
        }
    }

    private JsonResponseModel handleGetGameState(int gameId) {
        JSPUserInterface userInterface = (JSPUserInterface) WebInit.getUserInterface(gameId);
        GameStateResponseModel gameStateResponseModel = new GameStateResponseModel();
        if (userInterface != null) {
            GameState gameState = userInterface.getGameState();
            if (gameState != null) {
                for (GameStatePlayer player : gameState.getPlayers()) {
                    if (userInterface instanceof WebGameUserInterface) {
                        player.user = ((WebGameUserInterface) userInterface).userFigureNames.get(player.name);
                    }
                    PlayerResponseModel playerResponseModel = writePlayer(player);
                    gameStateResponseModel.addPlayer(playerResponseModel);
                }
                gameStateResponseModel.setTimeOut(gameState.timeout() != null);
                if (gameState.isGameOver()) {
                    gameStateResponseModel.setGameOver(true);
                    Cleanup cleanup = new Cleanup(gameId);
                    cleanup.start();
                    WebGame.removeGame(gameId);
                }
                gameStateResponseModel.setCurrentName(gameState.getCurrentName());
                gameStateResponseModel.setDeckSize(gameState.getDeckSize());
                GameStateCard topCard = gameState.discardTopCard();
                if (topCard != null) {
                    CardResponseModel cardResponseModel = writeCard(topCard);
                    gameStateResponseModel.setDiscardTopCard(cardResponseModel);
                }
                ArrayList<String> roles = userInterface.getRoles();
                for (String role : roles) {
                    RoleResponseModel roleResponseModel = new RoleResponseModel(role);
                    gameStateResponseModel.addRole(roleResponseModel);
                }
            }
        }
        return gameStateResponseModel;
    }

    private PlayerResponseModel writePlayer(GameStatePlayer player) {
        PlayerResponseModel playerResponseModel = new PlayerResponseModel();
        playerResponseModel.setHandle(player.user);
        playerResponseModel.setName(player.name);
        playerResponseModel.setSpecialAbility(player.specialAbility);
        playerResponseModel.setHealth(player.health);
        playerResponseModel.setMaxHealth(player.maxHealth);
        playerResponseModel.setHandSize(player.handSize);
        playerResponseModel.setSheriff(player.isSheriff);
        if (player.gun != null) {
            CardResponseModel gun = writeCard(player.gun);
            playerResponseModel.setGun(gun);
        }
        List<GameStateCard> inPlay = player.inPlay;
        if (inPlay != null && !inPlay.isEmpty()) {
            for (GameStateCard inPlayItem : inPlay) {
                CardResponseModel inPlayCard = writeCard(inPlayItem);
                playerResponseModel.addInPlay(inPlayCard);
            }
        }
        return playerResponseModel;
    }

    private CardResponseModel writeCard(GameStateCard card) {
        CardResponseModel cardResponseModel = new CardResponseModel();
        cardResponseModel.setName(card.name);
        cardResponseModel.setSuit(card.suit);
        cardResponseModel.setValue(card.value);
        cardResponseModel.setType(card.type);
        return cardResponseModel;
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