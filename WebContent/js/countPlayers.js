import {canStart} from "./start.js";
import {setup} from "./setup.js";
import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";
import {stateMachine} from "./statemachine.js";

export {parseCountPlayers, countPlayers}

function parseCountPlayers(responseJson) {
    console.log("parseCountPlayers", responseJson)
    stateMachine.playerCount = responseJson["playercount"];
    const players = responseJson["players"];
    stateMachine.gamePlayerHandles = [];
    if (players !== null) {
        players.forEach(playerName => stateMachine.gamePlayerHandles.push(playerName))
    }
    canStart(stateMachine.gameId);
    setup();
    let maxPlayers = stateMachine.isSidestep ? 8 : 7;
    let newStyle = players.length < maxPlayers ? "block" : "none"
    if (document.getElementById("addcomputer") != null) {
        document.getElementById("addcomputer").style.display = newStyle;
    }

    let humans = 0;
    for (let j = 0; j < stateMachine.gamePlayerHandles.length; j++) {
        const last2 = stateMachine.gamePlayerHandles[j].slice(-2);
        if (last2 === "AI") {
        } else {
            humans += 1;
        }
    }
    if (document.getElementById("proleWrapper") != null && document.getElementById("pcharWrapper") != null) {
        if (humans === 1) {
            document.getElementById("proleWrapper").style.display = "block";
            document.getElementById("pcharWrapper").style.display = "block";
        } else {
            document.getElementById("proleWrapper").style.display = "none";
            document.getElementById("pcharWrapper").style.display = "none";
        }
    }
}

function countPlayers(gameId) {
    sendApiRequest(getServletUrl() + "?messageType=COUNTPLAYERS&gameId=" + gameId, parseCountPlayers);
}