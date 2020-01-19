import axios from 'axios';
import Sortable from 'sortablejs';
import {Map} from '../map';
import {
    ICONS, createButton, generateId, TypesString, TypesEnum, CreateModal, sleep
} from '../util';
import {EnableValidation, OnSubmit} from '../forms/validation';
import * as constants from '../constants';
import $ from 'jquery';
import Draggabilly from 'draggabilly';
import Packery from 'packery';


let Questions = [];
const RemovedQuestions = [];
const Answers = [];

const domObjects = {};

let IdeationId;
let UserId;

let replyTitleElement;
let replyTitle;
let replyTitleErr;



let pckry;

function removeAnswer(question) {
    domObjects.ideationWrapper.removeChild(question.dom);
    const index = Answers.indexOf(question.answer);
    if (index >= 0) {
        Answers.splice(index, 1);
    }


    if (RemovedQuestions.length < 1) {
        domObjects.addQuestionWrapper.classList.remove('d-none');
    }
    RemovedQuestions.push(question);
    addQuestionsToAddList();
    pckry.layout();
}

function addQuestionsToAddList() {
    domObjects.addQuestionSelect.innerHTML = '';
    if (RemovedQuestions.length < 1) {
        domObjects.addQuestionWrapper.classList.add('d-none');
        return;
    }
    RemovedQuestions.forEach((element, index, array) => {
        const domOption = document.createElement('option');
        domOption.innerText = element.questionString;
        domOption.setAttribute('value', index);
        domObjects.addQuestionSelect.appendChild(domOption);
    });
}

function reAddQuestion() {
    const questionIndex = domObjects.addQuestionSelect.value;
    addQuestionToList(RemovedQuestions[questionIndex]);
    removeQuestionFromAddList(questionIndex);
}

function removeQuestionFromAddList(questionIndex) {
    RemovedQuestions.splice(questionIndex, 1);
    addQuestionsToAddList();
}

function addOpenQuestion(question) {
    let domQuestionInput;
    if (!question.longAnswer) {
        domQuestionInput = document.createElement('input');
    } else {
        domQuestionInput = document.createElement('textarea');
        domQuestionInput.style.height = '200px';
        domQuestionInput.addEventListener("heightChange", () => {
            console.log('resize ');
            pckry.layout();
        });
    }
    domQuestionInput.id = question.inputId;
    domQuestionInput.setAttribute('type', 'text');
    domQuestionInput.classList.add('form-control');
    domQuestionInput.classList.add('question-input');
    domQuestionInput.addEventListener('input', () => {
        question.answer.OpenAnswer = domQuestionInput.value;
    });
    question.domBody.appendChild(domQuestionInput);
}

