import Sortable from 'sortablejs';
import axios from 'axios';
import { Map } from '../map';
import {
    ICONS, createButton, generateId, TypesString, TypesEnum,CreateModal
} from '../util';
import * as constants from '../constants';
import * as ideationValidation from '../ideations/createIdeationValidation' // validation for ideations
import * as formValidation from './createFormValidation' // validation for forms
import * as validation from './formCreatorValidation' // validation for botho

const Questions = [];

let FormType = 0;

let domModal;
let isInitialized = false;

// Function used when moving/deleting elements -> set all indexes correct again
function resetIndex() {
    for (let i = 0; i < Questions.length; i++) {
        Questions[i].index = i;
    }
}

// remove element from DOM and question list
function removeElement(domElement, question) {
    const index = Questions.indexOf(question);
    Questions.splice(index, 1);
    domElement.remove();
    resetIndex();
}

function moveElement(question, moveHtml = true) {
    const {domElement} = question;

    // first: remove question from array (if exists) to add it again
    const index = Questions.indexOf(question);
    if (index >= 0) {
        Questions.splice(index, 1);
    }

    // Then: add questions to array on right place
    Questions.splice(question.index, 0, question);

    // Make sure all indexes of Questions are correct
    resetIndex();

    if (moveHtml) {
        // Get form wrapper
        const formWrapper = document.getElementById('form-wrapper');

        // if element already exists in DOM -> remove first
        if (domElement.parentNode === formWrapper) {
            formWrapper.removeChild(domElement);
            // formWrapper.removeChild(question.domButtons);
        }

        // Add element to DOM
        if (question.index + 1 >= Questions.length) { // if last element, just add it
            formWrapper.appendChild(domElement);
        } else { // if it is in the middle -> insert it at right place
            formWrapper.insertBefore(domElement, Questions[question.index + 1].domElement);
        }
    }
}

function AddOpenQuestion(question) {
    // Add option for making long answer
    const setting = document.createElement('div');
    setting.className = 'checkbox-option form-check';

    const id = generateId();
    const lblLongAnswer = document.createElement('label');
    lblLongAnswer.setAttribute('for', id);
    lblLongAnswer.innerHTML = `${constants.LONG_ANSWER}?`;
    const inputLonganswer = document.createElement('input');
    inputLonganswer.className = 'form-check-input';
    inputLonganswer.setAttribute('type', 'checkbox');
    inputLonganswer.id = id;
    inputLonganswer.addEventListener('change', () => {
        question.longAnswer = inputLonganswer.checked;
    });

    setting.appendChild(inputLonganswer);
    setting.appendChild(lblLongAnswer);

    question.domSettings.appendChild(setting);
}

function resetOptionsIndex(options) {
    for (let i = 0; i < options.length; i++) {
        options[i].index = i;
    }
}

// Similar functionality as moveElement
function moveOption(option, newOption = false) {
    const options = option.optionsRef;
    // first: remove option from array (if exists) to add it again
    const index = options.indexOf(option);
    if (index >= 0) {
        options.splice(index, 1);
    }

    // Then: add questions to array on right place
    options.splice(option.index, 0, option);

    if (newOption) option.questionRef.domOptions.appendChild(option.domElement);

    // Make sure all indexes of Questions are correct
    resetOptionsIndex(options);
}

function removeOption(option) {
    const options = option.optionsRef;
    const optionDom = option.domElement;
    const index = options.indexOf(option);
    options.splice(index, 1);
    optionDom.remove();
    resetOptionsIndex(options);
}

