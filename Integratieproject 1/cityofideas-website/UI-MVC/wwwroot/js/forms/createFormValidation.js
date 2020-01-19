import * as constants from "../constants";

let domTitle;
let domErrorTitle;

export function initValidation(){
    domTitle = document.getElementById('FormTitle');
    domErrorTitle = document.getElementById('error-FormTitle');

    domTitle.addEventListener('input',() => {
        domErrorTitle.innerText = '';
        domErrorTitle.classList.remove('active');
    });
}

export function validate(){
    let isValid = true;
    
    if (!domTitle.validity.valid){
        domErrorTitle.innerText = constants.ERR_OPENTEXT;
        domErrorTitle.classList.add('active');
        isValid = false;
    } 
    
    return isValid;
}