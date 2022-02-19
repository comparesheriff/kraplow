import {join} from "./join.js";
import {create, addRobot} from "./create.js";
import {leave} from "./leave.js";
import {playerInfo} from "./playerInfo.js";
import {start} from "./start.js";
import {stateMachine} from "./statemachine.js";
export {setup}

function getNewAvailable(newAvailable) {
    newAvailable = "";
    for (let i = 0; i < stateMachine.availableGames.length; i++) {
        if (stateMachine.canJoins[i] === 'true') {
            newAvailable += '<div style="border:2px solid #000;"><div><div style="float:left;"><a id="joinLink"><div><img src="./../join.png" alt="Join" width="90"></div></a><input id="joinButton" type="button" value="Join"></div>';
        }
        if (stateMachine.playerHandles[i].length > 0) {
            newAvailable += '<div style="float:left;">';
            newAvailable += stateMachine.playerCounts[i] + ' players ';
            for (let j = 0; j < stateMachine.playerHandles[i].length; j++) {
                newAvailable += '<div style="float:left;"><div><img src="player.png" alt="' + stateMachine.playerHandles[i][j] + '" width="90"></div><div>' + stateMachine.playerHandles[i][j] + '</div></div> ';
            }
            newAvailable += '</div>';
        }
        newAvailable += '</div><div style="clear:both;"></div></div>';
    }
    if (newAvailable === stateMachine.previousAvailable) {
        return;
    } else {
        stateMachine.previousAvailable = newAvailable;
    }
    return newAvailable;
}


function handleStateMachineUserNull(result) {
    result += '<a id="leavelink"><div><img src="leave.png" alt="Leave" width="90"></div></a><input id="leavebutton" type="button" value="Leave Game">';
    result += '<p>Invite friends to join this game by getting them to put this link in their browsers address bar.</p><p><input type="text" readonly id="joinurl" value="http://chriscarr.name:8080/westerncardgame?join=' + stateMachine.gameId + '" style="width:350px"><input type="button" onclick="copyToClipboard()" value="Copy to Clipboard"></p>';
    result += '<p id="addcomputer"><input id="addrobotbutton" type="button" value="Add Computer Player +"></p>';
    result += '<p>Expansions: (Side Step - ' + stateMachine.isSidestep + ')</p>';
    result += '<p>Prefered options only work when playing alone against AI.</p>';
    result += '<p id="pcharWrapper">Prefered Character <select id="pchar"><option value="random">Random</option>';
    playerInfo.forEach(playerKey => {
            if (playerKey.expansion === "base" || (stateMachine.isSidestep && playerKey.expansion === "sidestep")) {
                result += "<option value='" + playerKey + "'>" + playerKey + " - " + playerInfo.description + "</option>";
            }
        }
    )
    result += '</select></p>';
    result += '<p id="proleWrapper">Prefered Role <select id="prole"><option value="random">Random</option>';
    result += '<option value="Sheriff">Sheriff - Kill the Outlaws and Renegades</option>';
    result += '<option value="Deputy">Deputy - Kill the Outlaws and Renegades - Protect the Sheriff</option>';
    result += '<option value="Outlaw">Outlaw - Kill the Sheriff</option>';
    result += '<option value="Renegade">Renegade - Kill everyone else, then the Sheriff</option>';
    result += '</select></p>';
    return result;
}

function setup() {
    const gameSetupDiv = document.getElementById('gameSetup');
    let result = "";
    if (!stateMachine.started) {
        if (stateMachine.user == null) {
            result += '<img src="logo2.png">'
            result += '<div style="position:relative;"><div style="float:left;"><div><img src="players.png" style="margin-left:auto; margin-right:auto; display:block;"></div><div style="width:75px; text-align:center;">4 - 7</div></div><div style="float:left;"><div><img src="age.png" style="margin-left:auto; margin-right:auto; display:block;"></div><div style="width:75px; text-align:center;">8+</div></div><div style="float:left;"><div><img src="time.png" style="margin-left:auto; margin-right:auto; display:block;"></div><div style="width:75px; text-align:center;">20 - 40 min</div></div></div><div style="clear:both;"></div>'
            let newAvailable = null;
            if (stateMachine.availableGames != null) {
                result += getNewAvailable(newAvailable);;
            }
            result += '<p><div id="startbutton" style="position:relative;"><a><img src="button.png" alt="start button"><div id="starttext" style="position:absolute; color:white; font-size:42px; left:56px; top:18px;">Play A Game</div></div></a></p>';
        } else {
            result = handleStateMachineUserNull(result);
        }
        if (stateMachine.playerCount != null) {
            result += '<div style="float:left;">';
            result += stateMachine.playerCount + ' players in game';
            result += '<div style="float:left;">';
            for (let j = 0; j < stateMachine.gamePlayerHandles.length; j++) {
                let removeAILink = stateMachine.gamePlayerHandles[j];
                const last2 = stateMachine.gamePlayerHandles[j].slice(-2);
                if (last2 === "AI") {
                    removeAILink = '<input id="leavebutton2" type="button" value="Remove - "/> ';
                }
                result += '<div style="float:left;"><div><img src="player.png" alt="' + stateMachine.gamePlayerHandles[j] + '" width="90"></div><div>' + removeAILink + '</div></div> ';
            }
            result += '</div>';
            result += '</div>'
        }
        if (stateMachine.gameId != null) {
            result += '<div id="giverbutton" style="float:left; position:relative;"><a id="giverlink"><img src="button.png" alt="start button"><div id="givertext" style="position:absolute; color:white; font-size:42px; left:76px; top:18px;">Start Game</div></div></a>'
        }
        result += '<div style="clear:both;"></div>';
    }
    if (stateMachine.setupResult !== result) {
        gameSetupDiv.innerHTML = result;
        stateMachine.setupResult = result;
        let joinLink = gameSetupDiv.querySelector("#joinLink")
        if(joinLink != null) joinLink.addEventListener('click', () => {join(' + stateMachine.availableGames[i] + ', '\'' + document.getElementById("handle").value + '\''); return false;})
        let joinButton = gameSetupDiv.querySelector("#joinButton")
        if(joinButton != null) joinButton.addEventListener('click', () => {join(' + stateMachine.availableGames[i] + ', '\'' + document.getElementById("handle").value + '\'');})
        let startButton = gameSetupDiv.querySelector("#startbutton");
        if(startButton != null) startButton.addEventListener('click', () => {create(); return false})
        let leaveButton = gameSetupDiv.querySelector("#leavebutton")
        if(leaveButton != null) leaveButton.addEventListener('click', () => leave(false, stateMachine.user, stateMachine.gameId))
        let leaveButton2 = gameSetupDiv.querySelector("#leavebutton2")
        if(leaveButton2 != null) leaveButton2.addEventListener('click', () => leave(true, stateMachine.gamePlayerHandles[' + j + '], stateMachine.gameId))
        let addrobotbutton = gameSetupDiv.querySelector("#addrobotbutton")
        if(addrobotbutton != null) addrobotbutton.addEventListener('click', () => addRobot())
        let giverlink = gameSetupDiv.querySelector("#giverlink")
        if(giverlink != null) giverlink.addEventListener('click', () => {start(stateMachine.gameId); return false;})
        let leaveLink = gameSetupDiv.querySelector("#leavelink")
        if(leaveLink != null) leaveLink.addEventListener('click', () => {leave(false, stateMachine.user, stateMachine.gameId); return false;})
    }

}