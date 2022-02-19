export {commandName}
function commandName(command) {
    if (command === "askDiscard") {
        return "Discard down to number of remaining life";
    } else if (command === "askBlueDiscard") {
        return "Discard a blue card to draw 2 others";
    } else if (command === "askOthersCard") {
        return "Choose a card to remove from player";
    } else if (command === "askPlay") {
        return "Play any number of cards";
    } else if (command === "askPlayer") {
        return "Which player would you like to target?";
    } else if (command === "chooseCardToPutBack") {
        return "Which card would you like to put back?";
    } else if (command === "chooseDiscard") {
        return "Draw from the deck or discard pile";
    } else if (command === "chooseDrawCard") {
        return "Choose which card for the Draw";
    } else if (command === "chooseFromPlayer") {
        return "Draw from the deck or a player?";
    } else if (command === "chooseGeneralStoreCard") {
        return "Choose a general store card to take";
    } else if (command === "chooseTwoDiscardForLife") {
        return "Discard cards for Life(2 for 1, 4 for 2)";
    } else if (command === "chooseTwoDiscardForShoot") {
        return "Discard cards for Shoot(2 for 1)";
    } else if (command === "respondBang") {
        return "Play a Shoot or lose a life";
    } else if (command === "respondBeer") {
        return "Play a beer or die";
    } else if (command === "respondMiss") {
        return "Play a missed or lose a life";
    } else if (command === "respondTwoMiss") {
        return "Play 2 misses or lose a life";
    }
}