function CreateOption(question, iconClass, index = 0) {
    const wrapper = question.domElement;
    // Create new option DOM
    const optionDom = document.createElement('div');
    const icon = document.createElement('i');
    icon.className = iconClass;
    const textbox = document.createElement('input');
    textbox.className = 'ml-2';
    textbox.setAttribute('type', 'text');
    optionDom.appendChild(icon);
    optionDom.appendChild(textbox);

    // Add new option to array in Question object
    const option = {
        index,
        string: '',
        optionsRef: question.options,
        questionRef: question,
        domElement: optionDom,
    };

    textbox.addEventListener('input', () => {
        option.string = textbox.value;
    });

    moveOption(option, true);

    // Button to delete options
    const deleteBtn = document.createElement('a');
    deleteBtn.className = 'icon-btn ml-1';
    const deleteOptionIcon = document.createElement('i');
    deleteOptionIcon.className = 'fas fa-minus';
    deleteBtn.appendChild(deleteOptionIcon);
    optionDom.appendChild(deleteBtn);

    // Add delete functionality, if there are no options add a new one.
    deleteBtn.addEventListener('click',
        () => {
            removeOption(option);
            if (wrapper.childNodes.length === 0) {
                CreateOption(question, iconClass);
            }
        });

    const btnMove = document.createElement('a');
    btnMove.className = 'optionHandle ml-1';
    const icoMove = document.createElement('i');
    icoMove.className = 'fas fa-arrows-alt';
    btnMove.appendChild(icoMove);
    optionDom.appendChild(btnMove);
}

// Used for single -and multiplechoice questions
function AddChoiceQuestion(question, wrapper) {
    // Wrapper for options
    const domOptions = document.createElement('div');
    domOptions.className = 'mt-2';

    question.domOptions = domOptions;

    new Sortable(domOptions, {
        animation: 150,
        handle: '.optionHandle',
        ghostClass: 'question-ghost',
        onEnd(evt) {
            const option = question.options[evt.oldIndex];
            option.index = evt.newIndex;
            moveOption(option);
        },
    });

    // different icons for different questiontypes
    let iconClass;
    if (question.type === TypesEnum.SINGLECHOICE) {
        iconClass = 'far fa-circle';
    } else {
        iconClass = 'far fa-square';
    }

    // Make initial option
    CreateOption(question, iconClass);

    // append options wrapper to question
    wrapper.appendChild(domOptions);

    // Button for adding a new option
    const addOption = document.createElement('a');
    addOption.className = 'icon-btn';
    const addOptionIcon = document.createElement('i');
    addOptionIcon.className = 'fas fa-plus';
    addOption.appendChild(addOptionIcon);
    wrapper.appendChild(addOption);

    // Add new option on click
    addOption.addEventListener('click',
        () => {
            CreateOption(question, iconClass, question.options.length);
        });
}