function addMediaQuestion(question) {
    
    const domWrapper = document.createElement('div');
    domWrapper.className = 'box';
    question.domBody.appendChild(domWrapper);
    
    const domInputWrapper = document.createElement('div');
    domInputWrapper.className = 'box__input';
    domWrapper.appendChild(domInputWrapper);
    
    const id = generateId();
    
    const domInput = document.createElement('input');
    domInput.id = id;
    domInput.className = 'box__file';
    domInput.setAttribute('type','file');
    domInputWrapper.appendChild(domInput);
    
    const domLabel = document.createElement('label');
    domLabel.setAttribute('for',id);
    domInputWrapper.appendChild(domLabel);
    
    const domLabelSpan1 = document.createElement('span');
    domLabelSpan1.className = 'font-weight-bold choose-text';
    if (question.fieldType === TypesEnum.IMAGE){
        domLabelSpan1.innerText = constants.CHOOSE_IMAGE;
        domInput.setAttribute('accept','image/*')
    } else{
        domLabelSpan1.innerText = constants.CHOOSE_VIDEO;
        domInput.setAttribute('accept','video/mp4,video/webm,video/ogg')
    }
    
    domLabel.appendChild(domLabelSpan1);

    const domLabelSpan2 = document.createElement('span');
    domLabelSpan2.innerHTML = `<br/>${constants.DRAG}<br/>`;
    domLabel.appendChild(domLabelSpan2);
    
    //Prevent default behaviour
    function preventDefault(e){
        e.preventDefault();
        e.stopPropagation();
    }
    
    domWrapper.addEventListener('drag',preventDefault);
    domWrapper.addEventListener('dragstart',preventDefault);
    domWrapper.addEventListener('dragend',preventDefault);
    domWrapper.addEventListener('dragover',preventDefault);
    domWrapper.addEventListener('dragenter',preventDefault);
    domWrapper.addEventListener('dragleave',preventDefault);
    domWrapper.addEventListener('drop',preventDefault);
    
    function dragEnter(){
        domWrapper.classList.add('is-dragover')
    }
    
    domWrapper.addEventListener('dragover',dragEnter);
    domWrapper.addEventListener('dragEnter',dragEnter);
    
    function dragExit(){
        domWrapper.classList.remove('is-dragover')
    }
    domWrapper.addEventListener('dragleave',dragExit);
    domWrapper.addEventListener('dragend',dragExit);
    
    
    // Media input events
    function handleAddFile(file){
        const fileType = file['type'];
        let validTypes;
        if (question.fieldType === TypesEnum.IMAGE){
            validTypes = ['image/gif', 'image/jpeg', 'image/png'];
        } else{
            validTypes = ['video/mp4', 'video/webm', 'video/ogg'];
        }
        
        if (!validTypes.includes(fileType)) {
            domLabelSpan1.innerText = constants.TYPE_NOT_SUPPORTED;
            domLabelSpan2.innerText = '';
            question.answer.FileAnswer = undefined;
            return;
        }
        
        
        
        question.answer.FileAnswer = file;
        domLabelSpan1.innerText = file.name;
        domLabelSpan2.innerText = '';
    }
    
    domWrapper.addEventListener('drop',(e)=> {
        dragExit();
        handleAddFile(e.dataTransfer.files[0]);
    });

    domInput.addEventListener('input', () => {
        handleAddFile(domInput.files[0]);
    });
    
    
    
    


}

function addSingleQuestion(question) {
    const id = generateId();
    question.options.forEach((element, index, array) => {
        const domLabel = document.createElement('label');
        domLabel.className = 'col-12';
        domLabel.setAttribute('for', element);
        const domLabelSpan = document.createElement('span');
        domLabelSpan.innerText = element;
        const domInput = document.createElement('input');
        domInput.setAttribute('type', 'radio');
        domInput.setAttribute('name', id);
        domInput.className = 'question-input';
        domInput.id = element;
        domLabel.appendChild(domInput);
        domLabel.appendChild(domLabelSpan);

        // Set single choice answer to radio button that is currently being checked
        domInput.addEventListener('change', () => {
            question.answer.SingleAnswer = index;
        });

        question.domBody.appendChild(domLabel);
    });
}

function addMultipleQuestion(question) {
    question.options.forEach((element, index, array) => {
        const domLabel = document.createElement('label');
        domLabel.className = 'col-12';
        domLabel.setAttribute('for', element);
        const domLabelSpan = document.createElement('span');
        domLabelSpan.innerText = element;
        const domInput = document.createElement('input');
        domInput.setAttribute('type', 'checkbox');
        domInput.id = element;
        domInput.className = 'question-input';
        domLabel.appendChild(domInput);
        domLabel.appendChild(domLabelSpan);

        question.answer.MultipleAnswer.push(false);

        // Add/remove checkbox that is being checked to multiple choice answer
        domInput.addEventListener('change', () => {
            question.answer.MultipleAnswer[index] = domInput.checked;
        });

        question.domBody.appendChild(domLabel);
    });
}

function addDropDownQuestion(question) {
    const domSelect = document.createElement('select');
    domSelect.className = 'form-control question-input';
    const domFirstOption = document.createElement('option');
    domFirstOption.innerText = 'Maak een keuze';
    domFirstOption.className = 'option-zero';
    domFirstOption.setAttribute('value', -1);
    domSelect.appendChild(domFirstOption);
    question.options.forEach((element, index, array) => {
        const domOption = document.createElement('option');
        domOption.innerText = element;
        domOption.setAttribute('value', index);
        domSelect.appendChild(domOption);
        // Set single choice answer to radio button that is currently being checked
    });
    question.answer.SingleAnswer = domSelect.value;
    domSelect.addEventListener('change', () => {
        question.answer.SingleAnswer = domSelect.value;
    });
    question.domBody.appendChild(domSelect);
}

