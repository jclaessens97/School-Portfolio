import axios from "axios";
import {Map} from "../map";
import $ from "jquery";
import {getCookie} from "../util";
import {LoadIoT} from "../iot";

let PhasesLiElements = [];
let PhasesDivs = [];

let PhasesCount;

let FormIdElement;
/*let FormQuestionElement;
let Form;
let FormQuestion;
let answer;
let upvotes;
let downvotes;

let votesUpProgress;
let votesDownProgress;

let ProjectId;
let iotWrapper;*/




/*function setVotesProgress() {
    const totalVotes = upvotes + downvotes;
    const scaledUpvotes = upvotes / totalVotes * 100;
    const scaledDownvotes = downvotes / totalVotes * 100;


    votesUpProgress.style.width = scaledUpvotes + '%';
    votesDownProgress.style.width = scaledDownvotes + '%';


}*/

function hideDivs() {
    for (let i = 0; i < PhasesCount; i++) {
        PhasesDivs[i].classList.add('d-none');
        PhasesLiElements[i].classList.remove('selected');
    }
}

function showDiv(index) {
    PhasesDivs[index].classList.remove('d-none');
    PhasesLiElements[index].classList.add('selected');
}

function addEventHandlers() {
    for (let i = 0; i < PhasesCount; i++) {
        PhasesLiElements[i].addEventListener('click', () => {
            hideDivs();
            showDiv(i);
        });
    }
}

function getElements() {
    const progressBar = document.getElementsByClassName('progressbar')[0];
    PhasesLiElements = Array.from(progressBar.getElementsByTagName('li'));
    PhasesCount = PhasesLiElements.length;

    for (let i = 1; i < PhasesCount + 1; i++) {
        const PhaseDiv = document.getElementById(`phase${i}`);
        PhasesDivs.push(PhaseDiv);
    }

/*    FormIdElement = document.getElementById("form-id");
    FormQuestionElement = document.getElementById("form-question");
    iotWrapper = document.getElementById('iot-wrapper');*/
    //ProjectId = document.getElementById('project-id').value;
}

/*function showVoteResults(upvotes, downvotes) {


    const votesUpWrapper = document.createElement('div');
    votesUpWrapper.className = 'w-50';

    const votesUpText = document.createElement('span');
    votesUpText.className = 'mr-2';
    votesUpText.innerText = 'Voor';
    votesUpWrapper.appendChild(votesUpText);
    const votesUpDisplay = document.createElement('div');
    votesUpDisplay.className = 'progress votes-display';
    votesUpWrapper.appendChild(votesUpDisplay);
    votesUpProgress = document.createElement('div');
    votesUpProgress.className = 'progress-bar';
    votesUpDisplay.appendChild(votesUpProgress);


    FormQuestionElement.appendChild(votesUpWrapper);

    const votesDownWrapper = document.createElement('div');
    votesDownWrapper.className = 'w-50';

    const votesDownText = document.createElement('span');
    votesDownText.className = 'mr-2';
    votesDownText.innerText = 'Tegen';
    votesDownWrapper.appendChild(votesDownText);
    const votesDownDisplay = document.createElement('div');
    votesDownDisplay.className = 'progress votes-display';
    votesDownWrapper.appendChild(votesDownDisplay);
    votesDownProgress = document.createElement('div');
    votesDownProgress.className = 'progress-bar';

    votesDownDisplay.appendChild(votesDownProgress);


    FormQuestionElement.appendChild(votesDownWrapper);

    setVotesProgress();

}*/

/*
function sendAnswer() {
    const url = `/api/forms/vote/${FormIdElement.value}/${answer}`;

    axios.post(url)
        .then((r) => {
            document.cookie = "voted=true";
            upvotes = r.data.upvotes;
            downvotes = r.data.downvotes;
            showVoteResults();
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het voten:');
            console.log(err);
        })
}
*/