function AddLocationQuestion(question,wrapper) {
    
    const Location = {
        latitude: undefined,
        longitude: undefined,
        zoomLevel: undefined,
        //allowZoom : true,
    };
    
    question.location = Location;
    
    const domInstructions = document.createElement('h5');
    domInstructions.className = "mt-2";
    domInstructions.innerText = constants.START_LOCATION;
    wrapper.appendChild(domInstructions);

    const domInstructions2 = document.createElement('p');
    domInstructions2.innerText = "Sleep met het kaartje naar de gewenste startlocatie en zoomniveau.";
    wrapper.appendChild(domInstructions2);
    
    const domMapWrapper = document.createElement('div');
    domMapWrapper.className = 'd-flex justify-content-between';
    wrapper.appendChild(domMapWrapper);
    
    //Div for the map
    const domMap = document.createElement('div');
    domMap.className = 'map';
    domMapWrapper.appendChild(domMap);
    
    //Search box for map
    const domSearchBox = document.createElement('input');
    domSearchBox.id = 'pac-input';
    domSearchBox.className = 'controls';
    domSearchBox.setAttribute('type', 'text');
    domSearchBox.setAttribute('placeholder', constants.MAP_SERACH);
    
    const domDetailsWrapper = document.createElement('div');
    domDetailsWrapper.className = "w-50";
    domMapWrapper.appendChild(domDetailsWrapper);
    
    const domInfo = document.createElement('div');
    //domDetailsWrapper.appendChild(domInfo);
    domInfo.className = 'ml-2';
    
    const domLatitude = document.createElement('p');
    const domLatitudeNbr = document.createElement('span');
    domLatitude.innerHTML = `${constants.LATITUDE}: `;
    domLatitude.appendChild(domLatitudeNbr);
    domInfo.appendChild(domLatitude);

    const domLongitude = document.createElement('p');
    const domLongitudeNbr = document.createElement('span');
    domLongitude.innerHTML = `${constants.LONGITUDE}: `;
    domLongitude.appendChild(domLongitudeNbr);
    domInfo.appendChild(domLongitude);

    const domZoomLevel = document.createElement('p');
    const domZoomLevelNbr = document.createElement('span');
    domZoomLevel.innerHTML = `${constants.ZOOM_LEVEL}: `;
    domZoomLevel.appendChild(domZoomLevelNbr);
    domInfo.appendChild(domZoomLevel);
    
    /*
    let id = generateId();
    const domAllowZoomLabel = document.createElement('label');
    domAllowZoomLabel.setAttribute('for',id);
    const domAllowZoomCheckbox = document.createElement('input');
    domAllowZoomCheckbox.setAttribute('type','checkbox');
    domAllowZoomCheckbox.id = id;
    const domAllowZoomText = document.createElement('span');
    domAllowZoomText.innerText = `${constants.ALLOW_ZOOM}?` ;
    domAllowZoomLabel.appendChild(domAllowZoomCheckbox);
    domAllowZoomLabel.appendChild(domAllowZoomText);
    domDetailsWrapper.appendChild(domAllowZoomLabel);
    
    domAllowZoomCheckbox.addEventListener('input', () => {
        LocationOptions.allowZoom = domAllowZoomCheckbox.checked;
    }); */
    
    
    function showMap(position) {
        let searchOrigin;
        Map.loadGoogleMapsApi().then((googleMaps) => { 
            const map = new googleMaps.Map(domMap, {
                center: { lat: position.coords.latitude, lng: position.coords.longitude },
                zoom: 14,
                streetViewControl: false,
            });
            updateLocation();

            function updateLocation() {
                const latitude = map.center.lat();
                const longitude = map.center.lng();
                const zoomLevel = map.zoom;
                
                domLatitudeNbr.innerText = latitude;
                domLongitudeNbr.innerText = longitude;
                domZoomLevelNbr.innerText = zoomLevel;
                
                Location.latitude = latitude;
                Location.longitude = longitude;
                Location.zoomLevel = zoomLevel;
            }

            map.addListener('center_changed', () => {
                updateLocation();
            });
            map.addListener('zoom_changed',() => {
               updateLocation(); 
            });

            const searchBox = new googleMaps.places.SearchBox(domSearchBox);
            map.controls[google.maps.ControlPosition.TOP_LEFT].push(domSearchBox);

            domSearchBox.addEventListener('keydown', (event) => {
                if (event.key === 'Enter') {
                    const location = domSearchBox.value;
                    const geocoder = new googleMaps.Geocoder();
                    geocoder.geocode({ address: location }, (data) => {
                        const lat = data[0].geometry.location.lat();
                        const lng = data[0].geometry.location.lng();
                        searchOrigin = new googleMaps.LatLng(lat, lng);
                        map.setCenter(searchOrigin);
                    });
                }
            })
        });
    }

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((position) => {
            showMap(position);
        });
    } else {
        x.innerHTML = 'Geolocation is not supported by this browser.';
    }
}

function AddStatementQuestion(question,wrapper) {
    const domExplanation = document.createElement('p');
    domExplanation.innerText = constants.STATEMENT_EXPLANATION;
    wrapper.appendChild(domExplanation);
}

