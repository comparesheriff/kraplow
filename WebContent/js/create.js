import {join, joinAi} from "./join.js";
import {stateMachine} from "./statemachine.js";
import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";

export {parseCreate, addRobot, create}

function parseCreate(responseJson) {
    console.log("parseCreate", responseJson)
    const gameId = responseJson["gameid"];
    join(gameId, document.getElementById("handle").value);
    return gameId;
}

function addRobot(){
    joinAi(stateMachine.gameId, stateMachine.robotIndex, 'Robot ' + (stateMachine.robotIndex + 1));
}

function create() {
    let visibility = "public";
    if (document.getElementById("private").checked) {
        visibility = "private";
    }
    let sidestep = "";
    if (document.getElementById("sidestep").checked) {
        sidestep = "&sidestep=true";
        stateMachine.isSidestep = true;
    }
    sendApiRequest(getServletUrl() + "?messageType=CREATE&visibility=" + visibility + sidestep, parseCreate);
}