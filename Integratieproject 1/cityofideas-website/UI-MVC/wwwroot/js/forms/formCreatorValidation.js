import * as constants from "../constants";

let Questions;
let domErrorQuestions;


export function initValidation(answers){
    domErrorQuestions = document.getElementById('error-questions');
    Questions = answers;
}

export function validate(){
    let isValid = true;
    if (Questions.length < 1) {
        domErrorQuestions.innerText = constants.ERR_ONE_QUESTION_REQUIRED;
        domErrorQuestions.classList.add('active');
        isValid = false;
    }
    
    Questions.forEach((question) => {
        if (!question.domTextBox.validity.valid){
            question.domTextBoxErr.classList.add('active');
            question.domTextBoxErr.innerText = constants.ERR_OPENTEXT;
            isValid = false;
        }
    });
    
    
    return isValid;
}

export function questionAdded(){
    domErrorQuestions.innerText = '';
    domErrorQuestions.classList.remove('active');
}