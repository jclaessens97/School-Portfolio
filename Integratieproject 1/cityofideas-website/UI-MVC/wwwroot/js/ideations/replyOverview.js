import axios from 'axios';
import {formatDate, getFormattedDate, timeAgo} from '../util';

let btnTrending;
let btnNew;
let btnBest;
let btnControversial;
let btnReported;

let selectedSort;
let pages;
let liPrev;
let liNext;

const PAGE_SIZE = 6;
let currentPage = 0;
let ideationReplyCount;
let pagesAmount;

function addPage(index) {
    const li = document.createElement('li');
    li.className = 'page-item';
    if (index === currentPage){
        li.classList.add('active');
    } 
    const a = document.createElement('a');
    li.appendChild(a);
    a.className = 'page-link';
    a.setAttribute('href','#');
    a.innerText = (+index)+(+1);
    pages.insertBefore(li,pages.lastChild);
    
    a.addEventListener('click', (e) => {
        e.preventDefault();
        loadReplies(selectedSort,index);
    });
}

function addPages() {
    while (pages.firstChild) {
        pages.removeChild(pages.firstChild);
    }
    
    pagesAmount = Math.ceil(ideationReplyCount/PAGE_SIZE);
    
    liPrev = document.createElement('li');
    liPrev.className = 'page-item';
    const aPrev = document.createElement('a');
    liPrev.appendChild(aPrev);
    aPrev.className = 'page-link';
    aPrev.setAttribute('href','#');
    aPrev.innerText = "Vorige";

    liNext = document.createElement('li');
    liNext.className = 'page-item';
    const aNext = document.createElement('a');
    liNext.appendChild(aNext);
    aNext.className = 'page-link';
    aNext.setAttribute('href','#');
    aNext.innerText = "Volgende";
    
    pages.appendChild(liPrev);
    pages.appendChild(liNext);

    if (currentPage >= pagesAmount-1){
        liNext.classList.add('disabled');
    }
    if(currentPage <= 0){
        liPrev.classList.add('disabled');
    }

    for (let i = 0; i < pagesAmount; i++) {
        addPage(i);
    } 
    
    aPrev.addEventListener('click', (e) => {
        e.preventDefault();
        loadReplies(selectedSort,((currentPage-1)>=0)?(currentPage-1):0);
    });
    
    aNext.addEventListener('click', (e) => {
        e.preventDefault();
        loadReplies(selectedSort,((currentPage+1)>=0)?(currentPage+1):currentPage);
    });
    
    
}

async function loadReplies(sortBy, page) {
    const spinner = document.getElementById('loader');
    spinner.classList.remove('d-none');
    hideNoReplies();
    clearContainer();
    const ideationId = document.getElementById('IdeationId').value;
    selectedSort = sortBy;

    try {
        const response = await axios.get(`/api/ideations/replies/${ideationId}/${page*PAGE_SIZE}/${PAGE_SIZE}?sortBy=${sortBy}`);
        spinner.classList.add('d-none');
        ideationReplyCount = response.data.count;
        currentPage = page;
        addPages();
        const replies = response.data.replies;
        if (replies === '' ||replies == null || replies.count < 1) {
            showNoReplies();
            return;
        }
        replies.forEach(createCard);
    } catch (err) {
        console.error(err);
        return [];
    }
}

function activateButton({ target }) {
    const sortButtons = document.getElementsByClassName('sortbutton');
    Array.from(sortButtons).forEach(b => b.className = 'sortbutton btn btn-primary'); // eslint-disable-line
    target.parentNode.className = 'sortbutton btn btn-primary active'; // eslint-disable-line
}

function clearContainer() {
    const container = document.getElementById('card-container');

    let child = container.lastElementChild;
    while (child) {
        container.removeChild(child);
        child = container.lastElementChild;
    }
}

function hideNoReplies(){
    const noReplies = document.getElementById('no-ideas');
    noReplies.classList.add('d-none');
}

function showNoReplies() {
    const noReplies = document.getElementById('no-ideas');
    noReplies.classList.remove('d-none');
}

