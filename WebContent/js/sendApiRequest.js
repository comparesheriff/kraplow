export {getServletUrl, sendApiRequest}

function getServletUrl() {
    return "/westerncardgame/chat";
}

async function sendApiRequest(url, responseHandler) {
    const response = await fetch(url);
    const json = await response.json();
    if (response.status === 200) {
        responseHandler(json);
    } else {
        alert('error');
    }
}