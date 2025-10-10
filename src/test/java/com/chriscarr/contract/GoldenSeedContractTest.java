package com.chriscarr.contract;

import com.chriscarr.bang.Character;
import com.chriscarr.bang.Role;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.ajax.handlers.CommandHandlers;
import com.chriscarr.game.ajax.handlers.GameStateHandlers;
import com.chriscarr.game.ajax.handlers.JoinHandlers;
import com.chriscarr.game.ajax.handlers.MessageHandlers;
import com.chriscarr.game.http.TestHttpServletResponse;
import com.chriscarr.infra.Rng;
import com.chriscarr.infra.testing.DeterministicRng;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@DeterministicRng(seed = 1)
@ResourceLock("RNG")
public class GoldenSeedContractTest {
    private static final Pattern TAG_GAMEID = Pattern.compile("<gameid>(\\d+)</gameid>");
    private static final Pattern TAG_USER = Pattern.compile("<user>([^<]+)</user>");

    private static final ScheduledExecutorService CLEANUP = Executors.newSingleThreadScheduledExecutor();

    @AfterAll
    static void shutdown() {
        CLEANUP.shutdownNow();
    }

    @Test
    @Timeout(1)
    void sameSeed_producesIdenticalXmlSnapshots() throws Exception {
        Snap a = runScenario(42L);
        Snap b = runScenario(42L);
        assertEquals(a.gameStateXml, b.gameStateXml, "gamestate must be identical for same seeds");
        assertEquals(a.firstMessageXml, b.firstMessageXml, "message must be identical for same seeds");
    }

    @Test
    @Timeout(1)
    void differentSeed_producesDifferentXmlSnapshots() throws Exception {
        Snap a = runScenario(42L);
        Snap c = runScenario(43L);
        // Mindestens eine der beiden Snapshots sollte sich unterscheiden
        boolean differs = !a.gameStateXml.equals(c.gameStateXml) || !a.firstMessageXml.equals(c.firstMessageXml);
        assertTrue(differs, "at least one of gamestate/message should differ for different seeds");
    }

    /**
     * Ein kurzer Durchlauf von CREATE -> JOIN×4 -> START -> GETGAMESTATE -> GETMESSAGE(user1)
     */
    private static Snap runScenario(long seed) throws Exception {
        synchronized (Rng.class) {
            Rng.seed(seed);

            int gameId = createGame();
            String user1 = join(gameId, "Alfred");
            String user2 = join(gameId, "Bernhard");
            String user3 = join(gameId, "Caroline");
            String user4 = join(gameId, "Dagobert");

            // START: nutze gültige Enum-Namen dynamisch (erstes Element), damit der Test unabhängig von konkreten Namen ist
            String playerRole = Role.values()[0].name();
            String playerCharacter = Character.values()[0].name();
            startGame(gameId, 0, playerRole, playerCharacter);

            String firstMsg = xml_waitForFirstMessage(gameId, user1);
            String gameState = xml_waitForGameState(gameId);
            return new Snap(gameState, firstMsg);
        }
    }

    // ---------- Ajax-Helpers ----------
    private static int createGame() throws Exception {
        AjaxAction action = CommandHandlers.createGame();
        Map<String, String> p = Map.of("visibility", "public");
        var resp = call(action, p);
        Matcher m = TAG_GAMEID.matcher(resp);
        assertTrue(m.find(), "CREATE must return <gameid>...</gameid>");
        return Integer.parseInt(m.group(1));
    }

    private static String join(int gameId, String handle) throws Exception {
        AjaxAction action = JoinHandlers.join();
        Map<String, String> p = new HashMap<>();
        p.put("gameId", String.valueOf(gameId));
        p.put("handle", handle);
        var resp = call(action, p);
        Matcher m = TAG_USER.matcher(resp);
        assertTrue(m.find(), "JOIN must return <user>...</user>");
        return m.group(1);
    }

    private static void startGame(int gameId, int aiSleepMs, String prole, String pchar) throws Exception {
        AjaxAction action = CommandHandlers.startGame();
        Map<String, String> p = new HashMap<>();
        p.put("gameId", String.valueOf(gameId));
        p.put("aiSleepMs", String.valueOf(aiSleepMs));
        p.put("prole", prole);
        p.put("pchar", pchar);
        var resp = call(action, p);
        assertTrue(resp.contains("<ok/>"), "START must respond with <ok/>");
    }

    /**
     * Wartet, bis GETGAMESTATE nicht mehr das leere <gamestate/> liefert.
     */
    private static String xml_waitForGameState(int gameId) throws Exception {
        for (int i = 0; i < 10_000; i++) {
            String xml = normalize(xml_getGameState(gameId));
            // Handler gibt <gamestate/> zurück, solange UI/GameState noch nicht bereit ist. (siehe Handler)
            if (!xml.contains("<gamestate/>")) return xml;
            Thread.onSpinWait();
        }
        return normalize(xml_getGameState(gameId)); // Fallback für Assertion-Output
    }

    /**
     * Wartet, bis GETMESSAGE eine <message>…</message> liefert (statt <ok/> bei leerer Queue).
     */
    private static String xml_waitForFirstMessage(int gameId, String user) throws Exception {
        for (int i = 0; i < 10_000; i++) {
            String xml = normalize(xml_getMessage(gameId, user));
            // Handler liefert <ok/>, wenn noch keine Messages vorhanden sind. (siehe Handler)
            if (xml.contains("<message>")) return xml;
            Thread.onSpinWait();
        }
        return normalize(xml_getMessage(gameId, user));
    }

    private static String xml_getGameState(int gameId) throws Exception {
        AjaxAction action = GameStateHandlers.getGameState(CLEANUP);
        Map<String, String> p = Map.of("gameId", String.valueOf(gameId));
        return call(action, p);
    }

    private static String xml_getMessage(int gameId, String user) throws Exception {
        AjaxAction action = MessageHandlers.getMessage();
        Map<String, String> p = new HashMap<>();
        p.put("gameId", String.valueOf(gameId));
        p.put("user", user);
        return call(action, p);
    }

    private static String call(AjaxAction action, Map<String, String> params) throws Exception {
        HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        // generischer Parameter-Zugriff
        Mockito.when(req.getParameter(Mockito.anyString()))
            .thenAnswer(inv -> params.get(inv.getArgument(0)));
        // manche Handler fragen getParameterMap() ab (CREATE->sidestep)
        Mockito.when(req.getParameterMap()).thenReturn(Map.of());
        TestHttpServletResponse testHttpServletResponse = new TestHttpServletResponse();
        action.handle(req, testHttpServletResponse);
        return testHttpServletResponse.getBody().replace("\r", "");

    }

    private static String normalize(String xml) {
        // 1) Whitespace stabilisieren
        String compact = xml.replaceAll(">\\s+<", "><");
        // 2) Message-IDs sind global inkrementell (static AtomicInteger) und damit
        //    nicht deterministisch pro JVM-Lauf → für den Contractvergleich neutralisieren.
        return compact.replaceAll("<id>\\d+</id>", "<id>_</id>");
    }

    private record Snap(String gameStateXml, String firstMessageXml) {
    }
}
