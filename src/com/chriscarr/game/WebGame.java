package com.chriscarr.game;

import com.chriscarr.bang.userinterface.WebGameUserInterface;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebGame {
    private static int gameCounter = 0;
    private static int guestCounter = 0;
    private static final Map<Integer, GamePrep> gamePreps = new ConcurrentHashMap<>();
    private static final Map<String, List<ChatMessage>> chatLogs = new ConcurrentHashMap<>();
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> gameHandles = new ConcurrentHashMap<>();

    static {
        chatLogs.put("lobby", new ArrayList<>());
    }

    public static int create(String visibility, boolean sidestep) {
        int gameId = gameCounter;
        GamePrep gamePrep = new GamePrep(visibility, sidestep);
        gamePreps.put(gameId, gamePrep);
        gameCounter++;
        chatLogs.put(Integer.toString(gameId), new ArrayList<>());
        gameHandles.put(Integer.toString(gameId), new ArrayList<>());
        return gameId;
    }

    public static List<String> getJoinedPlayers(int gameId) {
        GamePrep gamePrep = gamePreps.get(gameId);
        if (gamePrep != null) {
            return gamePrep.getJoinedPlayers();
        } else {
            return new ArrayList<>();
        }
    }

    public static String getUniqueHandle(int gameId, String handle) {
        handle = handle.replace("AI", "ai");
        handle = handle.replaceAll("[^a-zA-Z0-9]", "");
        List<String> handles = gameHandles.get(Integer.toString(gameId));
        while (handles.contains(handle)) {
            handle = handle + "_" + getNextGuestCounter();
        }
        handles.add(handle);
        return handle;
    }

    public static String join(int gameId, String handle) {
        return gamePreps.get(gameId).join(getUniqueHandle(gameId, handle));
    }

    public static String joinAI(int gameId, String handle) {
        return gamePreps.get(gameId).joinAI(getUniqueHandle(gameId, handle));
    }

    public static boolean canJoin(int gameId) {
        return gamePreps.get(gameId).canJoin();
    }

    public static void leave(int gameId, String joinNumber) {
        gamePreps.get(gameId).leave(joinNumber);
        List<String> handles = gameHandles.get(Integer.toString(gameId));
        handles.remove(joinNumber);
        if (gamePreps.get(gameId).getCountPlayers() == 0) {
            gamePreps.remove(gameId);
        }
    }

    public static int getCountPlayers(int gameId) {
        int timeoutMintues = 20;
        if (gamePreps.get(gameId) != null) {
            if (gamePreps.get(gameId).getLastUpdated() + (1000 * 60 * timeoutMintues) < System.currentTimeMillis()) {
                gamePreps.remove(gameId);
                return 0;
            }
            return gamePreps.get(gameId).getCountPlayers();
        }
        return 0;
    }

    public static boolean canStart(int gameId) {
        if (gamePreps.get(gameId) != null) {
            return gamePreps.get(gameId).canStart();
        }
        return false;
    }

    public static void start(int gameId, int aiSleepMs, String pRole, String pChar) {
        while (getJoinedPlayers(gameId).size() < 4) {
            joinAI(gameId, "ROBOT");
        }
        if (canStart(gameId)) {
            WebInit webInit = new WebInit();
            WebGameUserInterface x = new WebGameUserInterface(gamePreps.get(gameId).getJoinedPlayers(), aiSleepMs);
            webInit.setup(getCountPlayers(gameId), x, x, gameId, gamePreps.get(gameId).getSidestep(), pRole, pChar);
            gamePreps.remove(gameId);
        }
    }

    public static List<Integer> getAvailableGames() {
        ArrayList<Integer> results = new ArrayList<>();
        ArrayList<Integer> gameKeys = new ArrayList<>(gamePreps.keySet());
        for (Integer gameKey : gameKeys) {
            GamePrep prep = gamePreps.get(gameKey);
            String visibility = prep.getVisibility();
            if (visibility.equals("public")) {
                results.add(gameKey);
            }
        }
        return results;
    }

    public static void addChat(String chat, String gameId) {
        List<ChatMessage> chatLog = chatLogs.get(gameId);
        chatLog.add(new ChatMessage(chat));
        if (chatLog.size() > 50) {
            chatLog.remove(0);
        }
    }

    public static List<ChatMessage> getChatLog(String gameId) {
        return chatLogs.get(gameId);
    }

    public static void cleanSessions() {
        Iterator<String> sessionIter = sessions.keySet().iterator();
        long now = System.currentTimeMillis();
        while (sessionIter.hasNext()) {
            String sessionId = sessionIter.next();
            if ((sessions.get(sessionId).lastUpdated + 5000) < now) {
                Session session = sessions.get(sessionId);
                String handle = session.handle;
                ArrayList<GamePrep> allPreps = new ArrayList<>(gamePreps.values());
                for (GamePrep prep : allPreps) {
                    prep.leave(handle);
                }
                sessions.remove(sessionId);
            }
        }
    }

    public static void updateSession(String sessionId, String handle) {
        if (!sessionId.equals("null")) {
            //Removed new Long deprecated
            sessions.put(sessionId, new Session(System.currentTimeMillis(), handle));
            cleanSessions();
        }
    }

    public static int getNextGuestCounter() {
        return guestCounter++;
    }

    public static List<Session> getSessions() {
        return new ArrayList<>(sessions.values());
    }

    public static void removeGame(int gameId) {
        chatLogs.remove(Integer.toString(gameId));
        gameHandles.remove(Integer.toString(gameId));
    }
}