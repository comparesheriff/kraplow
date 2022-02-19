import {stateMachine} from "./statemachine.js";
import {setup} from "./setup.js";
import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";
import {writeInfo} from "./start.js";

export {parseLeaveAI, leave, parseLeave}

function parseLeave(responseJson) {
    console.log("parseLeave", responseJson)
    const okNode = responseJson.ok;
    if (okNode) {
        stateMachine.user = null;
        stateMachine.gameId = null;
        stateMachine.playerCount = null;
        stateMachine.previousAvailable = null;
        stateMachine.startable = false;
        writeInfo();
        setup();
    }
}

function parseLeaveAI(responseJson) {
    console.log("parseLeaveAI", responseJson)
}

function leave(isAi, user, gameId) {
    if(isAi){
        sendApiRequest(getServletUrl() + "?messageType=LEAVE&user=" + user + "&gameId=" + gameId, parseLeaveAI);
    } else {
        sendApiRequest(getServletUrl() + "?messageType=LEAVE&user=" + user + "&gameId=" + gameId, parseLeave);
    }
}