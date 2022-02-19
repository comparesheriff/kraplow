import {stateMachine} from "./statemachine.js";
import {countPlayers} from "./countPlayers.js";
import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";
import {writeInfo} from "./start.js";

export {parseJoinAi, parseJoin, join, joinAi}

function parseJoin(responseJson) {
    console.log("parseJoin", responseJson)

    if (Object.keys(responseJson).length !== 0) {
        stateMachine.user = responseJson["user"];
        stateMachine.gameId = responseJson["gameid"];
        document.getElementById('handle').value = stateMachine.user;
    } else {
        alert("Game Full, Sorry");
    }
    writeInfo();
    countPlayers(stateMachine.gameId);
}


function join(gameId, handle) {
    document.cookie = "handle=" + handle + "; expires=Thu, 18 Dec 2999 12:00:00 UTC";
    sendApiRequest(getServletUrl() + "?messageType=JOIN&gameId=" + gameId + "&handle=" + handle, parseJoin);
}

function parseJoinAi() {
    writeInfo();
    countPlayers(stateMachine.gameId);
}

function joinAi(gameId, ai, handle) {
    sendApiRequest(getServletUrl() + "?messageType=JOINAI&gameId=" + gameId + "&handle=" + handle, parseJoinAi);
}