/*function ShowStatement() {
    const formLoader = document.getElementById('form-loader');
    formLoader.classList.remove('d-block');
    FormQuestionElement.classList.remove('justify-content-center');
    formLoader.classList.add('d-none');

    //FormQuestionElement.classList.remove('d-flex');
    //FormQuestionElement.classList.remove('align-items-center');
    const titleElement = document.createElement('h5');
    titleElement.innerText = Form.questions[0].questionString;
    FormQuestionElement.appendChild(titleElement);

    const wrapper = document.createElement('div');
    FormQuestionElement.appendChild(wrapper);

    const proLabel = document.createElement('label');
    proLabel.setAttribute('for', 'pro');
    proLabel.className = 'w-100';
    const proSpan = document.createElement('span');
    proSpan.innerText = 'Voor';
    const proInput = document.createElement('input');
    proInput.className = 'mr-2';
    proInput.setAttribute('value', '1');
    proInput.setAttribute('type', 'radio');
    proInput.setAttribute('id', 'pro');
    proInput.setAttribute('name', 'status');
    proLabel.appendChild(proInput);
    proLabel.appendChild(proSpan);
    wrapper.appendChild(proLabel);

    const contraLabel = document.createElement('label');
    contraLabel.setAttribute('for', 'contra');
    contraLabel.className = 'w-100';
    const contraSpan = document.createElement('span');
    contraSpan.innerText = 'Tegen';
    const contraInput = document.createElement('input');
    contraInput.className = 'mr-2';
    contraInput.setAttribute('value', '0');
    contraInput.setAttribute('type', 'radio');
    contraInput.setAttribute('id', 'contra');
    contraInput.setAttribute('name', 'status');
    contraLabel.appendChild(contraInput);
    contraLabel.appendChild(contraSpan);
    wrapper.appendChild(contraLabel);

    const sendBtn = document.createElement('button');
    sendBtn.disabled = true;
    sendBtn.className = 'btn-default';
    sendBtn.innerText = 'Verzenden';
    wrapper.appendChild(sendBtn);

    proInput.addEventListener('change', () => {
        sendBtn.disabled = false;
        answer = proInput.value;
    });

    contraInput.addEventListener('change', () => {
        sendBtn.disabled = false;
        answer = contraInput.value;
    });
    sendBtn.addEventListener('click', () => {
        wrapper.innerText = '';
        //wrapper.appendChild(formLoader);
        formLoader.classList.add('d-block');
        formLoader.classList.remove('d-none');
        sendAnswer();
    })

}*/

/*function ShowResults() {
    const formLoader = document.getElementById('form-loader');
    formLoader.classList.remove('d-block');
    FormQuestionElement.classList.remove('justify-content-center');
    formLoader.classList.add('d-none');

    const titleElement = document.createElement('h5');
    titleElement.innerText = FormQuestion;
    FormQuestionElement.appendChild(titleElement);

    showVoteResults(upvotes, downvotes);

}*/

/*function loadVotes() {
    const url = `/api/forms/votes/${FormIdElement.value}`;

    axios.get(url)
        .then((r) => {
            FormQuestion = r.data.title;
            upvotes = r.data.upvotes;
            downvotes = r.data.downvotes;
            ShowResults();

        })
        .catch((e) => {
            console.log('Something went wrong getting the votes:');
            console.log(e);
        })
}*/

/*function loadStatement() {
    const voted = getCookie("voted");

    if (voted) {
        loadVotes();
        return;
    }

    const url = `/api/forms/${FormIdElement.value}`;
    axios.get(url)
        .then((r) => {
            Form = r.data;
            ShowStatement();

        })
        .catch((e) => {
            console.log('Something went wrong getting statement:');
            console.log(e);
        })

}*/

/*function showIoT(iot) {
    const wrapper = document.createElement('div');
    wrapper.className = 'col-md-4';
    const card = document.createElement('div');
    card.className = 'card mb-4 shadow-sm bg-light';
    wrapper.appendChild(card);
    const mapElement = document.createElement('div');
    mapElement.className = 'w-100';
    mapElement.setAttribute('style','height: 225px');
    card.appendChild(mapElement);
    const cardBody = document.createElement('div');
    cardBody.className = 'card-body';
    card.appendChild(cardBody);
    const cardText = document.createElement('p');
    cardText.innerHTML = "Deze opstelling is <strong>nu</strong> te vinden op deze locatie, als je in de buurt bent kom dan zeker een kijkje nemen en geef je mening.";
    cardBody.appendChild(cardText);

    function showMap() {
        let map;

        Map.loadGoogleMapsApi().then((googleMaps) => {
            map = new googleMaps.Map(mapElement, {
                center: {lat: iot.location.latitude, lng: iot.location.longitude},
                zoom: iot.location.zoomLevel,
                zoomControl: true,
                mapTypeControl: true,
                scaleControl: false,
                streetViewControl: false,
                rotateControl: false,
                fullscreenControl: false
            });

            let marker = new googleMaps.Marker({
                position: {lat: iot.location.latitude, lng: iot.location.longitude},
                map: map,
                draggable: false,
            });
        });
    }
    
    showMap();


    iotWrapper.appendChild(wrapper);
}*/

/*function loadIot() {
    const url = `/api/project/iot/all/${ProjectId}`;

    axios.get(url)
        .then((r) => {
            r.data.forEach(showIoT);
        })
        .catch((e) => {
            console.log('Something went wrong getting the iot:');
            console.log(e);
        })
}*/

function init() {
    getElements();
    addEventHandlers();
    if (FormIdElement) {
       // loadStatement();
    }
    //loadIot();

    const projectId = document.getElementById('project-id').value;
    if (document.getElementById('iot-count')){
        LoadIoT("project",projectId);
    }
    
}

window.onload = init;