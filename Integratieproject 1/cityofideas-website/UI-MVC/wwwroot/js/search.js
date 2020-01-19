import axios from 'axios';

const searchInput = document.getElementById('searchInput');
const searchButton = document.getElementById('searchButton');
const spinner = document.getElementById('spinner');
const results = document.getElementById('search-results');

function removeChilds(parent) {
    var child = parent.lastElementChild;
    while (child) {
        parent.removeChild(child);
        child = parent.lastElementChild;
    } 
}

function updateUI(platforms, projects, ideations) {
    const platformContainer = document.querySelector('#platform-results ul');
    removeChilds(platformContainer);
    platforms.forEach((p) => {
        const a = document.createElement('a');
        a.href = p.tenant;
        a.innerText = p.name;

        const li = document.createElement('li');
        li.appendChild(a);

        platformContainer.appendChild(li);
    });

    if (platforms.length === 0) {
        const li = document.createElement('li');
        li.innerHTML = '<i>Geen platformen gevonden voor deze zoektermen.</i>';
        platformContainer.appendChild(li)
    }

    const projectContainer = document.querySelector('#project-results ul');
    removeChilds(projectContainer);
    projects.forEach((p) => {
        const a = document.createElement('a');
        a.href = `${p.platform.tenant}/project/details/${p.projectId}`;
        a.innerText = p.title;

        const li = document.createElement('li');
        li.appendChild(a);

        projectContainer.appendChild(li);
    });

    if (projects.length === 0) {
        const li = document.createElement('li');
        li.innerHTML = '<i>Geen projecten gevonden voor deze zoektermen.<i>';
        projectContainer.appendChild(li)
    }

    const ideationContainer = document.querySelector('#ideation-results ul');
    removeChilds(ideationContainer);
    ideations.forEach((i) => {
        const a = document.createElement('a');
        a.href = `${i.project.platform.tenant}/ideation/view/${i.ideationId}`;
        a.innerText = i.centralQuestion;

        const li = document.createElement('li');
        li.appendChild(a);

        ideationContainer.appendChild(li);
    });

    if (ideations.length === 0) {
        const li = document.createElement('li');
        li.innerHTML = '<i>Geen ideations gevonden voor deze zoektermen.</i>';
        ideationContainer.appendChild(li)
    }
}

async function launchSearchQuery(searchString) {
    if (!searchString || searchString.length < 3) {
        spinner.style.display = 'none';
        results.style.display = 'block';
        updateUI([], [], []);
        return;
    }

    const promises = [
        axios.get(`/api/search/platforms?query=${searchString}`),
        axios.get(`/api/search/projects?query=${searchString}`),
        axios.get(`/api/search/ideations?query=${searchString}`),
    ];

    try {
        const [ 
            platformResponses,
            projectResponses,
            ideationResponses,
        ] = await Promise.all(promises);

        const platforms = platformResponses.data;
        const projects = projectResponses.data;
        const ideations = ideationResponses.data;

        updateUI(platforms, projects, ideations);
        spinner.style.display = 'none';
        results.style.display = 'block';
    } catch (err) {
        console.error(err);
    }
}

function addEventHandlers() {
    let debounceTimer = null;

    searchInput.addEventListener('keyup', (event) => {
        spinner.style.display = 'block';
        results.style.display = 'none';
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            launchSearchQuery(event.target.value);
        }, 300);
    });

    searchInput.addEventListener('keydown', () => {
        clearTimeout(debounceTimer);
    });

    searchButton.addEventListener('click', () => {
        launchSearchQuery(searchInput.target.value);
    });
}

function init() {
    document.getElementById('searchInputNav').hidden = true;
    document.getElementById('searchButtonNav').hidden = true;

    addEventHandlers();
}

window.onload = init;