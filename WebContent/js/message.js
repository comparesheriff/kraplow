import {stateMachine} from "./statemachine.js";
import {getDescriptionForCard, getImageForCard} from "./cardInfo.js";
import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";
import {commandName} from "./commandName.js";
import {generateTargetCode, generateTargetCodeSingle} from "./generateCodes.js";
import {getDescriptionForPlayer, getImageForPlayer} from "./playerInfo.js";
import {flashBorder} from "./flashBorder.js";
import {sendResponseJson} from "./response.js";
import {getPlayerInfo} from "./playerInfoApi.js";

export {parseGetMessage, formatMessage, getMessage, pollMessages}

function parseGetMessage(responseJson) {
    console.log("parseGetMessage", responseJson)
    const messageObject = responseJson.message;
    const gameMessagesDiv = document.getElementById('gameMessages');
    if (messageObject == null || stateMachine.goal == null) {
        stateMachine.messageId = null;
        gameMessagesDiv.innerHTML = "<h2>Waiting for other players to perform actions.</h2>";
        return
    }

    const id = messageObject["id"];
    const text = messageObject["text"];
    const hand = messageObject["hand"];
    if (id !== stateMachine.messageId) {
        stateMachine.messageId = id;
        gameMessagesDiv.innerHTML = formatMessage(text, id);
        let cardNodes = hand["card"];
        const handAreaDiv = document.getElementById('handArea');
        let cardString = '<h2>Your Hand</h2>';
        cardNodes.forEach(card => cardString += '<img height="90" src="' + getImageForCard(card) + '" alt="' + card + '" title="' + card + ' - ' + getDescriptionForCard(card) + '">')
        handAreaDiv.innerHTML = cardString;
    }
}

function getMessage(servletUrl, responseHandler, user, gameId) {
    sendApiRequest(getServletUrl() + "?messageType=GETMESSAGE&user=" + user + "&gameId=" + gameId, parseGetMessage);
}

