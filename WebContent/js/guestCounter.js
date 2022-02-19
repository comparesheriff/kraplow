import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";

export {parseGetGuestCounter, getGuestCounter}

function parseGetGuestCounter(responseJson) {
    console.log("parseGetGuestCounter", responseJson)
    document.cookie = "guestcounter=" + responseJson["guestcounter"];
}

function getGuestCounter() {
    sendApiRequest(getServletUrl() + "?messageType=GETGUESTCOUNTER", parseGetGuestCounter);
}