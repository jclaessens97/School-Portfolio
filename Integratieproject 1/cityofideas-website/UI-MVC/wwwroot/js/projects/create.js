/**
 * This module is used for both CREATING and EDITING of a project.
 */
import axios from 'axios';
import $ from 'jquery';
import { getSubdomainTenant, createButton, ICONS } from '../util';
import * as validator from './validation';
import '@chenfengyuan/datepicker/dist/datepicker.esm.js';
import * as constants from "../constants";


let phaseNumber;
let addPhaseInputFormTemplate;
let logo;
let logoChanged = false;

let startDatePicker; let
    endDatePicker;

let modSelect;
let modInput;
let modTable;
let mods = [];
let addedMods = [];

function removePhase(event) {
    // Remove the target node from the container
    const container = document.getElementById('phase-input-container');
    container.removeChild(event.target.parentNode);

    // Re-count the nodes and set the value accordingly
    for (let i = 0; i < phaseNumber - 2; i++) {
        container.children[i].children[0].children[1].value = i + 1;
    }

    phaseNumber--;
}

function addPhase() {
    // Get phase container
    const container = document.getElementById('phase-input-container');

    // Copy the first element to use as template
    const newElem = addPhaseInputFormTemplate.cloneNode(true);
    newElem.children[0].children[1].value = phaseNumber;
    newElem.children[1].children[1].value = '';
    newElem.children[2].children[1].value = '';
    newElem.children[3].children[0].checked = false;
    newElem.children[3].children[0].id = 'phase' + phaseNumber;
    newElem.children[3].children[1].setAttribute('for','phase' + phaseNumber);

    // Create remove button and append it to cloned element
    const removeButton = document.createElement('button');
    removeButton.type = 'button';
    removeButton.className = 'btn btn-danger';
    removeButton.innerHTML = '<i class="fa fa-trash"></i>';
    removeButton.style = 'width: 100%';

    removeButton.addEventListener('click', removePhase);

    newElem.appendChild(removeButton);

    // Add the new element to the list & swap position with add button
    const addPhaseButtonContainer = document.getElementById('addPhase-button-wrapper');
    container.removeChild(addPhaseButtonContainer);
    container.appendChild(newElem);
    container.appendChild(addPhaseButtonContainer);

    phaseNumber++;
}

function generateFormData() {
    let currentPhase = 0;
    const phasesSelects = Array.from(document.getElementsByClassName('phase-current'));
    
    let index = 0;
    phasesSelects.forEach((select) => {
        if (select.checked){
            currentPhase = index+1;
        } 
        index++;
    });
    
    // Manually create formdata object
    const formData = new FormData();
    formData.append('PlatformTenant', getSubdomainTenant());
    formData.append('Title', document.getElementById('Title').value);
    formData.append('Goal', document.getElementById('Goal').value);
    formData.append('Logo', logo);
    formData.append('StartDate', document.getElementById('StartDateInput').value);
    formData.append('EndDate', document.getElementById('EndDateInput').value);
    formData.append('CurrentPhase', currentPhase);

    const projectId = document.getElementById('ProjectId');
    if (projectId) {
        formData.append('ProjectId', projectId.value);
    }

    // Manually parse a phase object and put it in a list to match the projectDto in the backend
    const phaseInputElements = document.getElementsByClassName('phase-input-form');

    for (let i = 0; i < phaseNumber - 1; i++) {
        const phase = {
            number: phaseInputElements[i].children[0].children[1].value,
            title: phaseInputElements[i].children[1].children[1].value,
            description: phaseInputElements[i].children[2].children[1].value,
        };

        formData.append(`Phases[${i}].Number`, phase.number);
        formData.append(`Phases[${i}].Title`, phase.title);
        formData.append(`Phases[${i}].Description`, phase.description);
    }
    
    for (let i = 0; i < addedMods.length; i++) {
        formData.append(`Moderators[${i}]`,addedMods[i].userName);
    }
    
    return formData;
}

