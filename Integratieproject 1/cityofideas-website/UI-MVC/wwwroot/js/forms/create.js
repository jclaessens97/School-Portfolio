import CreateForm from './formCreator';
import * as constants from "../constants";

function addEventHandlers() {
    const domFormWrapper = document.getElementById('form-wrapper');
    domFormWrapper.innerText = constants.NO_FORMTYPE_CHOSEN;
    
    
    const domFormType = document.getElementById('FormType');
    domFormType.addEventListener('change',() => {
        const formType = parseInt(domFormType.options[domFormType.selectedIndex].value,10);
        domFormWrapper.innerText = '';
        CreateForm(formType);
    });
    
    
    
}

function init() {
    // Method in formCreator.js
    //CreateForm(0);
    addEventHandlers();
}

window.onload = init;