function addLocationQuestion(question) {
    const domSearchBox = document.createElement('input');
    domSearchBox.id = 'pac-input';
    domSearchBox.className = 'controls';
    domSearchBox.setAttribute('type', 'text');
    domSearchBox.setAttribute('placeholder', constants.MAP_SERACH);

    const domMap = document.createElement('div');
    domMap.className = 'map';
    question.domBody.appendChild(domMap);

    function showMap(question) {
        let searchOrigin;
        let
            map;
        let locationOptions = question.location;

        Map.loadGoogleMapsApi().then((googleMaps) => {
            map = new googleMaps.Map(domMap, {
                center: {lat: locationOptions.latitude, lng: locationOptions.longitude},
                zoom: locationOptions.zoomLevel,
                zoomControl: true,
                mapTypeControl: true,
                scaleControl: false,
                streetViewControl: false,
                rotateControl: false,
                fullscreenControl: false
            });
            

            function updateLocation() {
                const lat = map.center.lat();
                const long = map.center.lng();
                const zoom = map.zoom;
                //console.log(zoom);

                question.answer.LocationAnswer.Latitude = lat;
                question.answer.LocationAnswer.Longitude = long;
                question.answer.LocationAnswer.ZoomLevel = zoom;
                // marker.setPosition({lat:lat,lng:long});
            }

            map.addListener('center_changed', () => {
                updateLocation();
            });
            updateLocation();

            let animate = false;
            const domMarker = document.createElement('img');
            domMarker.setAttribute('src', '../../dist/markerIcon.svg');
            domMarker.className = 'centerMarker marker';
            domMap.appendChild(domMarker);

            const domXMarker = document.createElement('img');
            domXMarker.setAttribute('src', '../../dist/crossIcon.svg');
            domXMarker.className = 'xMarker marker';
            domMap.appendChild(domXMarker);
            domXMarker.style.display = 'none';


            function animateMarker() {
                if (animate) {
                    $(domMarker).animate({
                        top: '-=20px'
                    }, 500).animate({
                        top: '+=20px'
                    }, 500, animateMarker);
                }
            }

            function stopAnimation() {
                $(domMarker).finish();
            }

            map.addListener('dragstart', () => {
                domXMarker.style.display = 'block';
                animate = true;
                animateMarker();
            });

            map.addListener('dragend', () => {
                domXMarker.style.display = 'none';
                animate = false;
                stopAnimation();
            });

            /*
            let marker = new googleMaps.Marker({
                    position: { lat: position.coords.latitude, lng: position.coords.longitude },
                    map: map,
                    //draggable:true,
                    title:"Drag me!"
            }); */
            const searchBox = new googleMaps.places.SearchBox(domSearchBox);
            map.controls[google.maps.ControlPosition.TOP_LEFT].push(domSearchBox);
            // map.addListener('bounds_changed', function() {
            //  searchBox.setBounds(map.getBounds());
            //  });
            domSearchBox.addEventListener('keydown', (event) => {
                if (event.key === 'Enter') {
                    const location = domSearchBox.value;
                    const geocoder = new googleMaps.Geocoder();
                    geocoder.geocode({address: location}, (data) => {
                        const lat = data[0].geometry.location.lat();
                        const lng = data[0].geometry.location.lng();
                        searchOrigin = new googleMaps.LatLng(lat, lng);
                        map.setCenter(searchOrigin);
                    });
                }
            });
        });
    }

    showMap(question);
}

function SaveIdeation() {
    let isValid = OnSubmit();
    if (!replyTitleElement.validity.valid) {
        replyTitleErr.innerText = constants.ERR_OPENTEXT;
        replyTitleErr.classList.add = 'active';
        isValid = false;
    }
    if (!isValid) {
        pckry.layout();
        return;
    }

    const modal = CreateModal(domObjects.confirmPopup);

    modal.title.innerText = 'Ideation indienen';
    const domText = document.createElement('p');
    domText.innerText = constants.REPLY_IDEATION_CONFIRMATION;
    modal.body.appendChild(domText);
    const domConfirm = document.createElement('button');
    domConfirm.className = 'btn-default';
    domConfirm.innerText = constants.SEND;
    modal.body.appendChild(domConfirm);

    const domSpinner1 = document.createElement('span');
    domSpinner1.className = 'spinner-border spinner-border-sm ml-1 d-none';
    domSpinner1.setAttribute('role','status');
    domSpinner1.setAttribute('aria-hidden','true');
    domConfirm.appendChild(domSpinner1);
    
    domConfirm.addEventListener('click', () => {
        domSpinner1.classList.remove('d-none');
        domConfirm.disabled = true;
        ConfirmSaveIdeation();
    });
}

