const BASE = "/westerncardgame/chat";
const enc = s => encodeURIComponent(s ?? "");

async function fetchXml(url) {
    const res = await fetch(url, {headers: {Accept: "application/xml"}, cache: "no-store"});
    const txt = await res.text();
    return new DOMParser().parseFromString(txt, "application/xml");
}

export async function listAvailableGames() {
    const xml = await fetchXml(`${BASE}?messageType=AVAILABLEGAMES`);
    return [...xml.getElementsByTagName("game")].map(g => {
        const txt = t => (g.getElementsByTagName(t)[0]?.textContent ?? "");
        return {
            gameId: Number(txt("gameid")),
            playerCount: Number(txt("playercount")),
            canJoin: txt("canjoin") === "true",
            players: [...g.getElementsByTagName("playerName")].map(n => n.textContent ?? "")
        };
    });
}

export async function createGame() {
    const xml = await fetchXml(`${BASE}?messageType=CREATE&visibility=public`);
    return Number(xml.getElementsByTagName("gameid")[0]?.textContent ?? "0");
}

export async function join(gameId, handle) {
    const xml = await fetchXml(`${BASE}?messageType=JOIN&gameId=${gameId}&handle=${enc(handle)}`);
    // <joininfo><user>XYZ</user><gameid>...</gameid></joininfo> oder <fail/>
    const info = xml.getElementsByTagName("joininfo")[0];
    if (!info) return null;
    return info.getElementsByTagName("user")[0]?.textContent ?? null;
}

export async function leave(gameId, userToken) {
    await fetchXml(`${BASE}?messageType=LEAVE&gameId=${gameId}&user=${enc(userToken)}`);
}

export async function sendChat(gameIdOrLobby, handle, text) {
    const gameQuery = gameIdOrLobby ? `&gameid=${gameIdOrLobby}` : "";
    const payload = `${handle}: ${text}`;
    await fetchXml(`${BASE}?messageType=CHAT&chat=${enc(payload)}${gameQuery}`);
}

export async function getChat(gameIdOrLobby, guestCounter, handle) {
    const gameQuery = gameIdOrLobby ? `&gameid=${gameIdOrLobby}` : "";
    const xml = await fetchXml(`${BASE}?messageType=GETCHAT&guestCounter=${guestCounter}&handle=${enc(handle)}${gameQuery}`);
    const chats = [...xml.getElementsByTagName("chatmessage")].map(n => ({
        chat: n.getElementsByTagName("chat")[0]?.textContent ?? "",
        timestamp: n.getElementsByTagName("timestamp")[0]?.textContent ?? ""
    }));
    const sessions = [...xml.getElementsByTagName("session")].map(n => n.textContent ?? "");
    return {chats, sessions};
}

export async function getGameState(gameId) {
    const xml = await fetchXml(`${BASE}?messageType=GETGAMESTATE&gameId=${gameId}`);
    const nodes = xml.getElementsByTagName("gamestate");
    if (nodes.length === 0) return null;
    const gs = nodes[0];
    // leeres <gamestate/> → nichts darstellen
    if (!gs || gs.children.length === 0) return null;
    const txt = (p, t) => p.getElementsByTagName(t)[0]?.textContent ?? "";
    const players = [...gs.getElementsByTagName("player")].map(p => {
        const inplay = [...p.getElementsByTagName("inplaycard")].map(c => ({
            name: txt(c, "name"), suit: txt(c, "suit"), value: txt(c, "value"), type: txt(c, "type")
        }));
        const gunNode = p.getElementsByTagName("gun")[0];
        const gun = gunNode ? {
            name: txt(gunNode, "name"),
            suit: txt(gunNode, "suit"),
            value: txt(gunNode, "value"),
            type: txt(gunNode, "type")
        } : null;
        return {
            handle: txt(p, "handle"),
            name: txt(p, "name"),
            specialAbility: txt(p, "specialability"),
            health: Number(txt(p, "health")),
            maxHealth: Number(txt(p, "maxhealth")),
            handSize: Number(txt(p, "handsize")),
            isSheriff: p.getElementsByTagName("issheriff").length > 0,
            gun, inplay
        };
    });
    const roles = [...gs.getElementsByTagName("role")].map(n => n.textContent ?? "");
    const deckSize = Number(txt(gs, "decksize"));
    const currentName = txt(gs, "currentname");
    const discardTop = gs.getElementsByTagName("discardtopcard")[0];
    const topCard = discardTop ? {
        name: txt(discardTop, "name"),
        suit: txt(discardTop, "suit"),
        value: txt(discardTop, "value"),
        type: txt(discardTop, "type")
    } : null;
    const gameOver = xml.getElementsByTagName("gameover").length > 0;
    return {players, roles, deckSize, currentName, topCard, gameOver};
}

export async function getGuestCounter() {
    const xml = await fetchXml(`${BASE}?messageType=GETGUESTCOUNTER`);
    return Number(xml.getElementsByTagName("guestcounter")[0]?.textContent ?? "0");
}

export async function getMessage(gameId, userToken) {
    const xml = await fetchXml(`${BASE}?messageType=GETMESSAGE&gameId=${gameId}&user=${enc(userToken)}`);
    const msg = xml.getElementsByTagName("message")[0];
    if (!msg) return null;
    const id = Number(msg.getElementsByTagName("id")[0]?.textContent ?? "0");
    const text = msg.getElementsByTagName("text")[0]?.textContent ?? "";
    const hand = [...msg.getElementsByTagName("hand")[0]?.getElementsByTagName("card") ?? []]
        .map(c => c.textContent ?? "")
    return {id, text, hand};
}

export async function sendResponse(gameId, userToken, messageId, responseText) {
    const xml = await fetchXml(`${BASE}?messageType=SENDRESPONSE&gameId=${gameId}&user=${enc(userToken)}&messageId=${messageId}&response=${enc(responseText)}`);
}