import axios from 'axios';
import {
    ICONS, createButton, generateId, TypesString, TypesEnum, CreateModal
} from '../util';
import {Map} from "../map";
import * as constants from "../constants";
import $ from "jquery";

let projectId;

let ideationTable;
let formTable;
let ideationTableLinked;
let formTableLinked;

let ideationsWrapper;
let noIdeationsWrapper;
let ideationsLinkedWrapper;
let noideationsLinkedWrapper;

let formsWrapper;
let noFormsWrapper;
let formLinkedWrapper;
let noFormLinkedWrapper;

let modalElement;

let Ideations;
let Forms;


let ideationsLoaded;
let formsLoaded;

function getElements() {
    projectId = document.getElementById('project-id').value;
    ideationTable = document.getElementById('ideation-table');
    formTable = document.getElementById('form-table');
    ideationTableLinked = document.getElementById('ideation-table-linked');
    formTableLinked = document.getElementById('form-table-linked');

    ideationsWrapper = document.getElementById('ideations');
    noIdeationsWrapper = document.getElementById('no-ideations');
    ideationsLinkedWrapper = document.getElementById('ideations-linked');
    noideationsLinkedWrapper = document.getElementById('no-ideations-linked');

    formsWrapper = document.getElementById('forms');
    noFormsWrapper = document.getElementById('no-forms');
    formLinkedWrapper = document.getElementById('forms-linked');
    noFormLinkedWrapper = document.getElementById('no-forms-linked');

    modalElement = document.getElementById('popup');
}

function checkForEmptyTablesIdeations() {
    if (ideationTable.childElementCount < 1) {
        ideationsWrapper.classList.remove('d-block');
        ideationsWrapper.classList.add('d-none');
        noIdeationsWrapper.classList.remove('d-none');
        noIdeationsWrapper.classList.add('d-block');
    } else {
        ideationsWrapper.classList.remove('d-none');
        ideationsWrapper.classList.add('d-block');
        noIdeationsWrapper.classList.remove('d-block');
        noIdeationsWrapper.classList.add('d-none');
    }

    if (ideationTableLinked.childElementCount < 1) {
        ideationsLinkedWrapper.classList.remove('d-block');
        ideationsLinkedWrapper.classList.add('d-none');
        noideationsLinkedWrapper.classList.remove('d-none');
        noideationsLinkedWrapper.classList.add('d-block');
    } else {
        ideationsLinkedWrapper.classList.remove('d-none');
        ideationsLinkedWrapper.classList.add('d-block');
        noideationsLinkedWrapper.classList.remove('d-block');
        noideationsLinkedWrapper.classList.add('d-none');
    }
}

function checkForEmptyTablesForms() {
    if (formTable.childElementCount < 1) {
        formsWrapper.classList.remove('d-block');
        formsWrapper.classList.add('d-none');
        noFormsWrapper.classList.remove('d-none');
        noFormsWrapper.classList.add('d-block');
    } else {
        formsWrapper.classList.remove('d-none');
        formsWrapper.classList.add('d-block');
        noFormsWrapper.classList.remove('d-block');
        noFormsWrapper.classList.add('d-none');
    }

    if (formTableLinked.childElementCount < 1) {
        formLinkedWrapper.classList.remove('d-block');
        formLinkedWrapper.classList.add('d-none');
        noFormLinkedWrapper.classList.remove('d-none');
        noFormLinkedWrapper.classList.add('d-block');
    } else {
        formLinkedWrapper.classList.remove('d-none');
        formLinkedWrapper.classList.add('d-block');
        noFormLinkedWrapper.classList.remove('d-block');
        noFormLinkedWrapper.classList.add('d-none');
    }
}

function saveIotLink(element) {
    const url = '/api/iot';

    const isForm = (element.centralQuestion == null);

    const data = {
        IsForm: isForm,
        location: {
            latitude: element.location.latitude,
            longitude: element.location.longitude,
            zoomLevel: element.location.zoomLevel
        }
    };
    if (isForm) {
        data.formId = element.formId;
    } else {
        data.ideationId = element.ideationId;
        console.log(element.ideationId);
        // data.formId = 0;
    }

    const dataJson = JSON.stringify(data);

    const headers = {
        'Content-Type': 'application/json',
    };

    axios.post(url, dataJson, {headers: headers})
        .then((r) => {
            console.log(r.data);
            location.reload();
        })
        .catch((e) => {
            console.log('Something went wrong getting the saving the ideation:');
            console.log(e);
        });

}