function ConfirmSaveIdeation() {
    const postUrl = '/api/ideations/reply';

    const data = new FormData();

    data.append('IdeationId', IdeationId);
    data.append('UserId', UserId);
    data.append('title',replyTitle);
    
    //Array sorteren
    Answers.forEach(element => {
        element.orderIndex = element.domQuestion.getAttribute('orderindex');
    });
    Answers.sort((a,b) => (a.orderIndex > b.orderIndex) ? 1 : -1);
    let index = 0;
    Answers.forEach((element) => {
        let isAnswered = true;
        switch (element.FieldType) {
            case TypesEnum.OPEN:
                if (element.OpenAnswer != null) {
                    data.append(`Answers[${index}].OpenAnswer`, element.OpenAnswer);
                } else isAnswered = false;
                break;
            case TypesEnum.IMAGE:
            case TypesEnum.VIDEO:
                if (element.FileAnswer != null) {
                    data.append(`Answers[${index}].FileAnswer`, element.FileAnswer);
                } else isAnswered = false;
                break;
            case TypesEnum.DROPDOWN:
            case TypesEnum.SINGLECHOICE:
                if (element.SingleAnswer != null && element.SingleAnswer !== -1) {
                    data.append(`Answers[${index}].SingleAnswer`, element.SingleAnswer);
                } else isAnswered = false;
                break;
            case TypesEnum.MULTIPLECHOICE:
                if (element.MultipleAnswer.length > 0) {
                    element.MultipleAnswer.forEach((element2, index2, array) => {
                        data.append(`Answers[${index}].MultipleAnswer[${index2}]`, element2);
                        //
                    });
                } else isAnswered = false;

                break;
            case TypesEnum.LOCATION:
                if (element.LocationAnswer.Latitude != null) {
                    data.append(`Answers[${index}].LocationAnswer.Latitude`, element.LocationAnswer.Latitude);
                    data.append(`Answers[${index}].LocationAnswer.Longitude`, element.LocationAnswer.Longitude);
                    data.append(`Answers[${index}].LocationAnswer.ZoomLevel`, element.LocationAnswer.ZoomLevel);
                } else isAnswered = false;
                break;
            default:
                break;
        }

        if (isAnswered) {
            data.append(`Answers[${index}].QuestionIndex`, element.QuestionIndex);
            data.append(`Answers[${index}].FieldType`, element.FieldType);
            index++;
        }
    });


    const headers = {
        'Content-Type': 'multipart/form-data',
    };

    axios({
        method: 'post',
        url: postUrl,
        data,
        config: {headers},
    })
        .then((response) => {
            window.location.href = response.headers.location; //redirect to the ideation reply page
        })
        .catch((err) => {
            alert('er ging iets mis :(');
            console.log(err);
        });
}

