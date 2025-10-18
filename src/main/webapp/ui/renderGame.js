export function renderGame(container, handRoot, gs, myHandle, handCardNames) {
    container.innerHTML = "";
    if (!gs) {
        container.textContent = "No game state...";
        return;
    }
    // Meta: Deck / Discard / Roles / Current
    const turnEl = document.querySelector("#turn");
    if (turnEl) turnEl.textContent = gs.currentName ? `(turn: ${gs.currentName})` : "";
    const meta = document.querySelector("#meta");
    if (meta) {
        meta.innerHTML = "";
        const row = document.createElement("div");
        row.className = "kpi";
        const deck = box(`Deck: ${gs.deckSize}`);
        const discard = box(`Top: ${gs.topCard ? gs.topCard.name : "-"}`);
        const roles = document.createElement("div");
        roles.className = "kpi";
        roles.append(...gs.roles.map(r => badge(r)));
        row.append(deck, discard);
        meta.append(row, roles);
    }

    const table = document.createElement("table");
    table.innerHTML = `
      <thead><tr><th>Handle</th><th>Name</th><th>Health</th><th>Gun</th><th>In Play</th></tr></thead>
      <tbody></tbody>
    `;
    const body = table.querySelector("tbody");
    for (const p of gs.players) {
        const tr = document.createElement("tr");
        if (gs.currentName && p.name === gs.currentName) tr.classList.add("current");
        tr.appendChild(td(p.handle === myHandle ? "You" : p.handle));
        tr.appendChild(td(p.name + (p.isSheriff ? " ⭐ (Sheriff)" : "")));
        tr.appendChild(td(`${p.health}/${p.maxHealth} (hand ${p.handSize})`));
        tr.appendChild(td(p.gun ? p.gun.name : "-"));
        tr.appendChild(td(p.inplay.map(c => c.name).join(", ")));
        body.appendChild(tr);
    }
    container.appendChild(table);

    // meine Hand – echte Namen falls vorhanden (aus GETMESSAGE)
    handRoot.innerHTML = "";
    const me = gs.players.find(p => p.handle === myHandle);
    if (me) {
        const names = (handCardNames && handCardNames.length)
            ? handCardNames :
            Array.from({length: me.handSize}, (_, i) => `Card #${i + 1}`);
        for (let i = 0; i < names.length; i++) {
            const card = document.createElement("div");
            card.className = "card";
            const hasRealNames = !!(handCardNames && handCardNames.length);
            card.innerHTML = `<div class="name">${names[i]}</div>${hasRealNames ? "" : `<div class="muted">[unknown name]</div>`}`;
            handRoot.appendChild(card);
        }
    }
}

function td(s) {
    const el = document.createElement("td");
    el.textContent = String(s);
    return el;
}

function badge(s) {
    const b = document.createElement("span");
    b.className = "badge";
    b.textContent = s;
    return b;
}

function box(s) {
    const b = document.createElement("div");
    b.className = "box";
    b.textContent = s;
    return b;
}