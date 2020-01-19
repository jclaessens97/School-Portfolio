/**
 * This module is used for both CREATING and EDITING of a platform.
 */
import axios from 'axios';
import * as AColorPicker from 'a-color-picker';
import * as validator from './validation';
import * as constants from "../constants";
import * as toastr from 'toastr';
import {createButton, getBase64, ICONS} from '../util';

let selectedPreviewId;
let selectedValueId;
let picker = null;
let logo = null;
let logoChanged = false;
let banner = null;
let bannerChanged = false;


let modSelect;
let modInput;
let modTable;
let mods = [];
let addedMods = [];

function generateFormData() {
    const formData = new FormData();
    formData.append('Name', document.getElementById('Name').value);
    formData.append('Tenant', document.getElementById('Tenant').value);
    formData.append('Description', document.getElementById('Description').value);
    formData.append('Logo', logo);
    formData.append('Banner', banner);
    formData.append('SocialBarColor', document.getElementById('social-bar-value').value);
    formData.append('NavbarColor', document.getElementById('navbar-value').value);
    formData.append('BannerColor', document.getElementById('banner-value').value);
    formData.append('ButtonColor', document.getElementById('button-value').value);
    formData.append('ButtonTextColor', document.getElementById('button-text-value').value);
    formData.append('TextColor', document.getElementById('text-value').value);
    formData.append('BodyColor', document.getElementById('body-value').value);

    const platformId = document.getElementById('PlatformId');
    if (platformId) {
        formData.append('PlatformId', platformId.value);
    }
    
    const PlatformReason =  document.getElementById('reasonText');
    if (PlatformReason){
        formData.append('PlatformReason', PlatformReason.value);
    }

    for (let i = 0; i < addedMods.length; i++) {
        formData.append(`Admins[${i}]`,addedMods[i].userName);
    }
    
    return formData;
}

async function createPlatform() {
    if (!validator.isValid()) return;

    const formData = generateFormData();

    // Post the formdata as multipart form
    try {
        console.log('createPlatform');

        await axios.post(
            '/api/platforms',
            formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            },
        );
        toastr.success('Platform succesvol aangemaakt');
        // alert('success');
    } catch (err) {
        console.error(err);
        toastr.error('Platform aanmaken niet gelukt, sorry.');
    }
}

async function Createplatformrequest(){
    if (!validator.isValid()) return;

    const formData = generateFormData();
    // Post the formdata as multipart form
    try {
        console.log('createPlatform');

        await axios.post(
            '/api/platformrequests',
            formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            },
        );
        toastr.success('Platform succesvol aangemaakt');
        // alert('success');
    } catch (err) {
        console.error(err);
        toastr.error('Platform aanmaken niet gelukt, sorry.');
    }
}

async function updatePlatform() {
    if (!validator.isValid(true)) return;

    const formData = generateFormData();
    formData.append('LogoChanged', logoChanged);
    formData.append('BannerChanged', bannerChanged);

    // Put the formdata as multipart form
    try {
        await axios.put(
            `/api/platforms/${formData.get('PlatformId')}`,
            formData, {
                headers: { 'Content-Type': 'multipart/form-data' },
            },
        );
        toastr.success('Platform succesvol aangepast');
        // alert('success');
    } catch (err) {
        console.error(err);
        toastr.error('Platform aanpassen niet gelukt, sorry.');
    }
}