function addElement(type, index) {
    validation.questionAdded();
    // Create HTML wrapper for question
    const domQuestion = document.createElement('div');
    domQuestion.className = 'form-creator-box question mt-1 mb-1 col-12 pr-0 pl-0';

    // header
    const domHeader = document.createElement('div');
    domHeader.className = 'header btn-color';

    // body
    const domBody = document.createElement('div');
    domBody.className = 'body';

    domQuestion.appendChild(domHeader);
    domQuestion.appendChild(domBody);

    // All the needed settings to configure
    const domSettings = document.createElement('div');
    domSettings.className = 'settings';

    // Create question object
    const question = {
        type,
        question: null,
        required: false,
        longAnswer: false,
        options: [],
        location: null,
        index,
        domTextBox: undefined,
        domTextBoxErr: undefined,
        domElement: domQuestion,
        domSettings,
        domOptions: undefined, // will be defined in addChoiceQuestion (if needed)
        domButtons: undefined, // will be defined in 'addFormButtons'
    };

    // Add title to html
    const title = document.createElement('h5');
    title.innerHTML = TypesString[type];
    domHeader.appendChild(title);

    // Move element to right place in DOM and add to question list 'Questions'
    moveElement(question);

    // Add textbox for question text
    const domTextbox = document.createElement('input');
    domTextbox.setAttribute('type', 'text');
    domTextbox.required = true;
    domTextbox.className += ' form-control question form-creator-question';
    domBody.appendChild(domTextbox);
    question.domTextBox = domTextbox;
    const domDomTextBoxErr = document.createElement('span');
    domTextbox.addEventListener('input',
        () => {
            question.question = domTextbox.value;
            domDomTextBoxErr.classList.remove('active');
            domDomTextBoxErr.innerText = '';
        });
    domDomTextBoxErr.className = 'error';
    domBody.appendChild(domDomTextBoxErr);
    question.domTextBoxErr = domDomTextBoxErr;
    
    
    
    switch (type) {
        case TypesEnum.OPEN:
            AddOpenQuestion(question);
            break;
        case TypesEnum.STATEMENT:
            AddStatementQuestion(question,domBody);
            return;
        case TypesEnum.MULTIPLECHOICE:
        case TypesEnum.DROPDOWN:
        case TypesEnum.SINGLECHOICE:
            AddChoiceQuestion(question, domBody);
            break;
        case TypesEnum.LOCATION:
            AddLocationQuestion(question, domBody);
            break;
        default:
    }

    domBody.append(domSettings);

    // add bool required or not
    const setting = document.createElement('div');
    setting.className = 'checkbox-option form-check';
    const id = generateId();
    const lblRequired = document.createElement('label');
    lblRequired.setAttribute('for', id);
    lblRequired.innerHTML = `${constants.REQUIRED}?`;
    const inputRequired = document.createElement('input');
    inputRequired.className = 'form-check-input';
    inputRequired.setAttribute('type', 'checkbox');
    inputRequired.id = id;
    inputRequired.addEventListener('change', function () {
        question.required = inputRequired.checked;
    });

    setting.appendChild(inputRequired);
    setting.appendChild(lblRequired);

    domSettings.appendChild(setting);

    // Tools to move/delete questions
    const domTools = document.createElement('div');

    // Button to move element one index up
    const btnUp = createButton(ICONS.UP);
    domTools.appendChild(btnUp);
    btnUp.addEventListener('click',
        () => {
            if (question.index !== 0) { // check if already top object
                question.index--;
                moveElement(question);
            }
        });

    // Button to move element one index down
    const btnDown = createButton(ICONS.DOWN);
    domTools.appendChild(btnDown);
    btnDown.addEventListener('click',
        () => {
            if (question.index !== Questions.length - 1) { // check if already bottom element
                question.index++;
                moveElement(question);
            }
        });

    // Button to remove element
    const btnDelete = createButton(ICONS.DELETE);
    domTools.appendChild(btnDelete);
    btnDelete.addEventListener('click',
        () => {
            removeElement(domQuestion, question);
        });

    const btnMove = createButton(ICONS.MOVE);
    btnMove.className += ' questionHandle';
    domTools.appendChild(btnMove);

    domHeader.appendChild(domTools);
}

