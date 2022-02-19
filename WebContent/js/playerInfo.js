export {playerInfo, getImageForPlayer, getImageForRole, getDescriptionForPlayer}

const playerInfo = [];
playerInfo["Bart Cassidy"] = {
    image: "bartcassidy.png",
    description: "Lose health, draw a card.",
    expansion: "base"
};
playerInfo["Black Jack"] = {
    image: "blackjack.png",
    description: "Show second draw, if red suit, draw another.",
    expansion: "base"
};
playerInfo["Calamity Janet"] = {
    image: "calamityjanet.png",
    description: "Shoots can be played as Misses, and Misses as Shoots.",
    expansion: "base"
};
playerInfo["El Gringo"] = {
    image: "elgringo.png",
    description: "Draws card from player damaged him.",
    expansion: "base"
};
playerInfo["Jourdonnais"] = {
    image: "jourdonnais.png",
    description: "Has a barrel at all times.",
    expansion: "base"
};
playerInfo["Jesse Jones"] = {
    image: "jessejones.png",
    description: "Can draw first card from other player.",
    expansion: "base"
};
playerInfo["Kit Carlson"] = {
    image: "kitcarlson.png",
    description: "Draw 3, keep 2, put 1 back.",
    expansion: "base"
};
playerInfo["Sean Mallory"] = {
    image: "seanmallory.png",
    description: "Has handsize limit of 10.",
    expansion: "sidestep"
};
playerInfo["Vera Custer"] = {
    image: "veracuster.png",
    description: "Copies abilities of others.",
    expansion: "sidestep"
};
playerInfo["Tequila Joe"] = {image: "tequilajoe.png", description: "Beer gives him 2 life.", expansion: "sidestep"};
playerInfo["Pat Brennan"] = {
    image: "patbrennan.png",
    description: "Can draw a card infront of another player.",
    expansion: "sidestep"
};
playerInfo["Bill Noface"] = {
    image: "billnoface.png",
    description: "Draws 1 card and an extra for each missing health.",
    expansion: "sidestep"
};
playerInfo["Herb Hunter"] = {
    image: "herbhunter.png",
    description: "When other dies, draw 2 cards.",
    expansion: "sidestep"
};
playerInfo["Apache Kid"] = {
    image: "apachekid.png",
    description: "Others diamond cards do not work.",
    expansion: "sidestep"
};
playerInfo["Molly Stark"] = {
    image: "mollystark.png",
    description: "When playing cards out of turn, draw 1.",
    expansion: "sidestep"
};
playerInfo["Pixie Pete"] = {image: "pixiepete.png", description: "Draws 3 cards.", expansion: "sidestep"};
playerInfo["Doc Holyday"] = {
    image: "docholyday.png",
    description: "On his turn, discard 2 cards to Shoot.",
    expansion: "sidestep"
};
playerInfo["Greg Digger"] = {
    image: "gregdigger.png",
    description: "When other player killed, get 2 life.",
    expansion: "sidestep"
};
playerInfo["Jose Delgado"] = {
    image: "josedelgado.png",
    description: "On his turn discard blue card to draw 2.",
    expansion: "sidestep"
};
playerInfo["Belle Star"] = {
    image: "bellestar.png",
    description: "On her turn others cards do not work.",
    expansion: "sidestep"
};
playerInfo["Elena Fuente"] = {
    image: "elenafuente.png",
    description: "Any card can be a miss.",
    expansion: "sidestep"
};
playerInfo["Chuck Wengam"] = {
    image: "chuckwengam.png",
    description: "Change one life for 2 cards.",
    expansion: "sidestep"
};
playerInfo["Willy the Kid"] = {
    image: "willythekid.png",
    description: "No limit on Shoots per turn.",
    expansion: "base"
};
playerInfo["Vulture Sam"] = {
    image: "vulturesam.png",
    description: "Takes all cards from dead players.",
    expansion: "base"
};
playerInfo["Suzy Lafayette"] = {
    image: "suzylafayette.png",
    description: "Draw a card when hand is empty.",
    expansion: "base"
};
playerInfo["Slab the Killer"] = {
    image: "slabthekiller.png",
    description: "Others need 2 misses to counter his Shoot.",
    expansion: "base"
};
playerInfo["Sid Ketchum"] = {
    image: "sidketchum.png",
    description: "Can discard 2 cards for 1 life.",
    expansion: "base"
};
playerInfo["Rose Doolan"] = {image: "rosedoolan.png", description: "Has an Scope at all times.", expansion: "base"};
playerInfo["Pedro Ramirez"] = {image: "pedroramierz.png", description: "Can draw from discard.", expansion: "base"};
playerInfo["Paul Regret"] = {
    image: "paulregret.png",
    description: "Has a Mustang at all times.",
    expansion: "base"
};
playerInfo["Lucky Duke"] = {
    image: "luckyduke.png",
    description: "Flip 2 cards on a draw and choose 1.",
    expansion: "base"
};
playerInfo["Uncle Will"] = {
    image: "unclewill.png",
    description: "Any card can be a general store, once in a turn.",
    expansion: "base"
};
playerInfo["Johnny Kisch"] = {
    image: "johnnykisch.png",
    description: "When playing blue/green card, discard all cards in play with same name.",
    expansion: "base"
};
playerInfo["Claus The Saint"] = {
    image: "clausthesaint.png",
    description: "Draw one card for every player plus one, everyone else gets 1.",
    expansion: "base"
};

function getImageForPlayer(playerName) {
    if (playerInfo[playerName] != null) {
        return playerInfo[playerName].image;
    } else {
        return "cancel.png";
    }
}

function getDescriptionForPlayer(playerName) {
    if (playerInfo[playerName] != null) {
        return playerInfo[playerName].description;
    } else {
        return "";
    }
}

function getImageForRole(role, goal) {
    if (role === "Sheriff") {
        return '<img src="sheriff.png" alt="Role sheriff" title="' + role + " - " + goal + '" height="40"  style="position:absolute; left:70px; top:100px;"> ';
    } else if (role === "Outlaw") {
        return '<img src="outlaw.png" alt="Role outlaw" title="' + role + " - " + goal + '" height="40"  style="position:absolute; left:70px; top:100px;"> ';
    } else if (role === "Deputy") {
        return '<img src="deputy.png" alt="Role deputy" title="' + role + " - " + goal + '" height="40"  style="position:absolute; left:70px; top:100px;"> ';
    } else if (role === "Renegade") {
        return '<img src="renegade.png" alt="Role renegade" title="' + role + " - " + goal + '" height="40"  style="position:absolute; left:70px; top:100px;"> ';
    }
}