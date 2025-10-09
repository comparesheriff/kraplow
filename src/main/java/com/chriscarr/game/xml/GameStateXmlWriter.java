package com.chriscarr.game.xml;

import com.chriscarr.bang.gamestate.GameState;
import com.chriscarr.bang.gamestate.GameStateCard;
import com.chriscarr.bang.gamestate.GameStatePlayer;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public final class GameStateXmlWriter {
    private GameStateXmlWriter() {
    }

    public static void writeGameState(GameState gameState, List<String> roles, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("<gamestate>");
        resp.getWriter().write("<players>");
        for (GameStatePlayer p : gameState.getPlayers()) {
            writePlayer(p, resp);
        }
        resp.getWriter().write("</players>");

        if (gameState.timeout() != null) {
            resp.getWriter().write("<timeout>");
            resp.getWriter().write(XmlUtil.escapeXml(gameState.timeout()));
            resp.getWriter().write("</timeout>");
        }

        resp.getWriter().write("<currentname>");
        resp.getWriter().write(XmlUtil.escapeXml(gameState.getCurrentName()));
        resp.getWriter().write("</currentname>");

        resp.getWriter().write("<decksize>");
        resp.getWriter().print(gameState.getDeckSize());
        resp.getWriter().write("</decksize>");

        gameState.discardTopCard().ifPresent(top -> {
            try {
                resp.getWriter().write("<discardtopcard>");
                writeCard(top, resp);
                resp.getWriter().write("</discardtopcard>");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        if (roles != null && !roles.isEmpty()) {
            resp.getWriter().write("<roles>");
            for (String role : roles) {
                resp.getWriter().write("<role>");
                resp.getWriter().write(XmlUtil.escapeXml(role));
                resp.getWriter().write("</role>");
            }
            resp.getWriter().write("</roles>");
        }

        resp.getWriter().write("</gamestate>");
    }

    public static void writePlayer(GameStatePlayer player, HttpServletResponse response) throws IOException {
        response.getWriter().write("<player>");
        response.getWriter().write("<handle>");
        response.getWriter().write(XmlUtil.escapeXml(player.user));
        response.getWriter().write("</handle>");
        response.getWriter().write("<name>");
        response.getWriter().write(XmlUtil.escapeXml(player.name));
        response.getWriter().write("</name>");
        response.getWriter().write("<specialability>");
        response.getWriter().write(XmlUtil.escapeXml(player.specialAbility));
        response.getWriter().write("</specialability>");
        response.getWriter().write("<health>");
        response.getWriter().print(player.health);
        response.getWriter().write("</health>");
        response.getWriter().write("<maxhealth>");
        response.getWriter().print(player.maxHealth);
        response.getWriter().write("</maxhealth>");
        response.getWriter().write("<handsize>");
        response.getWriter().print(player.handSize);
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

    public static void writeCard(GameStateCard card, HttpServletResponse response) throws IOException {
        response.getWriter().write("<name>");
        response.getWriter().write(XmlUtil.escapeXml(card.name.getDisplayName()));
        response.getWriter().write("</name>");
        response.getWriter().write("<suit>");
        response.getWriter().write(XmlUtil.escapeXml(card.suit));
        response.getWriter().write("</suit>");
        response.getWriter().write("<value>");
        response.getWriter().write(XmlUtil.escapeXml(card.value));
        response.getWriter().write("</value>");
        response.getWriter().write("<type>");
        response.getWriter().write(XmlUtil.escapeXml(card.type));
        response.getWriter().write("</type>");
    }
}
