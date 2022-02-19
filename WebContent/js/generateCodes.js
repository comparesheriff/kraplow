export {generateTargetCodeSingle, generateTargetCode}

function generateTargetCode(targets) {
    let mouseOver = 'onmouseover="';
    let mouseOut = 'onmouseout="';
    for (let i = 0; i < targets.length; i++) {
        const target = targets[i];
        if (target !== "" && target !== "Cancel") {
            mouseOver += 'document.getElementById(\'' + target + '\').style.borderColor = \'red\';' + 'document.getElementById(\'' + target + '\').style.borderStyle = \'dashed\';';
            mouseOut += 'document.getElementById(\'' + target + '\').style.borderColor = \'transparent\';';
        }
    }
    mouseOver += '"';
    mouseOut += '"';
    return mouseOver + ' ' + mouseOut;
}

function generateTargetCodeSingle(target) {
    let mouseOver = 'onmouseover="';
    let mouseOut = 'onmouseout="';
    if (target !== "" && target !== "Cancel") {
        mouseOver += 'document.getElementById(\'' + target + '\').style.borderColor = \'red\';' + 'document.getElementById(\'' + target + '\').style.borderStyle = \'dashed\';';
        mouseOut += 'document.getElementById(\'' + target + '\').style.borderColor = \'transparent\';';
    }
    mouseOver += '"';
    mouseOut += '"';
    return mouseOver + ' ' + mouseOut;
}