export {getImageForCard, getDescriptionForCard}
function getImageForCard(cardName) {
    if (cardName.indexOf("Shoot") !== -1) {
        return "shoot.png";
    } else if (cardName.indexOf("Missed!") !== -1) {
        return "missed.png";
    } else if (cardName.indexOf("Beer") !== -1) {
        return "beer.png";
    } else if (cardName.indexOf("Barrel") !== -1) {
        return "barrel.png";
    } else if (cardName.indexOf("Scope") !== -1) {
        return "scope.png";
    } else if (cardName.indexOf("Mustang") !== -1) {
        return "mustang.png";
    } else if (cardName.indexOf("Schofield") !== -1) {
        return "schofield.png";
    } else if (cardName.indexOf("Colt45") !== -1) {
        return "colt45.png";
    } else if (cardName.indexOf("Remington") !== -1) {
        return "remington.png";
    } else if (cardName.indexOf("Winchester") !== -1) {
        return "winchester.png";
    } else if (cardName.indexOf("Volcanic") !== -1) {
        return "volcanic.png";
    } else if (cardName.indexOf("Rev. Carbine") !== -1) {
        return "carbine.png";
    } else if (cardName.indexOf("Jail") !== -1) {
        return "jail.png";
    } else if (cardName.indexOf("Dynamite") !== -1) {
        return "dynamite.png";
    } else if (cardName.indexOf("Gatling") !== -1) {
        return "gatling.png";
    } else if (cardName.indexOf("Saloon") !== -1) {
        return "saloon.png";
    } else if (cardName.indexOf("Panic!") !== -1) {
        return "panic.png";
    } else if (cardName.indexOf("General Store") !== -1) {
        return "generalstore.png";
    } else if (cardName.indexOf("Indians!") !== -1) {
        return "indians.png";
    } else if (cardName.indexOf("Duel") !== -1) {
        return "duel.png";
    } else if (cardName.indexOf("Stagecoach") !== -1) {
        return "stagecoach.png";
    } else if (cardName.indexOf("Wells Fargo") !== -1) {
        return "wellsfargo.png";
    } else if (cardName.indexOf("Cat Balou") !== -1) {
        return "catbalou.png";
    } else if (cardName.indexOf("Dodge") !== -1) {
        return "dodge.png";
    } else if (cardName.indexOf("Brawl") !== -1) {
        return "brawl.png";
    } else if (cardName.indexOf("Punch") !== -1) {
        return "punch.png";
    } else if (cardName.indexOf("Tequila") !== -1) {
        return "tequila.png";
    } else if (cardName.indexOf("Springfield") !== -1) {
        return "springfield.png";
    } else if (cardName.indexOf("Rag Time") !== -1) {
        return "ragtime.png";
    } else if (cardName.indexOf("Whisky") !== -1) {
        return "whisky.png";
    } else if (cardName.indexOf("Silver") !== -1) {
        return "silver.png";
    } else if (cardName.indexOf("Hideout") !== -1) {
        return "hideout.png";
    } else if (cardName.indexOf("Sombrero") !== -1) {
        return "sombrero.png";
    } else if (cardName.indexOf("Ten Gallon Hat") !== -1) {
        return "tengallonhat.png";
    } else if (cardName.indexOf("Iron Plate") !== -1) {
        return "ironplate.png";
    } else if (cardName.indexOf("Bible") !== -1) {
        return "bible.png";
    } else if (cardName.indexOf("Buffalo Rifle") !== -1) {
        return "buffalorifle.png";
    } else if (cardName.indexOf("Derringer") !== -1) {
        return "derringer.png";
    } else if (cardName.indexOf("Knife") !== -1) {
        return "knife.png";
    } else if (cardName.indexOf("Pepperbox") !== -1) {
        return "pepperbox.png";
    } else if (cardName.indexOf("Howitzer") !== -1) {
        return "howitzer.png";
    } else if (cardName.indexOf("Conestoga") !== -1) {
        return "conestoga.png";
    } else if (cardName.indexOf("Can Can") !== -1) {
        return "cancan.png";
    } else if (cardName.indexOf("Canteen") !== -1) {
        return "canteen.png";
    } else if (cardName.indexOf("Pony Express") !== -1) {
        return "ponyexpress.png";
    } else {
        return "cardface.png";
    }
}