// Color picker events
function updateColor(hexColor) {
    switch (selectedPreviewId) {
        case 'social-bar-preview':
        case 'navbar-preview':
        case 'banner-preview':
        case 'button-preview':
        case 'footer-preview':
            document.getElementById(selectedPreviewId).style.backgroundColor = hexColor;
            break;
        case 'text-preview':
        case 'button-text-preview':
            document.getElementById(selectedPreviewId).style.color = hexColor;
            break;
        case 'body-preview':
            document.getElementById('body-preview-color').style.backgroundColor = hexColor;
            document.getElementById('body-value').value = hexColor;
            break;
    }

    if (selectedPreviewId === 'button-text-preview') {
        document.getElementById('button-preview').style.color = hexColor;
        document.getElementById('body-preview').style.color = hexColor;
    }

    if (selectedPreviewId === 'button-preview') {
        document.getElementById('button-text-preview').style.backgroundColor = hexColor;
        document.getElementById('body-preview').style.backgroundColor = hexColor;
    }

    if (selectedPreviewId === 'social-bar-preview') {
        document.getElementById('footer-preview').style.backgroundColor = hexColor;
        document.getElementById('footer-value').value = hexColor;
    }

    if (selectedPreviewId === 'footer-preview') {
        document.getElementById('social-bar-preview').style.backgroundColor = hexColor;
        document.getElementById('social-bar-value').value = hexColor;
    }
}

function onColorChange(colorPicker) {
    // Update values of the colorBox and colorValue and HiddenTextfield.
    document.getElementById(selectedValueId).value = colorPicker.rgbhex;
    updateColor(colorPicker.rgbhex);    
}

function onColorChooserClick(event) {
    // If there is a previous picker in the DOM remove that first.
    if (picker !== null) {
        picker = null;
        const pickerElem = document.getElementsByClassName('a-color-picker')[0];
        pickerElem.parentNode.removeChild(pickerElem);
    }

    // Set current selected color preview/value.
    selectedPreviewId = event.target.id;
    selectedValueId = selectedPreviewId.replace('preview', 'value');
    const selectedValue = document.getElementById(selectedValueId);

    // Create picker starting from the selected color and bind onColorChange event handler.
    picker = AColorPicker.from('#colorpicker', {
        color: selectedValue.value,
        attachTo: '#color-preview',
    });

    picker.on('change', onColorChange);
}

function initColors() {
    const values = document.getElementsByClassName('color-value');
    Array.from(values).forEach((val) => {
        selectedValueId = val.id;
        selectedPreviewId = selectedValueId.replace('value', 'preview');

        updateColor(val.value);
    });

    const hiddenBannerValue = document.getElementById('hidden-banner-value');
    const bannerImageCheck = document.getElementById('bannerImageCheck');
    if (hiddenBannerValue && hiddenBannerValue.value && hiddenBannerValue.value.length > 0) {
        const imgPreview = document.getElementById('banner-preview');
        imgPreview.style.backgroundImage = `url(${hiddenBannerValue.value})`;
        imgPreview.style.backgroundSize = 'cover';
        bannerImageCheck.checked = true;
    }
}

function initPreview() {
    Array.from(document.getElementsByClassName('color-chooser'))
        .forEach(chooser => chooser.addEventListener('click', onColorChooserClick));

    // Hover effect footer and socialbar
    const socialBar = document.getElementById('social-bar-preview');
    const footer = document.getElementById('footer-preview');

    socialBar.addEventListener('mouseover', () => {
        footer.classList.add('animating-color-chooser');
    });
    socialBar.addEventListener('mouseleave', () => {
        footer.classList.remove('animating-color-chooser');
    });

    footer.addEventListener('mouseover', () => {
        socialBar.classList.add('animating-color-chooser');
    });
    footer.addEventListener('mouseleave', () => {
        socialBar.classList.remove('animating-color-chooser');
    });
}