function makeButton(type, buttons) {
    // Button DOM element
    const button = document.createElement('button');
    button.setAttribute('type', 'button');
    button.className = 'btn-default new-question-btn';
    button.addEventListener('click',
        () => {
            addElement(type, Questions.length);
            buttons.domWrapper.removeChild(buttons.domElement);
            buttons.domWrapper.appendChild(buttons.domNewQuestion);
        });
    button.innerHTML = TypesString[type];
    buttons.domElement.appendChild(button);
    
}

function addFormButtons(question = null) {
    // Get wrapper for buttons
    const wrapper = document.getElementById('buttons-wrapper');

    // wrapper for the buttons
    const buttonsWrapper = document.createElement('div');

    // button 'new question'
    const btnNewQuestion = document.createElement('button');
    btnNewQuestion.setAttribute('type', 'button');
    btnNewQuestion.className = 'btn-default';
    btnNewQuestion.addEventListener('click',
        () => {
            wrapper.removeChild(btnNewQuestion);
            wrapper.appendChild(buttonsWrapper);
        });
    btnNewQuestion.innerHTML = constants.NEW;
    const icoNewQuestion = document.createElement('span');
    icoNewQuestion.className = 'fa fa-plus icon-in-button';
    btnNewQuestion.appendChild(icoNewQuestion);
    wrapper.appendChild(btnNewQuestion);

    // All need information to add the buttons
    const buttons = {
        domElement: buttonsWrapper,
        question,
        domNewQuestion: btnNewQuestion,
        domWrapper: wrapper,
    };

    // Add all the neede buttons buttons
    if (FormType === 0) { // Form
        makeButton(TypesEnum.OPEN, buttons);
        makeButton(TypesEnum.SINGLECHOICE, buttons);
        makeButton(TypesEnum.DROPDOWN, buttons);
        makeButton(TypesEnum.MULTIPLECHOICE, buttons);
    } else if (FormType === 2) { // Ideation
        makeButton(TypesEnum.OPEN, buttons);
        makeButton(TypesEnum.SINGLECHOICE, buttons);
        makeButton(TypesEnum.DROPDOWN, buttons);
        makeButton(TypesEnum.MULTIPLECHOICE, buttons);
        makeButton(TypesEnum.IMAGE, buttons);
        makeButton(TypesEnum.VIDEO, buttons);
        makeButton(TypesEnum.LOCATION, buttons);
    }
}

function addStatementForm() {
    addElement(TypesEnum.STATEMENT,0);
}

function replacer(key, value) { // removes unnecessary info when sending json to server
    if (key.includes('dom') || key.includes('Ref')) return undefined;
    return value;
}

function SaveForm(){
    // Validation
    let isValid = true;
    if (FormType === 0 || FormType === 1) { // form
        if(!formValidation.validate()) isValid = false;
    }else if(FormType === 2){ // ideation
        if(!ideationValidation.validate()) isValid = false;
    }
    if (!validation.validate()) isValid = false;

    if (!isValid) return;
    
    const modal = CreateModal(domModal);

    if (FormType === 0 || FormType === 1) {
        modal.title.innerText = constants.CONFIRM_SAVE_FORM;
    }else if(FormType === 2){
        modal.title.innerText = constants.CONFIRM_SAVE_IDEATION;
    }
    
    const domText = document.createElement('p');
    domText.innerText = constants.SAVE_IDEATION_CONFIRMATION;
    modal.body.appendChild(domText);
    
    const domConfirm = document.createElement('button');
    domConfirm.className = 'btn-default';
    domConfirm.innerText = constants.SAVE;
    modal.body.appendChild(domConfirm);
    const domSpinner1 = document.createElement('span');
    domSpinner1.className = 'spinner-border spinner-border-sm ml-1 d-none';
    domSpinner1.setAttribute('role','status');
    domSpinner1.setAttribute('aria-hidden','true');
    domConfirm.appendChild(domSpinner1);
    domConfirm.addEventListener('click', () => {
        domSpinner1.classList.remove('d-none');
        domConfirmAndSave.disabled = true;
        domConfirm.disabled = true;
        ConfirmSaveForm();
    });

    const domConfirmAndSave = document.createElement('button');
    domConfirmAndSave.className = 'btn-default ml-2';
    domConfirmAndSave.innerText = constants.SAVE_AND_OPEN;
    modal.body.appendChild(domConfirmAndSave);
    const domSpinner2 = document.createElement('span');
    domSpinner2.className = 'spinner-border spinner-border-sm ml-1 d-none';
    domSpinner2.setAttribute('role','status');
    domSpinner2.setAttribute('aria-hidden','true');
    domConfirmAndSave.appendChild(domSpinner2);
    domConfirmAndSave.addEventListener('click', () => {
            domSpinner2.classList.remove('d-none');
            domConfirmAndSave.disabled = true;
            domConfirm.disabled = true;
        ConfirmSaveForm(true)}
        );
}