function editIotLink(element) {
    const url = '/api/iot';

    const isForm = (element.centralQuestion == null);

    const data = {
        IotLinkId: element.iotLink.iotLinkId,
        IsForm: isForm,
        location: {
            latitude: element.location.latitude,
            longitude: element.location.longitude,
            zoomLevel: element.location.zoomLevel
        }
    };
    if (isForm) {
        data.formId = element.formId;
    } else {
        data.ideationId = element.ideationId;
        console.log(element.ideationId);
        // data.formId = 0;
    }

    const dataJson = JSON.stringify(data);

    const headers = {
        'Content-Type': 'application/json',
    };

    axios.put(url, dataJson, {headers: headers})
        .then((r) => {
            console.log(r.data);
            location.reload();
        })
        .catch((e) => {
            console.log('Something went wrong getting the changing the iot link:');
            console.log(e);
        });
}

function confirmRemoveLink(element) {
    const url = `/api/iot/${element.iotLink.iotLinkId}`;

    axios.delete(url)
        .then(() => {
            location.reload();
        })
        .catch((e => {
            console.log('Er ging iets mis bij het verwijderen van de iot link');
            console.log(e);
        }))
}

function showMap(location, mapElement, element, SearchBox) {
    let searchOrigin;
    console.log(mapElement);
    Map.loadGoogleMapsApi().then((googleMaps) => {
        const map = new googleMaps.Map(mapElement, {
            center: {lat: location.latitude, lng: location.longitude},
            zoom: location.zoom,
            zoomControl: true,
            mapTypeControl: true,
            scaleControl: false,
            streetViewControl: false,
            rotateControl: false,
            fullscreenControl: false
        });

        function updateLocation() {
            const latitude = map.center.lat();
            const longitude = map.center.lng();
            const zoomLevel = map.zoom;

            element.location.latitude = latitude;
            element.location.longitude = longitude;
            element.location.zoomLevel = zoomLevel;
        }

        updateLocation();

        map.addListener('center_changed', () => {
            updateLocation();
        });
        map.addListener('zoom_changed', () => {
            updateLocation();
        });


        let animate = false;
        const domMarker = document.createElement('img');
        domMarker.setAttribute('src', '../../dist/markerIcon.svg');
        domMarker.className = 'centerMarker marker';
        mapElement.appendChild(domMarker);

        const domXMarker = document.createElement('img');
        domXMarker.setAttribute('src', '../../dist/crossIcon.svg');
        domXMarker.className = 'xMarker marker';
        mapElement.appendChild(domXMarker);
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
        const searchBox = new googleMaps.places.SearchBox(SearchBox);
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(SearchBox);

        SearchBox.addEventListener('keydown', (event) => {
            if (event.key === 'Enter') {
                const location = SearchBox.value;
                const geocoder = new googleMaps.Geocoder();
                geocoder.geocode({address: location}, (data) => {
                    const lat = data[0].geometry.location.lat();
                    const lng = data[0].geometry.location.lng();
                    searchOrigin = new googleMaps.LatLng(lat, lng);
                    map.setCenter(searchOrigin);
                });
            }
        })
    });
}

