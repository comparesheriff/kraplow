import {stateMachine} from "./statemachine.js";
import {getCookie} from "./cookie.js";
import {getGuestCounter} from "./guestCounter.js";
import {getGameState} from "./gameState.js";
import {setup} from "./setup.js";
import {pollMessages} from "./message.js";
import {getChat} from "./chat.js";
import {getAvailableGame} from "./availableGame.js";
import {countPlayers} from "./countPlayers.js";
import {writeInfo} from "./start.js";

export {timedCount}

function timedCount() {
    if (stateMachine.guestCounter == null) {
        const cookieGuestCounter = getCookie("guestcounter");
        if (cookieGuestCounter !== "") {
            stateMachine.guestCounter = cookieGuestCounter;
        } else {
            getGuestCounter();
        }
    }
    if (stateMachine.gameOver === false) {
        if (stateMachine.gameId != null) {
            getGameState(stateMachine.gameId);
        }
        setTimeout(timedCount, 1000);
        setup();
        pollMessages();
        const handleField = document.getElementById('handle');
        const handle = handleField.value;
        getChat(stateMachine.guestCounter, handle);
        writeInfo();
        if (!stateMachine.started) {
            if (stateMachine.user == null) {
                getAvailableGame();
            }
            if (stateMachine.gameId != null) {
                countPlayers(stateMachine.gameId);
            }
        }
    }
}