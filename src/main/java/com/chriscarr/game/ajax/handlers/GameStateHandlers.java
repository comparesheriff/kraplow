package com.chriscarr.game.ajax.handlers;

import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.userinterface.JSPUserInterface;
import com.chriscarr.bang.userinterface.WebGameUserInterface;
import com.chriscarr.game.WebGame;
import com.chriscarr.game.WebInit;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.xml.GameStateXmlWriter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class GameStateHandlers {
    private GameStateHandlers() {
    }

    public static AjaxAction getGameState(ScheduledExecutorService cleanup) {
        return (request, response) -> {
            String gameId = request.getParameter("gameId");
            JSPUserInterface ui = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
            if (ui == null) {
                response.getWriter().write("<gamestate/>");
                return;
            }
            GameState gameState = ui.getGameState();
            if (gameState == null) {
                response.getWriter().write("<gamestate/>");
                return;
            }

            if (ui instanceof WebGameUserInterface wui) {
                gameState.getPlayers().forEach(p -> p.user = wui.userFigureNames.get(p.name));
            }

            GameStateXmlWriter.writeGameState(gameState, ui.getRoles(), response);

            if (gameState.isGameOver()) {
                response.getWriter().write("<gameover/>");
                int gameIdInt = Integer.parseInt(gameId);
                WebGame.removeGame(Integer.parseInt(gameId));
                cleanup.schedule(() -> WebInit.remove(gameIdInt), 10, TimeUnit.SECONDS);
            }
        };
    }
}
