import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";
import {stateMachine} from "./statemachine.js";

export {parseSendChat, sendChat, parseGetChat, getChat}

function parseSendChat(responseJson) {
    const handleField = document.getElementById('handle');
    const handle = handleField.value;
    getChat(stateMachine.guestCounter, handle);
}

function sendChat(chat) {
    let gameIdString = "";
    if (stateMachine.gameId != null) {
        gameIdString = "&gameid=" + stateMachine.gameId;
    }
    sendApiRequest(getServletUrl() + "?messageType=CHAT&chat=" + document.getElementById('handle').value + ": " + chat + gameIdString, parseSendChat);
}

function parseGetChat(responseJson) {
    console.log("parseGetChat", responseJson)
    const chatLogDiv = document.getElementById('chatLog');
    const chatNodes = responseJson["chatmessage"];
    let result = "<textarea rows=\"10\" cols=\"60\" id=\"chatarea\" readonly>";
    if (chatNodes !== undefined && Object.keys(chatNodes).length !== 0) {
        for (let i = 0; i < chatNodes.length; i++) {
            if (chatNodes[i].childNodes.length > 0) {
                const chatMessage = chatNodes[i];
                const messageNode = chatMessage["chat"];
                const timestampNode = chatMessage.timestamp;
                result += timestampNode[0].childNodes[0].nodeValue + ': ' + messageNode[0].childNodes[0].nodeValue + '\n';
            }
        }
    }
    result += "</textarea>";
    if (chatLogDiv.innerHTML !== result) {
        chatLogDiv.innerHTML = result;
        const textarea = document.getElementById('chatarea');
        textarea.scrollTop = textarea.scrollHeight;
    }
    const sessions = responseJson["session"];
    const chatNamesDiv = document.getElementById('chatNames');
    let namesResult = "<textarea rows=\"10\" cols=\"20\" id=\"chatarea\" readonly>";
    if(sessions != null) sessions.forEach(session => namesResult += session + '\n')
    namesResult += "</textarea>";
    if (chatNamesDiv.innerHTML !== namesResult) {
        chatNamesDiv.innerHTML = namesResult;
    }
}

function getChat(guestCounter, handle) {
    let gameIdString = "";
    if (stateMachine.gameId != null) {
        gameIdString = "&gameid=" + stateMachine.gameId;
    }
    sendApiRequest(getServletUrl() + "?messageType=GETCHAT&guestCounter=" + guestCounter + "&handle=" + handle + gameIdString, parseGetChat);
}