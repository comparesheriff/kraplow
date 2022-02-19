import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";
import {stateMachine} from "./statemachine.js";

export {parseGetPlayerInfo, getPlayerInfo}

function parseGetPlayerInfo(responseJson) {
    console.log("getPlayerInfo", responseJson)
    const name = responseJson.name;
    if (name != null) {
        stateMachine.playerName = name;
    }
    const role = responseJson.role;
    if (role != null) {
        stateMachine.role = role;
    }
    const goal = responseJson.goal;
    if (goal != null) {
        stateMachine.goal = goal;
    }
}

function getPlayerInfo(user, gameId) {
    sendApiRequest(getServletUrl() + "?messageType=GETPLAYERINFO&user=" + user + "&gameId=" + gameId, parseGetPlayerInfo);
}