import {stateMachine} from "./statemachine.js";
import {setup} from "./setup.js";
import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";
import {getGameState} from "./gameState.js";

export {parseStart, canStart, parseCanStart, start, writeInfo}

function parseCanStart(responseJson) {
    console.log("parseCanStart", responseJson)
    stateMachine.startable = responseJson["yes"];
    setup();
    if (stateMachine.autostart === true && stateMachine.startable === true) {
        stateMachine.autostart = false;
        start(stateMachine.gameId);
    }
}

function canStart(gameId) {
    sendApiRequest(getServletUrl() + "?messageType=CANSTART&gameId=" + gameId, parseCanStart);
}

function parseStart(responseJson) {
    console.log("parseStart", responseJson)
    getGameState(stateMachine.gameId);
}

function start(gameId) {
    let pChar;
    let pRole;
    let aiSpeed = 0;
    if (document.getElementById("aiSpeedInstant").checked) {
        aiSpeed = document.getElementById("aiSpeedInstant").value;
    } else if (document.getElementById("aiSpeedFast").checked) {
        aiSpeed = document.getElementById("aiSpeedFast").value;
    } else if (document.getElementById("aiSpeedSlow").checked) {
        aiSpeed = document.getElementById("aiSpeedSlow").value;
    }
    //We will let you chose a role and char if you are playing alone against AI
    let humans = 0;
    for (let j = 0; j < stateMachine.gamePlayerHandles.length; j++) {
        const last2 = stateMachine.gamePlayerHandles[j].slice(-2);
        if (last2 === "AI") {

        } else {
            humans += 1;
        }
    }
    if (humans === 1) {
        pRole = document.getElementById("prole").value;
        pChar = document.getElementById("pchar").value;
    } else {
        pRole = "random";
        pChar = "random";
    }
    sendApiRequest(getServletUrl() + "?messageType=START&gameId=" + gameId + "&aiSleepMs=" + aiSpeed + "&pchar=" + pChar + "&prole=" + pRole, parseStart);
}

function writeInfo() {
    const gameInfoDiv = document.getElementById('gameInfo');
    if (stateMachine.gameId == null) {
        gameInfoDiv.style.display = "block";
    } else {
        gameInfoDiv.style.display = "none";
    }
}