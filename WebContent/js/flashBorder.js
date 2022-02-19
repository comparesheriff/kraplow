import {stateMachine} from "./statemachine.js";

export {flashBorder}

function flashBorder(elmId, stopFlash) {
    const elm = document.getElementById(elmId);
    if (stopFlash === "true") {
        elm.style.opacity = "1";
        clearInterval(stateMachine.flash);
    } else {
        let borderPattern = false;
        stateMachine.flash = setInterval(setBorder, 300);

        function setBorder() {
            if (borderPattern) {
                borderPattern = false;
                if (elm != null) {
                    elm.style.opacity = "1";
                }
            } else {
                borderPattern = true;
                if (elm != null) {
                    elm.style.opacity = "0.8";
                }
            }
        }
    }
}