// Saving the form
function ConfirmSaveForm(open = false) {
    // Get project ID and phate ID
    const projectId = document.getElementById('ProjectId').value;
    const phaseId = document.getElementById('PhaseId').value;

    // url for post
    let postUrl;

    // data in json form
    let jsonObject;

    if (FormType === 0 || FormType === 1) { // form
        const formTitle = document.getElementById('FormTitle').value;
        const form = {
            projectId,
            phaseId,
            formTitle,
            isStatementForm: FormType === 1,
            questions: Questions,
        };
        postUrl = '/api/forms';
        // Convert to json object, omit al unnecessary data
        jsonObject = JSON.stringify(form, replacer);
    } else if (FormType === 2) { // Ideation
        const centralQuestion = document.getElementById('CentralQuestion').value;
        const description = document.getElementById('Description').value;
        const url = document.getElementById('Url').value;
        const domIdeationType = document.getElementById('IdeationType');
        const ideationType = domIdeationType.options[domIdeationType.selectedIndex].value;
        const ideation = {
            projectId,
            phaseId,
            ideationType,
            centralQuestion,
            description,
            url,
            questions: Questions,
        };
        postUrl = '/api/ideations';
        // Convert to json object, omit al unnecessary data
        jsonObject = JSON.stringify(ideation, replacer);
    } else {
        console.log('form type not supported'); // form type should be 0 or 1
        return;
    }

    const headers = {
        'Content-Type': 'application/json',
    };

    
    axios.post(postUrl, jsonObject, {headers})
        .then((response) => {
            if (open){
                window.location.href = response.headers.location;
            } else{
                window.location.href = '/project/details/' + projectId;
            }
        })
        .catch();
   
    
}

function initValidation() {
    if (FormType === 0 || FormType === 1) { // form
        formValidation.initValidation();
    }else if(FormType === 2){ // ideation
        ideationValidation.initValidation();
    }
    validation.initValidation(Questions);
}

// type 0: form, type 2: ideation
export default function CreateForm(formType) {
    // Set form type
    FormType = formType;
    if (isInitialized){ //Clear everything
        Questions.splice(0,Questions.length);
        document.getElementById('buttons-wrapper').innerHTML = '';
        
    }
    if(formType === -1){
        document.getElementById('form-wrapper').innerText = constants.NO_FORMTYPE_CHOSEN;
        return;
    }
    
    domModal = document.getElementById('modal');
    
    
    // Get save button
    const saveBtn = document.getElementById('SaveBtn');
    saveBtn.addEventListener('click', () => {
        SaveForm();
    });

    console.log(formType);
    if (formType === 1){
        initValidation();
        addStatementForm(); // Form met een stelling
    } else {
        // add form buttons
        addFormButtons();

        // Get wrapper for form elements
        const domForm = document.getElementById('form-wrapper');
        // Initiate drag n drop
        new Sortable(domForm, {
            animation: 150,
            onEnd(evt) {
                const question = Questions[evt.oldIndex];
                question.index = evt.newIndex;
                moveElement(question, false);
            },
            handle: '.questionHandle',
            ghostClass: 'question-ghost',
            dragClass: 'question-drag',
        });

        initValidation();
        isInitialized = true;
    }
}
