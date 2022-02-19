import {getServletUrl, sendApiRequest} from "./sendApiRequest.js";
import {stateMachine} from "./statemachine.js";
import {setup} from "./setup.js";
import {getDescriptionForCard, getImageForCard} from "./cardInfo.js";
import {getDescriptionForPlayer, getImageForPlayer, getImageForRole} from "./playerInfo.js";

export {getGameState, parseGetGameState}

function getGameState(gameId) {
    sendApiRequest(getServletUrl() + "?messageType=GETGAMESTATE&gameId=" + gameId, parseGetGameState);
}

function parseGetGameState(responseJson) {
    let gunName;
    let description;
    let cardName;
    let discardName;
    let top;
    let discardCard;
    let size;
    let gameMessagesDiv;
    console.log("parsedGetGameState", responseJson)
    const gameStateDiv = document.getElementById('gameState');
    if (gameStateDiv != null) {
        let result = "<img src='table.png' style='position:absolute; top:0; left:0; z-index:-1;' width='630' height='630'>";
        result += "<img src='logo2.png' style='position:absolute; top:343px; left:222px;' width='200'>";
        const deckSize = responseJson["decksize"];
        if (deckSize === undefined) {
            return;
        } else {
            stateMachine.started = true;
            gameMessagesDiv = document.getElementById('gameMessages');
            gameMessagesDiv.style.display = "block";
            setup();
        }

        const name = responseJson["currentname"];

        if (responseJson["gameover"]) {
            const timeoutOverNode = responseJson.timeout;
            let gameOverMessage = "";
            if (timeoutOverNode != null) {
                gameOverMessage += "<h2>Timeout - " + timeoutOverNode.childNodes[0].nodeValue + "</h2>"
            }
            gameMessagesDiv = document.getElementById('gameMessages');
            const handleField = document.getElementById('handle');

            gameOverMessage += '<h2>Game Over</2> <a href="/westerncardgame?handle=' + handleField.value + '">Game Lobby</a>';
            gameMessagesDiv.innerHTML = gameOverMessage;
            stateMachine.gameOver = true;
        }

        const roleNodes = responseJson["roles"]
        let allRoles = "";
        if (roleNodes != null) {
            for (let roleIndex = 0; roleIndex < roleNodes.length; roleIndex++) {
                const roleName = roleNodes[roleIndex].childNodes[0].nodeValue;
                if (roleName === "Deputy") {
                    allRoles += "<img style='max-height:50px; max-width:50px' src='deputy.png' alt='Deputy' title='Deputy'>";
                } else if (roleName === "Outlaw") {
                    allRoles += "<img style='max-height:50px; max-width:50px' src='outlaw.png' alt='Outlaw' title='Outlaw'>";
                } else if (roleName === "Sheriff") {
                    allRoles += "<img style='max-height:50px; max-width:50px' src='sheriff.png' alt='Sheriff' title='Sheriff'>";
                } else if (roleName === "Renegade") {
                    allRoles += "<img style='max-height:50px; max-width:50px' src='renegade.png' alt='Renegade' title='Renegade'>";
                }
            }
        }
        if (document.getElementById("remainingroles").innerHTML !== allRoles) {
            document.getElementById("remainingroles").innerHTML = allRoles;
        }

        const playerNodes = responseJson["players"] == null ? [] : responseJson["players"];


        let foundSelf = false;
        if (playerNodes != null) {
            playerNodes.forEach(loopPlayerName => {
                if (loopPlayerName === stateMachine.playerName) foundSelf = true;
            })
        }
        if (foundSelf === false && stateMachine.playerName != null) {
            //result += "You dead";
        }
        result += '<table>'
        let row = 0;
        let column = 0;
        for (let playerIndex = 0; playerIndex < playerNodes.length; playerIndex++) {
            let healthIndex;
            if (column === 0) {
                result += '<tr>';
            }
            if (playerNodes.length === 1) {
                if (row === 0) {
                    if (column === 0 || column === 2) {
                        result += '<td><div style="width:200px; height:200px;"></div></td>';
                        column = column + 1;
                        if (column === 3) {
                            result += '</tr>';
                            row = row + 1;
                            column = 0;
                        }
                    }
                }
            }
            if (playerNodes.length === 2) {
                if (row === 0) {
                    result += '<td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;"></div></td></tr><tr>';
                    row = row + 1;
                }
                if (row === 1) {
                    if (column === 1) {
                        result += '<td><div style="width:200px; height:200px;">'
                        size = deckSize.childNodes[0].nodeValue;
                        if (size > 0) {
                            result += '<img id="draw_pile" src="card.png" alt="Draw Pile ' + size + ' cards"  title="' + size + ' cards" width="90"> ';
                        }

                        discardCard = responseJson.getElementsByTagName("discardtopcard")[0];
                        if (discardCard != null) {
                            discardName = discardCard.getElementsByTagName("name")[0];
                            cardName = discardName.childNodes[0].nodeValue;
                            description = " - " + getDescriptionForCard(cardName);
                            result += '<img id="discard_pile" src="' + getImageForCard(cardName) + '" alt="Discard Pile ' + cardName + '" title="' + cardName + description + '" width="90"> ';
                        }
                        result += '</div></td>';
                        column = column + 1;
                    }
                }
            }
            if (playerNodes.length === 3) {
                if (row === 0) {
                    if (column === 0 || column === 2) {
                        result += '<td><div style="width:200px; height:200px;"></div></td>';
                        column = column + 1;
                        if (column === 3) {
                            result += '</tr>';
                            row = row + 1;
                            column = 0;
                        }
                    }
                }
                if (row === 1) {
                    result += '<td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;">';
                    size = deckSize.childNodes[0].nodeValue;
                    if (size > 0) {
                        result += '<img id="draw_pile" src="card.png" alt="Draw Pile ' + size + ' cards"  title="' + size + ' cards" width="90"> ';
                    }

                    discardCard = responseJson["discardtopcard"];
                    if (discardCard != null) {
                        cardName = discardCard["name"];
                        description = " - " + getDescriptionForCard(cardName);
                        result += '<img id="discard_pile" src="' + getImageForCard(cardName) + '" alt="Discard Pile ' + cardName + '" title="' + cardName + description + '" width="90"> ';
                    }
                    result += '</div></td><td><div style="width:200px; height:200px;"></div></td></tr><tr>';
                    row = row + 1;
                    column = 0;
                }
                if (row === 2) {
                    if (column === 1) {
                        result += '<td><div style="width:200px; height:200px;"></div></td>';
                        column = column + 1;
                    }
                }
            }
            if (playerNodes.length === 4) {
                if (row === 0) {
                    if (column === 1) {
                        result += '<td><div style="width:200px; height:200px;"></div></td>';
                        column = column + 1;
                    }
                }
                if (row === 1) {
                    result += '<td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;">';
                    size = deckSize.childNodes[0].nodeValue;
                    if (size > 0) {
                        result += '<img id="draw_pile" src="card.png" alt="Draw Pile ' + size + ' cards"  title="' + size + ' cards" width="90"> ';
                    }

                    discardCard = responseJson["discardtopcard"];
                    if (discardCard != null) {
                        cardName = discardCard["name"];
                        description = " - " + getDescriptionForCard(cardName);
                        result += '<img id="discard_pile" src="' + getImageForCard(cardName) + '" alt="Discard Pile ' + cardName + '" title="' + cardName + description + '"  width="90"> ';
                    }
                    result += '</div></td><td><div style="width:200px; height:200px;"></div></td></tr><tr>';
                    row = row + 1;
                }
                if (row === 2) {
                    if (column === 1) {
                        result += '<td><div style="width:200px; height:200px;"></div></td>';
                        column = column + 1;
                    }
                }
            }
            if (playerNodes.length === 5) {
                if (row === 1) {
                    result += '<td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;">';
                    size = deckSize.childNodes[0].nodeValue;
                    if (size > 0) {
                        result += '<img id="draw_pile" src="card.png" alt="Draw Pile ' + size + ' cards"  title="' + size + ' cards" width="90"> ';
                    }

                    discardCard = responseJson["discardtopcard"];
                    if (discardCard != null) {
                        cardName = discardCard["name"];
                        description = " - " + getDescriptionForCard(cardName);
                        result += '<img id="discard_pile" src="' + getImageForCard(cardName) + '" alt="Discard Pile ' + cardName + '" title="' + cardName + description + '" width="90"> ';
                    }
                    result += '</div></td><td><div style="width:200px; height:200px;"></div></td></tr><tr>';
                    row = row + 1;
                }
                if (row === 2) {
                    if (column === 1) {
                        result += '<td><div style="width:200px; height:200px;"></div></td>';
                        column = column + 1;
                    }
                }
            }
            if (playerNodes.length === 6) {
                if (column === 1 && (row === 0 || row === 2)) {
                    result += '<td><div style="width:200px; height:200px;"></div></td>';
                    column = column + 1;
                } else if (column === 1 && row === 1) {
                    result += '<td><div style="width:200px; height:200px;">';
                    size = deckSize.childNodes[0].nodeValue;
                    if (size > 0) {
                        result += '<img id="draw_pile" src="card.png" alt="Draw Pile ' + size + ' cards"  title="' + size + ' cards" width="90"> ';
                    }

                    discardCard = responseJson["discardtopcard"];
                    if (discardCard != null) {
                        cardName = discardCard["name"];
                        description = " - " + getDescriptionForCard(cardName);
                        result += '<img id="discard_pile" src="' + getImageForCard(cardName) + '" alt="Discard Pile ' + cardName + '" title="' + cardName + description + '" width="90"> ';
                    }
                    result += '</div></td>';
                    column = column + 1;
                }
            }
            if (playerNodes.length === 7) {
                if (row === 1) {
                    if (column === 1) {
                        result += '<td><div style="width:200px; height:200px;">';
                        size = deckSize.childNodes[0].nodeValue;
                        if (size > 0) {
                            result += '<img id="draw_pile" src="card.png" alt="Draw Pile ' + size + ' cards"  title="' + size + ' cards" width="90"> ';
                        }

                        discardCard = responseJson["discardtopcard"];
                        if (discardCard != null) {
                            cardName = discardCard["name"];
                            description = " - " + getDescriptionForCard(cardName);
                            result += '<img id="discard_pile" src="' + getImageForCard(cardName) + '" alt="Discard Pile ' + cardName + '" title="' + cardName + description + '" width="90"> ';
                        }
                        result += '</div></td>';
                        column = column + 1;
                    }
                } else if (row === 2) {
                    if (column === 1) {
                        result += '<td><div style="width:200px; height:200px;"></div></td>';
                        column = column + 1;
                    }
                }
            }
            if (playerNodes.length === 8) {
                if (row === 1) {
                    if (column === 1) {
                        result += '<td><div style="width:200px; height:200px;">';
                        size = deckSize.childNodes[0].nodeValue;
                        if (size > 0) {
                            result += '<img id="draw_pile" src="card.png" alt="Draw Pile ' + size + ' cards"  title="' + size + ' cards" width="90"> ';
                        }

                        discardCard = responseJson["discardtopcard"];
                        if (discardCard != null) {
                            cardName = discardCard["name"];
                            description = " - " + getDescriptionForCard(cardName);
                            result += '<img id="discard_pile" src="' + getImageForCard(cardName) + '" alt="Discard Pile ' + cardName + '" title="' + cardName + description + '" width="90"> ';
                        }
                        result += '</div></td>';
                        column = column + 1;
                    }
                }
            }

            result += '<td>';
            if (playerNodes.length === 3) {
                if (playerIndex === 0) {
                    stateMachine.loopPlayer = playerNodes[0];
                } else if (playerIndex === 1) {
                    stateMachine.loopPlayer = playerNodes[2];
                } else if (playerIndex === 2) {
                    stateMachine.loopPlayer = playerNodes[1];
                }
            } else if (playerNodes.length === 4) {
                if (playerIndex === 0) {
                    stateMachine.loopPlayer = playerNodes[0];
                } else if (playerIndex === 1) {
                    stateMachine.loopPlayer = playerNodes[1];
                } else if (playerIndex === 2) {
                    stateMachine.loopPlayer = playerNodes[3];
                } else if (playerIndex === 3) {
                    stateMachine.loopPlayer = playerNodes[2];
                }
            } else if (playerNodes.length === 5) {
                if (playerIndex === 0) {
                    stateMachine.loopPlayer = playerNodes[0];
                } else if (playerIndex === 1) {
                    stateMachine.loopPlayer = playerNodes[1];
                } else if (playerIndex === 2) {
                    stateMachine.loopPlayer = playerNodes[2];
                } else if (playerIndex === 3) {
                    stateMachine.loopPlayer = playerNodes[4];
                } else if (playerIndex === 4) {
                    stateMachine.loopPlayer = playerNodes[3];
                }
            } else if (playerNodes.length === 6) {
                if (playerIndex === 0) {
                    stateMachine.loopPlayer = playerNodes[0];
                } else if (playerIndex === 1) {
                    stateMachine.loopPlayer = playerNodes[1];
                } else if (playerIndex === 2) {
                    stateMachine.loopPlayer = playerNodes[5];
                } else if (playerIndex === 3) {
                    stateMachine.loopPlayer = playerNodes[2];
                } else if (playerIndex === 4) {
                    stateMachine.loopPlayer = playerNodes[4];
                } else if (playerIndex === 5) {
                    stateMachine.loopPlayer = playerNodes[3];
                }
            } else if (playerNodes.length === 7) {
                if (playerIndex === 0) {
                    stateMachine.loopPlayer = playerNodes[0];
                } else if (playerIndex === 1) {
                    stateMachine.loopPlayer = playerNodes[1];
                } else if (playerIndex === 2) {
                    stateMachine.loopPlayer = playerNodes[2];
                } else if (playerIndex === 3) {
                    stateMachine.loopPlayer = playerNodes[6];
                } else if (playerIndex === 4) {
                    stateMachine.loopPlayer = playerNodes[3];
                } else if (playerIndex === 5) {
                    stateMachine.loopPlayer = playerNodes[5];
                } else if (playerIndex === 6) {
                    stateMachine.loopPlayer = playerNodes[4];
                }
            } else if (playerNodes.length === 8) {
                if (playerIndex === 0) {
                    stateMachine.loopPlayer = playerNodes[0];
                } else if (playerIndex === 1) {
                    stateMachine.loopPlayer = playerNodes[1];
                } else if (playerIndex === 2) {
                    stateMachine.loopPlayer = playerNodes[2];
                } else if (playerIndex === 3) {
                    stateMachine.loopPlayer = playerNodes[7];
                } else if (playerIndex === 4) {
                    stateMachine.loopPlayer = playerNodes[3];
                } else if (playerIndex === 5) {
                    stateMachine.loopPlayer = playerNodes[6];
                } else if (playerIndex === 6) {
                    stateMachine.loopPlayer = playerNodes[5];
                } else if (playerIndex === 7) {
                    stateMachine.loopPlayer = playerNodes[4];
                }
            } else {
                stateMachine.loopPlayer = playerNodes[playerIndex];
            }


            const loopPlayerName = stateMachine.loopPlayer.name;
            const loopPlayerHandle = stateMachine.loopPlayer.handle
            let border_color = "transparent";
            let altPlayerText = loopPlayerName + " ";
            if (loopPlayerName === stateMachine.playerName && name === loopPlayerName) {
                border_color = "#F0F";
                altPlayerText = "Your turn " + altPlayerText;
            } else if (name === loopPlayerName) {
                border_color = "#F00";
                altPlayerText = "Their turn " + altPlayerText;
            } else if (loopPlayerName === stateMachine.playerName) {
                border_color = "#00F";
                altPlayerText = "You " + altPlayerText;
            }
            result += '<div style="width:200px; height:200px; position:relative; border:4px solid ' + border_color + ';">';

            description = " - " + getDescriptionForPlayer(loopPlayerName);
            result += '<img src="' + getImageForPlayer(loopPlayerName) + '" alt="' + altPlayerText + '" id="' + loopPlayerName + '" title="' + loopPlayerName + description + '" style="border:4px solid transparent; position:absolute; top:0; left:0;" height="150px">';
            result += '<div style="position:relative; top:152px; width:125px; background-color:white; border: 1px solid black;">' + loopPlayerHandle + '</div>';
            const handSize = stateMachine.loopPlayer.getElementsByTagName("handsize")[0].childNodes[0].nodeValue;
            result += '<div style="position:relative; top:152px; width:125px; background-color:white; border: 1px solid black;"><img src="card.png" alt="hand" title="' + handSize + ' cards in hand" width="10" style="layout:inline-block;"> ' + handSize + ' cards in hand</div>';

            const gunNode = stateMachine.loopPlayer.gun
            if (gunNode != null) {
                gunName = gunNode.name;
                description = " - " + getDescriptionForCard(gunName);
                result += '<img src="' + getImageForCard(gunName) + '" alt="Gun ' + gunName + '" title="' + gunName + description + '" width="75" style="position:absolute; left:125px; top:0; z-index:7;"> ';
            } else {
                gunName = "Colt45";
                description = " - " + getDescriptionForCard(gunName);
                result += '<img src="' + getImageForCard(gunName) + '" alt="Gun ' + gunName + '" title="' + gunName + description + '" width="75" style="position:absolute; left:125px; top:0; z-index:7"> ';
            }

            const inPlayNodes = stateMachine.loopPlayer.inplaycard
            for (let inPlayIndex = 0; inPlayIndex < inPlayNodes.length; inPlayIndex++) {
                const inPlayName = inPlayNodes[inPlayIndex].getElementsByTagName("name")[0].childNodes[0].nodeValue;
                description = " - " + getDescriptionForCard(inPlayName);
                top = 17 * (inPlayIndex + 1);
                const zindex = 7 - (inPlayIndex + 1);
                result += '<img src="' + getImageForCard(inPlayName) + '" alt="In Play ' + inPlayName + '" title="' + inPlayName + description + '"  width="75" style="position:absolute; left:125px; top:' + top + 'px; z-index:' + zindex + ';"> ';
            }

            if (stateMachine.loopPlayer.getElementsByTagName("issheriff")[0] != null) {
                result += '<img src="sheriff.png" alt="Sheriff" title="Sheriff - Kill all Outlaws and the Renegade." height="40" style="position:absolute; left:70px; top:100px;"> ';
            } else {
                if (stateMachine.playerName === loopPlayerName) {
                    result += getImageForRole(stateMachine.role, stateMachine.goal);
                }
            }
            const playerHealth = stateMachine.loopPlayer.getElementsByTagName("health")[0].childNodes[0].nodeValue;
            const playerMaxHealth = stateMachine.loopPlayer.getElementsByTagName("maxhealth")[0].childNodes[0].nodeValue;
            for (healthIndex = 0; healthIndex < playerHealth; healthIndex++) {
                top = (healthIndex * 10) + 40;
                result += '<img src="borderbullet.png" alt="Health" title="' + playerHealth + '/' + playerMaxHealth + ' Health" height="10" style="position:absolute; left:100px; top:' + top + 'px;"> ';
            }
            for (let maxHealthIndex = healthIndex; maxHealthIndex < playerMaxHealth; maxHealthIndex++) {
                top = (maxHealthIndex * 10) + 40;
                result += '<img src="emptyborderbullet.png" alt="Damage" title="' + playerHealth + '/' + playerMaxHealth + ' Health" height="10" style="position:absolute; left:100px; top:' + top + 'px;"> ';
            }
            result += '</div>';
            result += '</td>'
            column = column + 1;
            if (column === 3) {
                column = 0;
                row = row + 1;
                result += '</tr>'
            }
            if (playerNodes.length === 2 && row === 2) {
                result += '<tr><td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;"></div></td></tr>';
            }
            if (playerNodes.length === 1 && row === 0 && column === 2) {
                result += '<td><div style="width:200px; height:200px;"></div></td></tr><tr><td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;">';
                size = deckSize;
                if (size > 0) {
                    result += '<img id="draw_pile" src="card.png" alt="Draw Pile ' + size + ' cards"  title="' + size + ' cards" width="90"> ';
                }

                discardCard = responseJson["discardtopcard"];
                if (discardCard != null) {
                    discardName = discardCard.getElementsByTagName("name")[0];
                    cardName = discardName.childNodes[0].nodeValue;
                    description = " - " + getDescriptionForCard(cardName);
                    result += '<img id="discard_pile" src="' + getImageForCard(cardName) + '" alt="Discard Pile ' + cardName + '" title="' + cardName + description + '" width="90"> ';
                }
                result += '</div></td><td><div style="width:200px; height:200px;"></div></td></tr><tr><td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;"></div></td><td><div style="width:200px; height:200px;"></div></td></tr>';
            }


        }
        result += '</table>';
        if (stateMachine.gameStateResult !== result) {
            stateMachine.gameStateResult = result;
            gameStateDiv.innerHTML = result;
        }
    }
}