function initImageUpload(type) {
    const labelBox = document.getElementById(`${type}-label-box`);
    const input = document.getElementById(`${type}-input`);
    const span1 = document.getElementById(`${type}-span1`);
    const span2 = document.getElementById(`${type}-span2`);
    const previewWrapper = document.getElementById(`${type}-preview-wrapper`);
    if (previewWrapper) {
        // only exists on create
        previewWrapper.style.display = 'none';
    }
    const imgPreview = document.getElementById(`${type}-preview`);

    // Prevent default behaviour
    function preventDefault(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    labelBox.addEventListener('drag', preventDefault);
    labelBox.addEventListener('dragstart', preventDefault);
    labelBox.addEventListener('dragend', preventDefault);
    labelBox.addEventListener('dragover', preventDefault);
    labelBox.addEventListener('dragenter', preventDefault);
    labelBox.addEventListener('dragleave', preventDefault);
    labelBox.addEventListener('drop', preventDefault);

    function dragEnter() {
        labelBox.classList.add('is-dragover')
    }

    labelBox.addEventListener('dragover', dragEnter);
    labelBox.addEventListener('dragEnter', dragEnter);

    function dragExit() {
        labelBox.classList.remove('is-dragover')
    }
    labelBox.addEventListener('dragleave', dragExit);
    labelBox.addEventListener('dragend', dragExit);

    // Media input events
    async function handleAddFile(file) {
        const fileType = file['type'];
        let validTypes;
        validTypes = ['image/gif', 'image/jpeg', 'image/png'];
        if (!validTypes.includes(fileType)) {
            span1.innerText = constants.TYPE_NOT_SUPPORTED;
            span2.innerText = '';
            return;
        }

        span1.innerText = file.name;
        span2.innerText = '';

        if (previewWrapper) {
            previewWrapper.style.display = 'block';
        }

        if (type === 'banner') {
            banner = file;
            bannerChanged = true;
            imgPreview.style.backgroundImage = `url(${await getBase64(file)})`;
            imgPreview.style.backgroundSize = 'cover';
        } else {
            logo = file;
            logoChanged = true;
            imgPreview.src = await getBase64(file);
        }
    }

    labelBox.addEventListener('drop', (e) => {
        dragExit();
        handleAddFile(e.dataTransfer.files[0]);
    });

    input.addEventListener('input', () => {
        handleAddFile(input.files[0]);
    });
}

function clearBannerImgPreview() {
    const preview = document.getElementById('banner-preview');
    preview.style.backgroundImage = null;
}

function addEventHandlers() {
    const tenantInput = document.getElementById('Tenant');
    tenantInput.addEventListener('focusout', async (event) => {
        await validator.checkIfTenantExists(event);
    });
    tenantInput.addEventListener('keydown', (event) => {
        // Don't allow spaces to be typed in
        if (event.keyCode === 32) {
            event.preventDefault();
        }
    });

    const nameInput = document.getElementById('Name')
    nameInput.addEventListener('input', (event) => {
        let input = event.target.value;
        input = input.replace(/[^A-Za-z]/g, '');
        tenantInput.value = input.toLowerCase();
    });

    const bannerLabelBox = document.getElementById('banner-label-box');
    bannerLabelBox.style.display = 'none';
    const bannerInput = document.getElementById('banner-input');
    const bannerImageCheck = document.getElementById('bannerImageCheck');
    bannerImageCheck.addEventListener('change', (event) => {
        if (event.target.checked) {
            bannerLabelBox.style.display = 'block';
        } else {
            bannerLabelBox.style.display = 'none';
            bannerInput.value = '';
            clearBannerImgPreview();
        }
    });

    const createPlatformButton = document.getElementById('createPlatform');
    if (createPlatformButton) {
        createPlatformButton.addEventListener('click', createPlatform);
    } else {
        // Add clickhandler to UpdateProject button (edit page)
        const updatePlatformButton = document.getElementById('updatePlatform');
        updatePlatformButton.addEventListener('click', updatePlatform);
    }
/*    const createPlatformRequest = document.getElementById('submit');
    createPlatformRequest.addEventListener('click',Createplatformrequest);*/
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
    const createPlatformButton = document.getElementById('createPlatform');
    if (createPlatformButton){
        addModeratorToSelect(mod);
    } else{
        if (mod.isMod) {
            addModeratorToList(mod);
        }else{
            addModeratorToSelect(mod);
        }
    }
    
}

function getAdmins() {
    const projectIdElement = document.getElementById('ProjectId');
    let projectId = -1;
    if (projectIdElement) {
        projectId = projectIdElement.value;
    }
    const url = `/api/users/Users`;

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
    modSelect = document.getElementById("mod-dropdown");
    modTable = document.getElementById('mod-table-body');
    initColors();
    initPreview();
    addEventHandlers();
    initImageUpload('logo');
    initImageUpload('banner');
    // validator.init();

    getAdmins();
}

window.onload = init;