function formatMessage(message, messageId) {
    let canPlay;
    let cardName;
    let image;
    let description;
    let i;
    let result = "";
    if (message.indexOf("-") !== -1) {
        const splitMessage = message.split("-");
        const commandData = splitMessage[1];
        const commandIndex = commandData.indexOf(" ");
        if (commandIndex !== -1) {
            const command = commandData.substring(0, commandIndex);
            result += '<h2>' + commandName(command) + '</h2>';
            const data = commandData.substring(commandIndex + 1);
            let splitData = data.split(", ");
            if (command.indexOf("chooseTwoDiscardForLife") !== -1 || command.indexOf("chooseTwoDiscardForShoot") !== -1) {
                stateMachine.selected = ",";
                result += "<a href='' onClick='sendResponse(selected, " + messageId + "); selected = \",\"; return false;'><img src='ok.png' alt='ok' height='90'></a> ";
                for (i = 0; i < splitData.length; i++) {
                    if (splitData[i] !== "") {
                        description = " - " + getDescriptionForCard(splitData[i]);
                        image = getImageForCard(splitData[i]);
                        result += '<a href="" onClick="document.getElementById(\'result' + i + '\').style.display = \'none\'; document.getElementById(\'discard' + i + '\').style.display = \'inline\'; selected += \'' + i + ',\'; return false;"><img src="' + image + '" alt="Keeping ' + splitData[i] + '" title="' + splitData[i] + description + '" id="result' + i + '" height="90"></a> ';
                        result += '<a href="" onClick="document.getElementById(\'discard' + i + '\').style.display = \'none\'; document.getElementById(\'result' + i + '\').style.display = \'inline\'; selected = selected.replace(\',' + i + ',\', \',\'); return false;"><img src="card.png" style="display:none;" alt="Discarding ' + splitData[i] + '" title="' + splitData[i] + description + '" id="discard' + i + '" height="90"></a> ';
                    }
                }
            } else if (command.indexOf("respondTwoMiss") !== -1) {
                stateMachine.selected = ",";
                result += "<img src='ok.png' alt='ok' onClick='sendResponse(selected, " + messageId + "); selected = \",\"; return false;' height='90'> ";
                for (i = 0; i < splitData.length; i++) {
                    if (splitData[i] !== "") {
                        const nameCanPlaySplit = splitData[i].split("@");
                        cardName = nameCanPlaySplit[0];
                        canPlay = nameCanPlaySplit[1];
                        description = " - " + getDescriptionForCard(cardName);
                        image = getImageForCard(cardName);
                        if (canPlay.indexOf("false") !== -1) {
                            result += '<img src="' + image + '" alt="Can not play" title="' + splitData[i] + description + '" id="result' + i + '" style="opacity:0.4;" height="90"> ';
                        } else {
                            result += '<a href="" onClick="document.getElementById(\'result' + i + '\').style.display = \'none\'; document.getElementById(\'discard' + i + '\').style.display = \'inline\';  selected += \'' + i + ',\'; return false;"><img src="' + image + '" alt="Keeping ' + splitData[i] + '" title="' + splitData[i] + description + '" id="result' + i + '" height="90"></a> ';
                            result += '<a href="" onClick="document.getElementById(\'discard' + i + '\').style.display = \'none\'; document.getElementById(\'result' + i + '\').style.display = \'inline\'; selected = selected.replace(\',' + i + ',\', \',\'); return false;"><img src="card.png" style="display:none;" alt="Discarding ' + splitData[i] + '" title="' + splitData[i] + description + '" id="discard' + i + '" height="90"></a> ';
                        }
                    }
                }
            } else {
                if (command.indexOf("chooseDiscard") !== -1) {
                    result += "<a href='' onClick='sendResponse(-1, " + messageId + "); return false;'><img src='card.png' alt='Draw Pile' height='90'></a> ";
                } else if (command.indexOf("askOthersCard") !== -1) {
                    if (splitData[0].indexOf("true") !== -1) {
                        result += "<a href='' onClick='sendResponse(-1, " + messageId + "); return false;'><img src='hand.png' alt='Players hand' height='90'></a> ";
                    }
                    if (splitData[1].indexOf("true") !== -1) {
                        const gunName = splitData[1].substring(4);
                        result += "<a href='' onClick='sendResponse(-2, " + messageId + "); return false;'><img src='" + getImageForCard(gunName) + "' alt='" + gunName + "' height='90'></a> ";
                    }
                    splitData = splitData.slice(2, splitData.length);
                } else if ((command.indexOf("askPlay") !== -1 && command.indexOf("askPlayer") === -1) || command.indexOf("respondBeer") !== -1 || command.indexOf("respondBang") !== -1 || command.indexOf("respondMiss") !== -1 || command.indexOf("askBlueDiscard") !== -1) {
                    result += "<a href='' onClick='sendResponse(-1, " + messageId + "); return false;'><img src='pass.png' alt='Done Playing' title='Done Playing' height='90'></a> ";
                }
                for (i = 0; i < splitData.length; i++) {
                    let targetCode = "";
                    canPlay = true;
                    let altTargetText = "";
                    if (command.indexOf("askPlay") !== -1 || command.indexOf("respondBeer") !== -1 || command.indexOf("respondBang") !== -1 || command.indexOf("respondMiss") !== -1) {
                        const canPlayTargets = splitData[i].split("@");
                        splitData[i] = canPlayTargets[0];
                        if (canPlayTargets.length === 3) {
                            if (canPlayTargets[1].indexOf("false") !== -1) {
                                canPlay = false;
                            } else {
                                let targets = canPlayTargets[2];
                                const splitTargets = targets.split("$");
                                targets = targets.substring(0, targets.length - 1);
                                altTargetText = " Targets: " + targets.replace(/\$/g, ", ");
                                targetCode = generateTargetCode(splitTargets);

                            }
                        } else if (canPlayTargets.length === 2) {
                            if (canPlayTargets[1].indexOf("false") !== -1) {
                                canPlay = false;
                            }
                        }
                    }
                    if ("" !== splitData[i].trim()) {
                        let image = null;
                        let suit = null;
                        let value = null;
                        description = "";
                        if (command.indexOf("askPlayer") !== -1) {
                            targetCode = generateTargetCodeSingle(splitData[i]);
                            image = getImageForPlayer(splitData[i]);
                            description = getDescriptionForPlayer(splitData[i]);
                            if (description !== "") {
                                description = " - " + description;
                            }
                        } else {
                            cardName = splitstatemachineData[i];
                            description = " - " + getDescriptionForCard(cardName);
                            image = getImageForCard(cardName);
                            const suits = cardName.split("^");
                            if (suits.length === 3) {
                                splitData[i] = suits[0];
                                suit = suits[1];
                                value = suits[2];
                            }
                        }
                        if (canPlay) {
                            result += '<a href="" onClick="sendResponse(' + i + ', ' + messageId + '); return false;"><img src="' + image + '" alt="' + splitData[i] + altTargetText + '" title="' + splitData[i] + description + '" ' + targetCode + ' height="90"></a> ';
                        } else {
                            result += '<img src="' + image + '" alt="Can not play ' + splitData[i] + '" title="' + splitData[i] + description + '" ' + targetCode + ' style="opacity:0.4;" height="90"> ';
                        }
                        if (suit != null && value != null) {
                            result += value;
                            if (suit === 'Clubs') {
                                result += '<img src="clubs.png" alt="Clubs" title="Clubs"> ';
                            } else if (suit === 'Spades') {
                                result += '<img src="spades.png" alt="Spades" title="Spades"> ';
                            } else if (suit === 'Hearts') {
                                result += '<img src="hearts.png" alt="Hearts" title="Hearts"> ';
                            } else if (suit === 'Diamonds') {
                                result += '<img src="diamonds.png" alt="Diamonds" title="Diamonds"> ';
                            }
                        }
                    }
                }
            }

        } else {
            result += '<h2>' + commandName(commandData) + '</h2>';
            if (commandData === "chooseFromPlayer") {
                result += "<a href='' onClick='sendResponse(\"true\", " + messageId + "); return false;'><img src='player.png' alt='Players hand' height='90'></a> ";
            }
            result += "<a href='' onClick='sendResponse(\"false\", " + messageId + "); return false;'><img src='card.png' alt='Draw Pile' height='90'></a> ";
        }
    } else {
        const gameLogDiv = document.getElementById('gameLog');
        gameLogDiv.style.display = "block";
        stateMachine.info = stateMachine.info + '\n' + message;
        gameLogDiv.innerHTML = stateMachine.info;
        const splitInfo = stateMachine.info.split("\n");
        const names = ["Calamity Janet", "Jourdonnais", "Paul Regret", "Kit Carlson", "Bart Cassidy", "Jesse Jones", "Pedro Ramirez", "El Gringo", "Rose Doolan", "Suzy Lafayette", "Black Jack", "Sid Ketchum", "Slab the Killer", "Lucky Duke", "Vulture Sam", "Willy the Kid", "Uncle Will", "Johnny Kisch", "Claus The Saint"];
        const lastInfo = splitInfo[splitInfo.length - 1];
        for (i = 0; i < names.length; i++) {
            if (lastInfo.includes(names[i])) {
                flashBorder(names[i], true);
            }
        }
        for (i = 0; i < names.length; i++) {
            if (lastInfo.includes(names[i])) {
                flashBorder(names[i]);
            }
        }

        gameLogDiv.scrollTop = gameLogDiv.scrollHeight;
        sendResponseJson("", messageId);
    }
    if (result === "") {
        result = "<h2>Waiting for other players to perform actions.</h2>";
    }
    return result;
}

function pollMessages() {
    if (stateMachine.started) {
        if (stateMachine.goal == null) {
            getPlayerInfo(stateMachine.user, stateMachine.gameId);
        }
        getMessage(stateMachine.user, stateMachine.gameId);
    }
}