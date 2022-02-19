import {stateMachine} from "./statemachine.js";
import {getMessage} from "./message.js";
import {getGameState} from "./gameState.js";
import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";

export {parseSendResponse, sendResponseJson}

function parseSendResponse(responseJson) {
    console.log("sendResponse", responseJson)
    if (responseJson.ok) {
        const gameSetupDiv = document.getElementById('gameSetup');
        gameSetupDiv.innerHTML = "";
        if (stateMachine.gameId != null) {
            getMessage(stateMachine.user, stateMachine.gameId);
            getGameState(stateMachine.gameId);
        }
    }
}

function sendResponseJson(response, messageId) {
    sendApiRequest(getServletUrl() + "?messageType=SENDRESPONSE&user=" + stateMachine.user + "&response=" + response + "&gameId=" + stateMachine.gameId + "&messageId=" + messageId, parseSendResponse);
}