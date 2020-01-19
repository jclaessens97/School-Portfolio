const ERR_OPENTEXT = 'Dit veld is verplicht';
const ERR_SINGLE = 'Gelieve iets aan te duiden';
const ERR_MULTIPLE = 'Gelieve minstens één vakje aaan te duiden';
const ERR_EMAIL_MISSING = 'Gelieve een emailadres op te geven';
const ERR_EMAIL_INVALID = 'Gelieve een geldig emailadres op te geven: example@example.be';

const OpenTextInputs = [];
const SingleChoiceInputs = [];
const DropDownInputs = [];
const MultipleChoiceInputs = [];
let email;
let emailError;

export function EnableValidation() {
    // Add required tag to all inputs except multiple choice question
    const domQuestions = Array.from(document.getElementsByClassName('question'));
    const domFieldTypeArr = document.getElementsByClassName('field-type');
    const domInputRequiredArr = document.getElementsByClassName('required');

    domQuestions.forEach((element, index) => {
        const domError = element.querySelector('.error');

        if (domInputRequiredArr[index].value.toLowerCase() === 'true') {
            const fieldType = parseInt(domFieldTypeArr[index].value, 10);
            const domInputArr = element.getElementsByClassName('question-input');
            const domInputField = domInputArr[0];

            const questionLabel = element.getElementsByClassName('question-label')[0];

            // Add required indicator to label
            questionLabel.innerHTML += "<span style='color:red !important'>*</span>";


            switch (fieldType) {
            case 0:
                OpenTextInputs.push({ input: domInputField, error: domError });
                domInputField.required = true;
                domInputField.addEventListener('input',
                    () => {
                        domError.className = 'error';
                        domError.innerHTML = '';
                    });
                
                break;
            case 4:
                MultipleChoiceInputs.push({ inputArr: domInputArr, error: domError });
                Array.from(domInputArr).forEach((element) => {
                    element.addEventListener('input',
                        () => {
                            domError.className = 'error';
                            domError.innerHTML = '';
                        });
                });
                break;
            case 3:
                SingleChoiceInputs.push({ input: domInputField, error: domError });
                Array.from(domInputArr).forEach((element) => {
                    element.addEventListener('input',
                        () => {
                            domError.className = 'error';
                            domError.innerHTML = '';
                        });
                });
                domInputField.required = true;
                break;
            case 6:
                DropDownInputs.push({ input: domInputField, error: domError });
                Array.from(domInputArr).forEach((element) => {
                    element.addEventListener('input',
                        () => {
                            domError.className = 'error';
                            domError.innerHTML = '';
                        });
                });
                domInputField.required = true;
                break;
            default:
                break;
            }
        }
    });


    email = document.getElementById('email');
    
    emailError = document.getElementById('email-error');

    if (email != null) {
        email.addEventListener('input',
            () => {
                if (email.validity.valid) {
                    emailError.className = 'error';
                    emailError.innerHTML = '';
                }
            });
    }
}

export function OnSubmit() {
    let isValid = true;
    try {
        OpenTextInputs.forEach((inputObject, index, array) => {
            if (!inputObject.input.validity.valid) {
                // If the field is not valid, we display a custom
                // error message.
                inputObject.error.innerHTML = ERR_OPENTEXT;
                inputObject.error.className = 'error active';
                // And we prevent the form from being sent by canceling the event
                isValid = false;
            }
        });

        SingleChoiceInputs.forEach((inputObject, index, array) => {
            if (!inputObject.input.validity.valid) {
                // If the field is not valid, we display a custom
                // error message.
                inputObject.error.innerHTML = ERR_SINGLE;
                inputObject.error.className = 'error active';
                // And we prevent the form from being sent by canceling the event
                isValid = false;
            }
        });

        DropDownInputs.forEach((inputObject, index, array) => {
            if (inputObject.input.options[inputObject.input.selectedIndex].value == -1) {
                // If the field is not valid, we display a custom
                // error message.
                inputObject.error.innerHTML = ERR_SINGLE;
                inputObject.error.className = 'error active';
                // And we prevent the form from being sent by canceling the event
                isValid = false;
            }
        });

        MultipleChoiceInputs.forEach((inputObject, index, array) => {
            let amountSelected = 0;
            Array.from(inputObject.inputArr).forEach((input) => {
                if (input.checked == true) {
                    amountSelected++;
                }
            });

            if (amountSelected == 0) {
                inputObject.error.innerHTML = ERR_MULTIPLE;
                inputObject.error.className = 'error active';
                isValid = false;
            }
        });
        if (email != null) {
            
            if (email.validity.valueMissing) {
                emailError.innerHTML = ERR_EMAIL_MISSING;
                emailError.className = 'error active';
                isValid = false;
            } else if (!email.validity.valid) {
                emailError.innerHTML = ERR_EMAIL_INVALID;
                emailError.className = 'error active';
                isValid = false;
            }
        }
    } catch (e) {
        isValid = false;
        console.log('Something went wrong');
        console.log(e);
    }


    return isValid;
}
