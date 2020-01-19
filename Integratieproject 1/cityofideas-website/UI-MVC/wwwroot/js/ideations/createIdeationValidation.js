import * as constants from '../constants'

let domCentralQuestion;
let domDescription;
let domUrl;
let domIdeationType;

let domErrorCentralQuestion;
let domErrorDescription;
let domErrorUrl;
let domErrorIdeationType;
export function initValidation(){
    domCentralQuestion = document.getElementById('CentralQuestion');
    domDescription = document.getElementById('Description');
    domUrl = document.getElementById('Url');
    domIdeationType = document.getElementById('IdeationType');
    
    domErrorCentralQuestion = document.getElementById('error-CenterQuestion');
    domErrorDescription = document.getElementById('error-Description');
    domErrorUrl = document.getElementById('error-Url');
    domErrorIdeationType = document.getElementById('error-IdeationType');
    
    domCentralQuestion.addEventListener('input',() => {
        domErrorCentralQuestion.innerText = '';
        domErrorCentralQuestion.classList.remove('active');
    });

    domDescription.addEventListener('input',() => {
        domErrorDescription.innerText = '';
        domErrorDescription.classList.remove('active');
    });

    domUrl.addEventListener('input',() => {
        domErrorUrl.innerText = '';
        domErrorUrl.classList.remove('active');
    });
    
    domIdeationType.addEventListener('input',() => {
        domErrorIdeationType.innerText = '';
        domErrorIdeationType.classList.remove('active');
    });
}

export function validate(){
    let isValid = true;
    
    if (!domCentralQuestion.validity.valid){
        domErrorCentralQuestion.innerText = constants.ERR_OPENTEXT;
        domErrorCentralQuestion.classList.add('active');
        isValid = false;
    }
    console.log(domDescription.value);
    if (!domDescription.validity.valid){
        domErrorDescription.innerText = constants.ERR_OPENTEXT;
        domErrorDescription.classList.add('active');
        isValid = false;
    }

    // TODO: Add regex
    if (!domUrl.validity.valid){
        domErrorUrl.innerText = constants.ERR_OPENTEXT;
        domErrorUrl.classList.add('active');
        isValid = false;
    }

    let value = domIdeationType.options[domIdeationType.selectedIndex].value;
    if (value < 1){
        domErrorIdeationType.innerText = constants.ERR_SINGLE;
        domErrorIdeationType.classList.add('active');
        isValid = false;
    }
    return isValid;
}