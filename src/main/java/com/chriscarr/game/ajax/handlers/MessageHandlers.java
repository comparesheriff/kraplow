package com.chriscarr.game.ajax.handlers;

import com.chriscarr.bang.Hand;
import com.chriscarr.bang.cards.Card;
import com.chriscarr.bang.userinterface.JSPUserInterface;
import com.chriscarr.bang.userinterface.Message;
import com.chriscarr.bang.userinterface.WebGameUserInterface;
import com.chriscarr.game.WebInit;
import com.chriscarr.game.ajax.AjaxAction;
import com.chriscarr.game.http.ParamUtil;
import com.chriscarr.game.xml.GenericXmlWriter;
import com.chriscarr.game.xml.MessageXmlWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public final class MessageHandlers {
    private MessageHandlers() {
    }

    public static AjaxAction getMessage() {
        return (request, response) -> {
            String user = request.getParameter("user");
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            if (gameId == null) {
                return;
            }
            JSPUserInterface ui = (JSPUserInterface) WebInit.getUserInterface(gameId);
            if (ui == null) {
                GenericXmlWriter.writeOk(response);
                return;
            }
            List<Message> messages = ((WebGameUserInterface) ui).getMessages(user);
            if (messages.isEmpty()) {
                GenericXmlWriter.writeOk(response);
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
            Integer gameId = ParamUtil.intParamOr400(request, response, "gameId");
            if (gameId == null) {
                return;
            }
            OptionalInt messageId = ParamUtil.intParam(request.getParameter("messageId"));
            request.getParameter("messageId");
            JSPUserInterface ui = (JSPUserInterface) WebInit.getUserInterface(gameId);
            if (ui != null) {
                List<Message> messages = ((WebGameUserInterface) ui).getMessages(user);
                if (!messages.isEmpty()) {
                    Message first = messages.getFirst();
                    boolean shouldConsume = messageId.isEmpty() || (first.getId() == messageId.getAsInt());
                    if (shouldConsume) {
                        messages.removeFirst();
                        if (responseMessage != null && !responseMessage.isEmpty()) {
                            ((WebGameUserInterface) ui).addResponse(user, responseMessage);
                        }
                    }
                }
            }
            GenericXmlWriter.writeOk(response);
        };
    }
}