function createLink(element) {
    const modal = CreateModal(modalElement, false);
    modal.title.innerText = 'Nieuwe Idea Of Things koppeling maken';

    const title = document.createElement('p');
    if (element.centralQuestion) {
        title.innerText = `Centrale vraag: ${element.centralQuestion}`;
    } else {
        title.innerText = `Titel: ${element.title}`;
    }


    modal.body.appendChild(title);

    const locationDiv = document.createElement('div');
    locationDiv.className = 'w-100';
    const locationText = document.createElement('p');
    locationText.innerText = "Waar zal de opstelling worden neergezet?";
    const locationMap = document.createElement('div');
    locationMap.className = 'map2';

    locationDiv.appendChild(locationText);
    locationDiv.appendChild(locationMap);
    modal.body.appendChild(locationDiv);

    //Search box for map
    const SearchBox = document.createElement('input');
    SearchBox.id = 'pac-input';
    SearchBox.className = 'controls';
    SearchBox.setAttribute('type', 'text');
    SearchBox.setAttribute('placeholder', constants.MAP_SERACH);

    const Location = {
        latitude: undefined,
        longitude: undefined,
        zoomLevel: undefined,
        //allowZoom : true,
    };
    element.location = Location;

    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition((position) => {
            const location = {
                latitude: position.coords.latitude,
                longitude: position.coords.longitude,
                zoom: 16
            };
            showMap(location, locationMap, element, SearchBox);
        });
    } else {
        x.innerHTML = 'Geolocation is not supported by this browser.';
    }


    const CreateButton = document.createElement('button');
    CreateButton.innerText = constants.CONFIRM;
    CreateButton.className = 'btn-default';
    modal.body.appendChild(CreateButton);

    CreateButton.addEventListener('click', (e) => {
        e.preventDefault();
        saveIotLink(element);
    });

}

function editLink(element) {
    const modal = CreateModal(modalElement, false);
    modal.title.innerText = 'Idea of Things koppeling bewerken';

    const title = document.createElement('p');
    if (element.centralQuestion) {
        title.innerText = `Centrale vraag: ${element.centralQuestion}`;
    } else {
        title.innerText = `Titel: ${element.title}`;
    }
    const identification = document.createElement('p');
    identification.innerHTML = `Link identificatie nr: <strong>${element.iotLink.iotLinkId}</strong>`;

    modal.body.appendChild(title);
    modal.body.appendChild(identification);

    const locationDiv = document.createElement('div');
    locationDiv.className = 'w-100';
    const locationText = document.createElement('p');
    locationText.innerText = "Waar zal de opstelling worden neergezet?";
    const locationMap = document.createElement('div');
    locationMap.className = 'map2';

    locationDiv.appendChild(locationText);
    locationDiv.appendChild(locationMap);
    modal.body.appendChild(locationDiv);

    //Search box for map
    const SearchBox = document.createElement('input');
    SearchBox.id = 'pac-input';
    SearchBox.className = 'controls';
    SearchBox.setAttribute('type', 'text');
    SearchBox.setAttribute('placeholder', constants.MAP_SERACH);

    const Location = {
        latitude: undefined,
        longitude: undefined,
        zoomLevel: undefined,
        //allowZoom : true,
    };
    element.location = Location;
    console.log(element.iotLink.location);
    //showMap(element.iotLink.location,locationMap,element,SearchBox);
    const location = {
        latitude: element.iotLink.location.latitude,
        longitude: element.iotLink.location.longitude,
        zoom: element.iotLink.location.zoomLevel
    };
    showMap(location,locationMap,element,SearchBox);


    const CreateButton = document.createElement('button');
    CreateButton.innerText = constants.CONFIRM;
    CreateButton.className = 'btn-default';
    modal.body.appendChild(CreateButton);

    CreateButton.addEventListener('click', (e) => {
        e.preventDefault();
        editIotLink(element);
    });
}

function removeLink(element) {
    const modal = CreateModal(modalElement);
    modal.title.innerText = 'Ben je zeker dat je de IoT link wil verwijderen?';

    const title = document.createElement('p');
    if (element.centralQuestion) {
        title.innerText = `Centrale vraag: ${element.centralQuestion}`;
    } else {
        title.innerText = `Titel: ${element.title}`;
    }
    const identification = document.createElement('p');
    identification.innerHTML = `Link identificatie nr: <strong>${element.iotLink.iotLinkId}</strong>`;

    modal.body.appendChild(title);
    modal.body.appendChild(identification);

    const CreateButton = document.createElement('button');
    CreateButton.innerText = constants.DELETE;
    CreateButton.className = 'btn-default';
    modal.body.appendChild(CreateButton);

    CreateButton.addEventListener('click', (e) => {
        e.preventDefault();
        confirmRemoveLink(element);
    });
    
}

