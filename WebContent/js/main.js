import {getCookie} from "./cookie.js";
import {join} from "./join.js";

const randomAdjectives = ["Selective", "Tasty", "Furtive", "Pink", "Straight", "Shaggy", "Disgusting", "Cut", "Humorous", "Rightful", "Elated", "Rough", "Plucky", "Sophisticated", "Rude", "Fancy", "Noxious", "Drab", "Spicy", "Elastic", "Well-groomed", "Upset", "Fresh", "Voiceless", "Silent", "Sulky", "Cynical", "Stimulating", "Astonishing", "Blushing", "Well-made", "Picayune", "Flippant", "Lovely", "Political", "Ambitious", "Obese", "Freezing", "Maddening", "Hideous", "Lively", "Solid", "Taboo", "Fallacious", "Hysterical", "Magenta", "Gullible", "Abortive", "Permissible", "Successful", "Crazy", "Joyous", "Legal", "Flawless", "Boorish", "Eatable", "Literate", "Fantastic", "Gusty", "Faded", "Possible", "Rare", "Labored", "Equal", "Feeble", "Sparkling", "Thoughtless", "Green", "Even", "Puzzled", "Rigid", "Garrulous", "Paltry", "Difficult", "Nostalgic", "Uptight", "Habitual", "Woozy", "Quiet", "Thirsty", "Fearful", "Gleaming", "Happy", "Vagabond", "Ill", "Many", "Deeply", "Luxuriant", "Present", "Tall", "Swanky", "Clear", "Tired", "Fluffy", "Blue-eyed", "Average", "Obscene", "Parched", "Ten", "Uninterested", "Important", "Wooden", "Late", "Scattered", "Materialistic", "Alluring", "Square", "Sweltering", "Capable", "Gruesome", "Maniacal", "Periodic", "Dashing", "Whimsical", "Overwrought", "Future", "Aquatic", "Protective"];
const randomNames = ["Sachiko", "Walter", "Brittney", "Donnell", "Eufemia", "Clementina", "Erika", "Lauran", "Twanda", "Arianna", "Rema", "Monroe", "Herma", "Abigail", "Hattie", "Rick", "Adrianna", "Lesha", "Melida", "Izola", "Marquitta", "Savanna", "Ethelyn", "Leone", "Irwin", "Kristal", "Gonzalo", "Maryanne", "Abdul", "Nakia", "Rina", "Adrienne", "Bula", "Shane", "Avis", "Allegra", "Arlen", "Vaughn", "Pricilla", "Iesha", "Elidia", "Jeramy", "Caren", "Theodore", "Flavia", "Telma", "Barbra", "Will", "Lachelle", "Kate"];
const handleField = document.getElementById('handle');
const handleLocation = window.location.href.indexOf("handle=");

let params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});

if (handleLocation !== -1) {
    handleField.value = params.handle;
} else {
    const cookieHandle = getCookie("handle");
    if (cookieHandle !== "") {
        handleField.value = cookieHandle;
    } else {
        handleField.value = randomAdjectives[Math.floor(Math.random() * randomAdjectives.length)] + randomNames[Math.floor(Math.random() * randomNames.length)];
    }
}

const joinLocation = window.location.href.indexOf("join=");
if (joinLocation !== -1) {
    let gameIdJoin = params.join;
    join(gameIdJoin, handleField.value);
}
