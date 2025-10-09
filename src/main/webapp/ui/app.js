import {
    createGame,
    getChat,
    getGameState,
    getGuestCounter,
    getMessage,
    join,
    leave,
    listAvailableGames,
    sendChat,
    sendResponse
} from "./api.js";
import {renderLobby} from "./render.js";
import {renderGame} from "./renderGame.js";
import {renderChat} from "./renderChat.js";

const $ = sel => document.querySelector(sel);
const state = {
    games: [],
    loading: false,
    gameId: null,
    handle: "",
    user: null,
    guestCounter: 0,
    chat: [],
    sessions: [],
    gs: null,
    lastMsgId: null,
    handCardNames: []
};

async function refresh() {
    state.loading = true;
    try {
        state.games = await listAvailableGames();
    } catch { /* ignoriere wie legacy */
    } finally {
        state.loading = false;
        render();
    }
}

function render() {
    renderLobby($("#lobby"), state.games);
}

$("#btnCreate").addEventListener("click", async () => {
    const id = await createGame();
    $("#gameId").value = String(id);
    state.gameId = id;
    await refresh();
});
$("#btnJoin").addEventListener("click", async () => {
    const id = ($("#gameId").value || "").trim();
    const handle = ($("#handle").value || "").trim();
    if (!id || !handle) return;
    const user = await join(Number(id), handle);
    if (!user) {
        alert("Join failed");
        return;
    }
    state.gameId = Number(id);
    state.handle = handle;
    state.user = user;
    localStorage.setItem("handle", handle);
    localStorage.setItem("user", user);
    $("#btnLeave").disabled = false;
    $("#btnChat").disabled = false;
});
$("#btnLeave").addEventListener("click", async () => {
    if (!state.gameId || !state.handle) return;
    await leave(state.gameId, state.user || state.handle); // user-Token bevorzugen
    state.gameId = null;
    state.gs = null;
    state.user = null;
    state.handCardNames = []
    $("#btnLeave").disabled = true;
    renderGame($("#game"), $("#hand"), null, state.handle, state.handCardNames);
});
$("#btnChat").addEventListener("click", async () => {
    const chatInput = $("#chatInput");
    const txt = (chatInput.value || "").trim();
    if (!txt) return;
    await sendChat(state.gameId, state.handle, txt);
    chatInput.value = "";
});
$("#chatInput").addEventListener("keydown", async (e) => {
    if (e.key === "Enter") {
        e.preventDefault();
        $("#btnChat").click();
    }
});

// Bootstrap: guestCounter + gespeicherten Handle laden
(async function bootstrap() {
    try {
        const savedGuestCounter = localStorage.getItem("guestCounter");
        state.guestCounter = savedGuestCounter ? Number(savedGuestCounter) : await getGuestCounter();
        localStorage.setItem("guestCounter", String(state.guestCounter));
    } catch {
    }
    const savedHandle = localStorage.getItem("handle");
    const savedUser = localStorage.getItem("user");
    if (savedHandle) {
        $("#handle").value = savedHandle;
        state.handle = savedHandle;
    }
    if (savedUser) {
        state.user = savedUser;
    }
    await refresh();
})();

setInterval(async () => {
    await refresh();
    try {
        const {chats, sessions} = await getChat(state.gameId, state.guestCounter, state.handle);
        state.chat = chats;
        state.sessions = sessions;
        const chatlog = $("#chatlog");
        renderChat(chatlog, state.chat, state.sessions);
        const el = chatlog;
        el && (el.scrollTop = el.scrollHeight);
    } catch {
    }
    try {
        if (state.gameId) {
            state.gs = await getGameState(state.gameId);
            renderGame($("#game"), $("#hand"), state.gs, state.handle, state.handCardNames);
        }
    } catch {
    }
    // Message-Poll: liefert auch die eigene Hand (Kartennamen)
    try {
        if (state.gameId && (state.user || state.handle)) {
            const msg = await getMessage(state.gameId, state.user || state.handle);
            if (msg) {
                state.handCardNames = msg.hand || [];
                if (state.lastMsgId !== msg.id) {
                    state.lastMsgId = msg.id;
                    openModal(msg.text, msg.id);
                }
            }
        }
    } catch {
    }
}, 1500);

function openModal(text, msgId) {
    const m = $("#modal"), body = $("#modalBody"), input = $("#responseInput");
    if (!m || !body || !input) return;
    body.textContent = text ?? "";
    m.classList.remove("hidden");
    m.setAttribute("aria-hidden", "false");
    input.value = "";
    input.focus();
    $("#btnSendResponse").onclick = async () => {
        const resp = (input.value || "").trim();
        await sendResponse(state.gameId, state.user || state.handle, msgId, resp);
        closeModal();
    };
    $("#btnCloseModal").onclick = closeModal;
}

function closeModal() {
    const m = $("#modal");
    if (!m) return;
    m.classList.add("hidden");
    m.setAttribute("aria-hidden", "true");
}