function addLinked(element) {
    const tr = document.createElement('tr');

    const tdLinkId = document.createElement('td');

    tdLinkId.innerText = element.iotLink.iotLinkId;
    tdLinkId.className = 'text-danger font-weight-bold';

    const tdId = document.createElement('td');
    const tdTitle = document.createElement('td');

    const tdLink = document.createElement('td');
    const aLink = document.createElement('a');
    aLink.innerText = 'Link beheren';
    aLink.setAttribute('href', '#');
    tdLink.appendChild(aLink);

    const tdLinkRemove = document.createElement('td');
    const aLinkRemove = document.createElement('a');
    aLinkRemove.innerText = 'Link verwijderen';
    aLinkRemove.setAttribute('href', '#');
    tdLinkRemove.appendChild(aLinkRemove);

    tr.appendChild(tdLinkId);
    tr.appendChild(tdId);
    tr.appendChild(tdTitle);
    tr.appendChild(tdLink);
    tr.appendChild(tdLinkRemove);

    if (element.isForm) {
        tdId.innerText = element.formId;
        tdTitle.innerText = element.title;
        formTableLinked.append(tr);

    } else {
        tdId.innerText = element.ideationId;
        tdTitle.innerText = element.centralQuestion;
        ideationTableLinked.append(tr);
    }
    
    aLinkRemove.addEventListener('click',(e) => {
        e.preventDefault();
        removeLink(element);
    });

    aLink.addEventListener('click', (e) => {
        e.preventDefault();
        editLink(element);
    });


}

function addUnlinked(element) {
    const tr = document.createElement('tr');
    const tdId = document.createElement('td');
    tdId.innerText = element.formId;
    const tdTitle = document.createElement('td');
    tdTitle.innerText = element.title;
    const tdLink = document.createElement('td');
    const aLink = document.createElement('a');
    aLink.innerText = 'Link maken';
    aLink.setAttribute('href', '#');
    tdLink.appendChild(aLink);

    tr.appendChild(tdId);
    tr.appendChild(tdTitle);
    tr.appendChild(tdLink);

    if (element.isForm) {
        tdId.innerText = element.formId;
        tdTitle.innerText = element.title;
        formTable.append(tr);
    } else {
        tdId.innerText = element.ideationId;
        tdTitle.innerText = element.centralQuestion;
        ideationTable.append(tr);
    }

    aLink.addEventListener('click', (e) => {
        e.preventDefault();
        createLink(element);
    });
}

function showForms() {
    Forms.forEach((element) => {
        element.isForm = true;
        if (element.iotLink == null) {
            addUnlinked(element);
        } else {
            addLinked(element);
        }
    });
    checkForEmptyTablesForms()

}

function showIdeations() {
    Ideations.forEach((element) => {
        element.isForm = false;
        if (element.iotLink == null) {
            addUnlinked(element);
        } else {
            addLinked(element);
        }
    });
    checkForEmptyTablesIdeations();
}

function showElements() {
    showIdeations();
    showForms();
}

function loadIdeations() {
    const url = `/api/ideations/all/admin/${projectId}`;

    axios.get(url)
        .then((r) => {
            console.log(r.data);
            Ideations = r.data;
            ideationsLoaded = true;
            if (ideationsLoaded && formsLoaded) {
                showElements();
            }
        })
        .catch((e) => {
            console.log('Something went wrong getting the ideations:');
            console.log(e);
        })
}

function loadForms() {
    const url = `/api/forms/all/statement/${projectId}`;

    axios.get(url)
        .then((r) => {
            console.log(r.data);
            Forms = r.data;
            formsLoaded = true;
            if (ideationsLoaded && formsLoaded) {
                showElements();
            }
        })
        .catch((e) => {
            console.log('Something went wrong getting the forms:');
            console.log(e)
        });
}

function loadData() {
    loadIdeations();
    loadForms();
}

function init() {
    console.log('test');
    getElements();
    loadData();
}


window.onload = init;