function createCard(reply) {
    const wrapper = document.createElement('div');
    wrapper.className = 'col-md-4 mt-3';
    
    const card = document.createElement('div');
    card.className = 'card shadow';
    wrapper.appendChild(card);

    const cardBody = document.createElement('div');
    cardBody.className = 'card-body';
    card.appendChild(cardBody);

    const cardTitle = document.createElement('h5');
    cardTitle.className = 'card-title';
    cardTitle.innerText = `${reply.userDisplayName}`;
    cardBody.appendChild(cardTitle);

    if (selectedSort === "reported"){
        const cardSubtitle = document.createElement('p');
        cardSubtitle.className = 'card-subtitle mb-2 text-danger';
        cardSubtitle.innerHTML = `<i class="fas fa-flag text-danger"></i> ${reply.reportCount}`;
        cardBody.appendChild(cardSubtitle);
    } else{
        const cardSubtitle = document.createElement('p');
        cardSubtitle.className = 'card-subtitle mb-2 text-muted';
        cardSubtitle.innerHTML = `<i class="fas fa-heart"></i> ${reply.upVotes}`;
        cardBody.appendChild(cardSubtitle);
    }

    const cardText = document.createElement('p');
    cardText.className = 'card-text';
    cardText.innerHTML = reply.title;
    cardBody.appendChild(cardText);
    
    const cardLink = document.createElement('a');
    cardLink.innerText = 'Bekijken';
    cardLink.className = 'card-link';
    cardLink.href = `/ideation/view/${reply.ideationReplyId}`;
    cardBody.appendChild(cardLink);

    const cardFooter = document.createElement('div');
    cardFooter.className = 'card-footer';
    card.appendChild(cardFooter);
    
    const commentTime = new Date(reply.createdString);
    const timeAgoNum = timeAgo(commentTime);
    
    
    const date = document.createElement('small');
    date.className = 'text-muted';
    date.innerText = `${timeAgoNum}`;
    cardFooter.appendChild(date);

    const container = document.getElementById('card-container');
    container.appendChild(wrapper);
}

function removeActiveClass() {
    btnNew.classList.remove('active');
    btnControversial.classList.remove('active');
    btnTrending.classList.remove('active');
    btnBest.classList.remove('active');
    if (btnReported){
        btnReported.classList.remove('bg-danger');
        btnReported.classList.add('text-danger');
        btnReported.classList.remove('text-white');
    }
}

async function loadRecent(event) {
    event.preventDefault(event);
    removeActiveClass();
    btnNew.classList.add('active');
    //activateButton(event);

    await loadReplies('recent',0);
}

async function loadTop(event) {
    event.preventDefault(event);
    removeActiveClass();
    btnBest.classList.add('active');
    //activateButton(event);

    await loadReplies('top',0);
}

async function loadActivity(event) {
    event.preventDefault(event);
    removeActiveClass();
    btnTrending.classList.add('active');
    //activateButton(event);

   await loadReplies('trending',0);
}

async function loadControversial(event) {
    event.preventDefault(event);
    removeActiveClass();
    btnControversial.classList.add('active');
    //activateButton(event);

    await loadReplies('controversial',0);
}

async function loadReported(event) {
    event.preventDefault(event);
    removeActiveClass();
    btnReported.classList.add('bg-danger');
    btnReported.classList.remove('text-danger');
    btnReported.classList.add('text-white');
    //activateButton(event);
    await loadReplies('reported',0);
}

function addEventHandlers() {
    btnNew.addEventListener('click', loadRecent);
    btnBest.addEventListener('click', loadTop);
    btnTrending.addEventListener('click', loadActivity);
    btnControversial.addEventListener('click', loadControversial);
    if (btnReported){
        btnReported.addEventListener('click', loadReported);
    } 
    btnTrending.click();
}

function init() {
    btnBest = document.getElementById('best');
    btnControversial = document.getElementById('controversial');
    btnNew = document.getElementById('newest');
    btnTrending = document.getElementById('trending');
    btnReported = document.getElementById('reported');
    pages = document.getElementById('pages');
    addEventHandlers();
}

window.onload = init;