function addQuestionToList(question) {
    const domQuestion = document.createElement('div');
    domQuestion.className = 'form-creator-box question grid-item';
    question.dom = domQuestion;
    const domHeader = document.createElement('div');
    const domBody = document.createElement('div');
    domHeader.className = 'header';
    domBody.className = 'body';
    domQuestion.appendChild(domHeader);
    domQuestion.appendChild(domBody);

    question.domBody = domBody;

    // Add hidden input fields for validation
    var domFieldType = document.createElement('input');
    domFieldType.className = 'field-type';
    domFieldType.setAttribute('value', question.fieldType);
    domFieldType.hidden = true;
    const domRequired = document.createElement('input');
    domRequired.className = 'required';
    domRequired.setAttribute('value', question.required);
    domRequired.hidden = true;
    domQuestion.appendChild(domFieldType);
    domQuestion.appendChild(domRequired);

    const answer = {
        domQuestion: domQuestion,
        QuestionIndex: question.index,
        FieldType: question.fieldType,
        OpenAnswer: undefined,
        SingleAnswer: null,
        MultipleAnswer: [],
        LocationAnswer: {
            Latitude: undefined,
            Longitude: undefined,
            ZoomLevel: undefined,
        },
        FileAnswer: undefined,
    };
    Answers.push(answer);
    question.answer = answer;

    const id = generateId();
    question.inputId = id;

    /*
    var domFieldType = document.createElement('h5');
    if (question.required) {
        domFieldType.innerText = 'Verplichte vraag';
    } else {
        domFieldType.innerText = 'Optionele vraag';
    }

    domHeader.appendChild(domFieldType);
    */


    const domQuestionLabel = document.createElement('label');
    domQuestionLabel.innerText = question.questionString;
    domQuestionLabel.setAttribute('for', id);
    domQuestionLabel.className = 'question-label w-70 mb-0';
    domHeader.appendChild(domQuestionLabel);


    switch (question.fieldType) {
        case TypesEnum.OPEN:
            addOpenQuestion(question);
            break;
        case TypesEnum.IMAGE:
        case TypesEnum.VIDEO:
            addMediaQuestion(question);
            break;
        case TypesEnum.SINGLECHOICE:
            addSingleQuestion(question);
            break;
        case TypesEnum.MULTIPLECHOICE:
            addMultipleQuestion(question);
            break;
        case TypesEnum.LOCATION:
            addLocationQuestion(question);
            break;
        case TypesEnum.DROPDOWN:
            addDropDownQuestion(question);
            break;
    }


    const domTools = document.createElement('div');
    domTools.className = 'w-30';


    if (!question.required) {
        const btnDelete = createButton(ICONS.DELETE);
        domTools.appendChild(btnDelete);
        btnDelete.addEventListener('click', () => {
            removeAnswer(question);
        });
    }

    const btnMove = createButton(ICONS.MOVE);
    btnMove.className = 'questionHandle';
    domTools.appendChild(btnMove);

    domHeader.appendChild(domTools);


    const domError = document.createElement('span');
    domError.className = 'error';
    domQuestion.appendChild(domError);


    domObjects.ideationWrapper.appendChild(domQuestion);
    
    
    pckry.appended(domQuestion);
    pckry.layout();

    var draggie = new Draggabilly(domQuestion, {
        handle: '.questionHandle',
    });

    pckry.bindDraggabillyEvents(draggie);

    
}

function showQuestions(questions) {
    Questions = questions;
    Questions.forEach((element, index, array) => {
        element.index = index;
        element.hidden = false;
        addQuestionToList(element);
    });
    EnableValidation();
}

function initiateGrid() {

    function orderItems() {
        pckry.getItemElements().forEach(function (itemElem, i) {
            itemElem.setAttribute('orderindex', i);
        });
    }

    pckry.on('layoutComplete', orderItems);
    pckry.on('dragItemPositioned', orderItems);
    
    pckry.layout();
}

function loadQuestions() {
    axios.get(`/api/ideations/questions/${IdeationId}`)
        .then(async (response) => {
            showQuestions(response.data);
            initiateGrid();
            await sleep(500);
            pckry.layout();
        }).catch((error) => {
        console.log(error);
    });

}

function init() {
    //Get ID's
    IdeationId = document.getElementById('ideation-id').value;
    UserId = document.getElementById('user-id').value;
    //Get DOM elements
    domObjects.ideationWrapper = document.getElementById('ideation-wrapper');
    domObjects.btnAddQuestion = document.getElementById('AddQuestionBtn');
    domObjects.addQuestionWrapper = document.getElementById('addquestion-wrapper');
    domObjects.addQuestionSelect = document.getElementById('addquestion-select');
    domObjects.addQuestionButton = document.getElementById('addquestion-button');
    domObjects.confirmPopup = document.getElementById('confirm-popup');
    replyTitleElement = document.getElementById('title');
    replyTitleErr = document.getElementById('title-error');
    
    replyTitleElement.addEventListener('input',() => {
        replyTitle = replyTitleElement.value;
        replyTitleErr.innerText = '';
        replyTitleErr.classList.remove('active');
    });

    const grid = domObjects.ideationWrapper;

    pckry = new Packery(grid, {
        // options
        itemSelector: '.grid-item',
        gutter: 10
    });
    
    loadQuestions();

    const saveBtn = document.getElementById('SaveBtn');
    saveBtn.addEventListener('click', () => {
        SaveIdeation();
    });
    domObjects.addQuestionButton.addEventListener('click', () => {
        reAddQuestion();
    });
    
}

window.onload = init;
