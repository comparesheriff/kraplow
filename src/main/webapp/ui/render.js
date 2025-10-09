export function renderLobby(root, games) {
    root.innerHTML = "";
    const h = document.createElement("div");
    h.innerHTML = `<h2>Available Games</h2>`;
    const table = document.createElement("table");
    table.innerHTML = `
    <thead><tr><th>ID</th><th>Players</th><th>Can Join</th><th>Handles</th></tr></thead>
    <tbody></tbody>`;
    const tbody = table.querySelector("tbody");
    for (const g of games) {
        const tr = document.createElement("tr");
        const td = s => {
            const el = document.createElement("td");
            el.textContent = s;
            return el;
        };
        tr.appendChild(td(String(g.gameId)));
        tr.appendChild(td(String(g.playerCount)));
        tr.appendChild(td(g.canJoin ? "yes" : "no"));
        tr.appendChild(td(g.players.join(", ")));
        tbody.appendChild(tr);
    }
    root.append(h, table);
}