function getDescriptionForCard(cardName) {
    if (cardName.indexOf("Shoot") !== -1) {
        return "Damage a player within range. Can play only 1 per turn.";
    } else if (cardName.indexOf("Missed!") !== -1) {
        return "Counter to Shoot.";
    } else if (cardName.indexOf("Beer") !== -1) {
        return "Regain a health. Does not work if only 2 players left.";
    } else if (cardName.indexOf("Barrel") !== -1) {
        return "Draw on after a Shoot. If heart counts as Missed.";
    } else if (cardName.indexOf("Scope") !== -1) {
        return "Other players look 1 space closer.";
    } else if (cardName.indexOf("Mustang") !== -1) {
        return "You look 1 space further from other players.";
    } else if (cardName.indexOf("Schofield") !== -1) {
        return "Gun range of 2.";
    } else if (cardName.indexOf("Colt45") !== -1) {
        return "Gun range of 1. Default gun.";
    } else if (cardName.indexOf("Remington") !== -1) {
        return "Gun range of 3.";
    } else if (cardName.indexOf("Winchester") !== -1) {
        return "Gun range of 4.";
    } else if (cardName.indexOf("Volcanic") !== -1) {
        return "Gun range of 1. Play any number of Shoots.";
    } else if (cardName.indexOf("Rev. Carbine") !== -1) {
        return "Gun range of 5.";
    } else if (cardName.indexOf("Jail") !== -1) {
        return "Draw before Drawing cards. Miss a turn if not Heart.";
    } else if (cardName.indexOf("Dynamite") !== -1) {
        return "Draw before Drawing cards. If 2-9 of spades. Take 3 damage, else pass to next player.";
    } else if (cardName.indexOf("Gatling") !== -1) {
        return "Shoot everyone else does not count towards Shoots per turn.";
    } else if (cardName.indexOf("Saloon") !== -1) {
        return "Everyone gets a beer.";
    } else if (cardName.indexOf("Panic!") !== -1) {
        return "Take a card from a player 1 space away.";
    } else if (cardName.indexOf("General Store") !== -1) {
        return "Everyone gets a card.";
    } else if (cardName.indexOf("Indians!") !== -1) {
        return "Everyone else plays a Shoot or takes a damage.";
    } else if (cardName.indexOf("Duel") !== -1) {
        return "You and any other player discard Shoots, until someone runs out and takes a damage.";
    } else if (cardName.indexOf("Stagecoach") !== -1) {
        return "Draw 2 cards.";
    } else if (cardName.indexOf("Wells Fargo") !== -1) {
        return "Draw 3 cards.";
    } else if (cardName.indexOf("Cat Balou") !== -1) {
        return "Other player discards a card from in play of your choosing or random from the hand.";
    } else if (cardName.indexOf("Rag Time") !== -1) {
        return "Discard and take a card from any player";
    } else if (cardName.indexOf("Dodge") !== -1) {
        return "Miss and draw a card";
    } else if (cardName.indexOf("Whisky") !== -1) {
        return "Discard for 2 health";
    } else if (cardName.indexOf("Hideout") !== -1) {
        return "You look 1 space further from other players.";
    } else if (cardName.indexOf("Silver") !== -1) {
        return "Other players look 1 space closer.";
    } else if (cardName.indexOf("Punch") !== -1) {
        return "Shoot a distance of one";
    } else if (cardName.indexOf("Brawl") !== -1) {
        return "All others discard";
    } else if (cardName.indexOf("Tequila") !== -1) {
        return "Discard and give anyone a beer";
    } else if (cardName.indexOf("Springfield") !== -1) {
        return "Discard and shoot anyone";
    } else if (cardName.indexOf("Pony Express") !== -1) {
        return "Draw 3 cards.";
    } else if (cardName.indexOf("Ten Gallon Hat") !== -1) {
        return "Counter to Shoot.";
    } else if (cardName.indexOf("Sombrero") !== -1) {
        return "Counter to Shoot.";
    } else if (cardName.indexOf("Bible") !== -1) {
        return "Counter to Shoot and draw a card.";
    } else if (cardName.indexOf("Iron Plate") !== -1) {
        return "Counter to Shoot.";
    } else if (cardName.indexOf("Derringer") !== -1) {
        return "Shoot a distance of one and draw a card.";
    } else if (cardName.indexOf("Pepperbox") !== -1) {
        return "Damage a player within range.";
    } else if (cardName.indexOf("Knife") !== -1) {
        return "Shoot a distance of one.";
    } else if (cardName.indexOf("Canteen") !== -1) {
        return "Regain a health.";
    } else if (cardName.indexOf("Howitzer") !== -1) {
        return "Shoot everyone else does not count towards Shoots per turn.";
    } else if (cardName.indexOf("Can Can") !== -1) {
        return "Other player discards a card from in play of your choosing or random from the hand.";
    } else if (cardName.indexOf("Buffalo Rifle") !== -1) {
        return "Discard and shoot anyone";
    } else if (cardName.indexOf("Conestoga") !== -1) {
        return "Take a card from any player.";
    } else {
        return "No description";
    }
}