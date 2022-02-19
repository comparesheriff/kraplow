import {setup} from "./setup.js";
import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";

export {parseGetAvailableGame, getAvailableGame}
function parseGetAvailableGame(responseJson) {
    if (Object.keys(responseJson).length === 0) return
    console.log("START: parseGetAvailableGame", responseJson)
    const gameNodes = responseJson["games"]
    let availableGames = [];
    let playerCounts = [];
    let canJoins = [];
    let playerHandles = [];
    for (let i = 0; i < gameNodes.length; i++) {
        const currentGame = gameNodes[i]
        const availableGameId = currentGame["gameid"];
        const availablePlayerCount = currentGame["playercount"];
        const canJoin = currentGame["canjoin"]
        availableGames.push(availableGameId);
        playerCounts.push(availablePlayerCount);
        const players = currentGame["players"]
        const playerArray = [];
        players.forEach(name => playerArray.push(name))
        playerHandles.push(playerArray);
        canJoins.push(canJoin);
    }
    console.log("END: parseGetAvailableGame", availableGames, playerCounts, canJoins, playerHandles)
    setup();
}

function getAvailableGame() {
    sendApiRequest(getServletUrl() + "?messageType=AVAILABLEGAMES", parseGetAvailableGame);
}