async function createProject() {
    if (!validator.isValid()) return;

    const formData = generateFormData();
    // Post the formdata as multipart form
    try {
        const response = await axios.post(
            '/api/project',
            formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            },
        );
        window.location.href = response.headers.location;
    } catch (err) {
        console.error(err);
    }
}

async function updateProject() {
    if (!validator.isValid(logoChanged)) return;

    const formData = generateFormData();
    formData.append('LogoChanged', logoChanged);

    // Put the formdata as multipart form
    try {
        const response = await axios.put(
            `/api/project/${formData.get('ProjectId')}`,
            formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            },
        );
        window.location.href = response.headers.location;
    } catch (err) {
        console.error(err);
    }
}

function addEventHandlers() {
    // Add clickhandler to AddPhase button
    const addPhaseButton = document.getElementById('addPhase');
    addPhaseButton.addEventListener('click', addPhase);

    /*
    // Add onFileChange handler to file input
    const fileInput = document.getElementById('Logo');
    fileInput.addEventListener('change', () => {
        [logo] = fileInput.files;
        logoChanged = true;
    });*/

    // Add clickhandler to CreateProject button (create page)
    const createProjectButton = document.getElementById('createProject');
    if (createProjectButton) {
        createProjectButton.addEventListener('click', createProject);
    } else {
        // Add clickhandler to UpdateProject button (edit page)
        const updateProjectButton = document.getElementById('updateProject');
        updateProjectButton.addEventListener('click', updateProject);
    }

    // * Image upload * //

    const labelBox = document.getElementById('logo-label-box');
    const input = document.getElementById('logo-input');
    const span1 = document.getElementById('logo-span1');
    const span2 = document.getElementById('logo-span2');

    // Prevent default behaviour
    function preventDefault(e){
        e.preventDefault();
        e.stopPropagation();
    }

    labelBox.addEventListener('drag',preventDefault);
    labelBox.addEventListener('dragstart',preventDefault);
    labelBox.addEventListener('dragend',preventDefault);
    labelBox.addEventListener('dragover',preventDefault);
    labelBox.addEventListener('dragenter',preventDefault);
    labelBox.addEventListener('dragleave',preventDefault);
    labelBox.addEventListener('drop',preventDefault);

    function dragEnter(){
        labelBox.classList.add('is-dragover')
    }

    labelBox.addEventListener('dragover',dragEnter);
    labelBox.addEventListener('dragEnter',dragEnter);

    function dragExit(){
        labelBox.classList.remove('is-dragover')
    }
    labelBox.addEventListener('dragleave',dragExit);
    labelBox.addEventListener('dragend',dragExit);


    // Media input events
    function handleAddFile(file) {
        const fileType = file['type'];
        let validTypes;
        validTypes = ['image/gif', 'image/jpeg', 'image/png'];
        if (!validTypes.includes(fileType)) {
            span1.innerText = constants.TYPE_NOT_SUPPORTED;
            span2.innerText = '';
            return;
        }
        logo = file;
        logoChanged = true;
        span1.innerText = file.name;
        span2.innerText = '';
    }


    labelBox.addEventListener('drop',(e)=> {
        dragExit();
        handleAddFile(e.dataTransfer.files[0]);
    });

    input.addEventListener('input', () => {
        handleAddFile(input.files[0]);
    });
    
    // if edit chang
    const logoNameElement = document.getElementById('Logo_Name');
    if (logoNameElement){
        const logoName = logoNameElement.value;
        span1.innerText = logoName;
        span2.innerText = '';
    } 
    
    
}

function filterFunction() {
    let filter, option, i;
    filter = modInput.value.toUpperCase();
    option = modSelect.getElementsByClassName('option');
    for (i = 0; i < option.length; i++) {
        const textSpan = option[i].getElementsByTagName('span')[0];
        let txtValue = textSpan.textContent || textSpan.innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            option[i].classList.add('d-block');
            option[i].classList.remove('d-none');
        } else {
            option[i].classList.add('d-none');
            option[i].classList.remove('d-block');
        }
    }
}

