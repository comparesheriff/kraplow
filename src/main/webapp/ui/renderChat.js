export function renderChat(root, chats, sessions) {
    root.innerHTML = "";
    const log = document.createElement("div");
    for (const c of chats) {
        const div = document.createElement("div");
        div.innerHTML = `<span class="muted">${c.timestamp}</span> ${escapeHtml(c.chat)}`;
        log.append(div);
    }
    const aside = document.createElement("div");
    aside.className = "muted";
    aside.textContent = "Online: " + sessions.join(", ");
    root.append(log, aside);
}

function escapeHtml(s) {
    return s.replace(/[&<>"']/g, m => ({'&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&apos;'}[m]));
}
