package com.chriscarr.game.ajax.handlers;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.JSPUserInterface;
import com.chriscarr.bang.userinterface.Message;
import com.chriscarr.bang.userinterface.WebGameUserInterface;
import com.chriscarr.game.WebInit;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.xml.MessageXmlWriter;

import java.util.ArrayList;
import java.util.List;

public final class MessageHandlers {
    private MessageHandlers() {
    }

    public static AjaxAction getMessage() {
        return (request, response) -> {
            String user = request.getParameter("user");
            String gameId = request.getParameter("gameId");
            JSPUserInterface ui =
                (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
            if (ui == null) {
                MessageXmlWriter.writeOk(response);
                return;
            }
            List<Message> messages = ((WebGameUserInterface) ui).getMessages(user);
            if (messages.isEmpty()) {
                MessageXmlWriter.writeOk(response);
                return;
            }
            Message first = messages.getFirst();
            ArrayList<String> cardNames = new ArrayList<>();
            String playerName = ((WebGameUserInterface) ui).getPlayerForUser(user);
            if (ui.isPlayerAlive(playerName)) {
                Hand hand = ui.getHandForUser(playerName);
                for (Card card : hand) {
                    cardNames.add(card.getName().getDisplayName());
                }
            }
            MessageXmlWriter.writeMessage(first.getId(), first.getMessage(), cardNames, response);
        };
    }

    public static AjaxAction sendResponse() {
        return (request, response) -> {
            String user = request.getParameter("user");
            String responseMessage = request.getParameter("response");
            String gameId = request.getParameter("gameId");
            request.getParameter("messageId");
            JSPUserInterface ui = (JSPUserInterface) WebInit.getUserInterface(Integer.parseInt(gameId));
            if (ui != null) {
                List<Message> messages = ((WebGameUserInterface) ui).getMessages(user);
                if (!messages.isEmpty()) {
                    messages.removeFirst();
                    if (responseMessage != null && !responseMessage.isEmpty()) {
                        ((WebGameUserInterface) ui).addResponse(user, responseMessage);
                    }
                }
            }
            MessageXmlWriter.writeOk(response);
        };
    }
}