function addModeratorToList(mod) {
    const tr = document.createElement('tr');
    const tdLastName = document.createElement('td');
    
    const tdFirstName = document.createElement('td');
    
    if (mod.isOrganistation) {
        tdLastName.innerText = mod.firmName;
    }else{
        tdLastName.innerText = mod.lastName;
        tdFirstName.innerText = mod.firstName;
    }
    const tdEmail = document.createElement('td');
    tdEmail.innerText = mod.email;
    const tdRemove = document.createElement('td');
    const aRemove = document.createElement('a');
    aRemove.setAttribute('href','#');
    aRemove.innerText = constants.DELETE;
    tdRemove.appendChild(aRemove);
    tr.appendChild(tdLastName);
    tr.appendChild(tdFirstName);
    tr.appendChild(tdEmail);
    tr.appendChild(tdRemove);
    modTable.appendChild(tr);

    addedMods.push(mod);
    aRemove.addEventListener('click',(e) => {
        tr.remove();
        addModeratorToSelect(mod);
        const index = addedMods.indexOf(mod);
        addedMods.splice(index,1);
        console.log(addedMods);
        e.preventDefault();
    })
}

function addModeratorToSelect(mod) {
    mods.push(mod);
    const option = document.createElement('span');
    option.className = 'd-block p-1 ml-2 option';
    const addOption = createButton(ICONS.ADD);
    addOption.classList.add("mr-2");
    const optionText = document.createElement('span');
    if (mod.isOrganistation) {
        optionText.innerText = `${mod.firmName} (${mod.email})`;
    }else{
        optionText.innerText = `${mod.firstName} ${mod.lastName} (${mod.email})`;
    }
    option.appendChild(addOption);
    option.appendChild(optionText);
    modSelect.appendChild(option);
    
    addOption.addEventListener('click',() => {
        option.remove();
        addModeratorToList(mod); 
        addedMods.push(mod);
    });
}

function addModerator(mod) {
    if (mod.isMod) {
        addModeratorToList(mod);
    }else{
        addModeratorToSelect(mod);
    }
}

function getMods() {
    const projectIdElement = document.getElementById('ProjectId');
    let projectId = -1;
    if (projectIdElement) {
        projectId = projectIdElement.value;
    }
    const url = `/api/users/moderators/${projectId}`;
    
    axios.get(url)
        .then((r) => {
            
            r.data.forEach(addModerator);
            modInput = document.getElementById("mod-input");
            modInput.addEventListener('keyup',filterFunction);
        })
        .catch((err) => {
            console.log(err);
        });

    
}

function init() {
    // Initialize global values.
    // On edit page the phases are bootstrapped by Razor.
    // On create page there is only 1 template available.
    modSelect = document.getElementById("mod-dropdown");
    modTable = document.getElementById('mod-table-body');
    const templates = document.getElementsByClassName('phase-input-form');
    phaseNumber = templates.length + 1;
    [addPhaseInputFormTemplate] = templates;

    // Initialize datepickers
    const datePickerOptions = {
        format: 'dd/mm/yyyy',
        //startDate: new Date(),
        startDate: new Date()
    };

    $('[data-toggle="startdate-picker"]').datepicker(datePickerOptions);
    $('[data-toggle="enddate-picker"]').datepicker(datePickerOptions);
    
    const startDateElement = document.getElementById('StartDate');
    const endDateElement = document.getElementById('EndDate');
    let startDate;
    let endDate;
    if (startDateElement && endDateElement) {
        console.log(startDateElement);
        startDate = startDateElement.value;
        endDate = endDateElement.value;
        startDate = startDate.split(' ')[0];
        endDate = endDate.split(' ')[0];
        $('[data-toggle="startdate-picker"]').datepicker('setDate', startDate);
        $('[data-toggle="enddate-picker"]').datepicker('setDate', endDate);
    }
    
    

    // Initialize eventhandlers
    addEventHandlers();

    // Initialize validator
    validator.init();
    
    
    getMods();
}

window.onload = init;
