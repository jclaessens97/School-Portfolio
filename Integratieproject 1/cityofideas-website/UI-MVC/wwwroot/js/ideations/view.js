import axios from 'axios';
import * as signalR from '@aspnet/signalr';
import {CreateModal, ICONS, createButton, sleep, getCookie, timeAgo} from '../util';
import * as comments from './comments';
import Packery from 'packery';
import * as constants from '../constants';
import IotLinkType from '../IotLinkType';

let connection;

let domAgreeButton;
let domDisagreeButton;
let domVotesWrapper;
let domVotesDisplay;
let domVotesProgress;

let domReportButton;

let domReviewWrapper;

let domModal;

let HasReported = false;
let HasVoted = false;
let UpVotes = 0;
let DownVotes = 0;
let domUpVotes;
let domDownVotes;

let IdeationReplyId;
let UserName;
let UserIsMod;

async function Vote(agree = true) {
    if (HasVoted){
        return; // No cheating hombre 
    }
    setVoted();
    let url;
    if (agree) {
        url = `/api/ideations/reply/vote/up/${IdeationReplyId}`;
    } else {
        url = `/api/ideations/reply/vote/down/${IdeationReplyId}`;
    }

    try {
        await axios.post(url).then(() => {
            if (agree){
                console.log('tst');
                connection.invoke('SendUpvote', IotLinkType.Ideation, IdeationReplyId);
                UpVotes++;
            } else {
                connection.invoke('SendDownvote', IotLinkType.Ideation, IdeationReplyId);
                DownVotes++;
            }
            UpdateVoteCount();
        })
            .catch(()=>{
                console.log('Something went wrong voting...')
            });
        
    } catch (err) {
        console.log('Er ging iets mis bij het voten :(');
        console.log(err);
    }
}

function setVoted(){
    document.cookie = `voted${IdeationReplyId}=true`;
    domVotesWrapper.innerHTML  = '';
    const votedText = document.createElement('p');
    votedText.innerText = constants.VOTED;
    votedText.className = 'italic';
    domVotesWrapper.appendChild(votedText);
    
}

function initVotes() {
    domAgreeButton.innerText = constants.VOTE_AGREE;
    domDisagreeButton.innerText = constants.VOTE_DISAGREE;
    domAgreeButton.addEventListener('click', () => {
        Vote();
        
    });
    domDisagreeButton.addEventListener('click', () => {
        Vote(false);
    });
    if (HasVoted){
        setVoted();
    } else{
        const voted = getCookie(`voted${IdeationReplyId}`);
        if (voted){
            setVoted();
        } 
    }
}

function UpdateVoteCount() {
    let totalVotes = +UpVotes + +DownVotes;
    let displayValueScaled = (totalVotes > 0) ? +UpVotes / +totalVotes * 100 : 0; // Coalesce if total is 0
    domVotesProgress.style.width = displayValueScaled + '%';
    domUpVotes.innerText = UpVotes;
    domDownVotes.innerText = DownVotes;
}

async function initializeWebSockets() {
    connection = new signalR.HubConnectionBuilder().withUrl('/votehub').build();

    connection.on('ReceiveUpvote', () => {
        UpVotes++;
        UpdateVoteCount();
    });

    connection.on('ReceiveDownvote', () => {
        DownVotes++;
        UpdateVoteCount();
    });

    await connection.start();
    //console.log(IdeationReplyId);
    connection.invoke('JoinPage', IdeationReplyId, IotLinkType.Ideation);
   // connection.invoke('JoinPage', `ideationReply - ${IdeationReplyId}`);
}

function breakdownWebsockets() {
    connection.invoke('LeavePage', IdeationReplyId,IotLinkType.Ideation);
}


function CheckReport(){
    if (HasReported){
        domReportButton.innerText = constants.REPORTED_IDEATION;
    } else{
        domReportButton.innerText = constants.REPORT_IDEATION;
    }
}

function ReportPost() {
    let url = `/api/ideations/reply/report`;
    if (HasReported) {
        url += "/cancel"
    }
    url += `/${IdeationReplyId}`;
    
    axios.post(url)
        .then(() =>{
            if (HasReported){
                domReportButton.innerText = constants.REPORT_IDEATION;
            } else{
                domReportButton.innerText = constants.REPORTED_IDEATION;
            }
            HasReported = !HasReported;
            CheckReport();
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het raporteren van een comment');
            console.log(err);
        })
     
    
}

function setReviewed(approved){
    domReviewWrapper.innerHTML = '';
    const text = approved ? constants.IDEATION_APPROVED : constants.IDEATION_DISAPPROVED;
    const approveText = document.createElement('p');
    approveText.innerText = text;
    if (approved){
        approveText.className = 'text-info';
    } else {
        approveText.className = 'text-danger';
    }
    approveText.classList.add('font-weight-bold');
    approveText.classList.add('mb-0');
    domReviewWrapper.appendChild(approveText);
    
    const domSwitchApprovedButton = document.createElement('button');
    
    domReviewWrapper.appendChild(domSwitchApprovedButton);
    if (approved){
        domSwitchApprovedButton.innerText = constants.IDEATION_DISAPPROVE;
        domSwitchApprovedButton.className = 'btn-link';
        domSwitchApprovedButton.addEventListener('click',disapprovePost)
    } else {
        domSwitchApprovedButton.innerText = constants.IDEATION_APPROVE2;
        domSwitchApprovedButton.className = 'btn-link';
        domSwitchApprovedButton.addEventListener('click',approvePost)
    }
    
    
}

function approvePost() {
    const url = `/api/ideations/reply/approve/${IdeationReplyId}`;

    axios.post(url)
        .then(() =>{
            setReviewed(true);
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het goedkeuren van een post');
            console.log(err);
        })
}

function disapprovePost() {
    const url = `/api/ideations/reply/disapprove/${IdeationReplyId}`;

    axios.post(url)
        .then(() =>{
            setReviewed(false);
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het afkeuren van een post');
            console.log(err);
        })
}

function initReviewPost() {
    const ReviewedByMod = document.getElementById('post-reviewed').value;
    if (!ReviewedByMod){
        const approveBtn = document.createElement('button');
        approveBtn.innerText = constants.IDEATION_APPROVE;
        approveBtn.className = "btn-default mb-3 mr-2";
        domReviewWrapper.appendChild(approveBtn);
        
        
        const disapproveBtn = document.createElement('button');
        disapproveBtn.innerText = constants.IDEATION_DISAPPROVE;
        disapproveBtn.className = "btn-default mb-3";
        domReviewWrapper.appendChild(disapproveBtn);
        
        approveBtn.addEventListener('click',approvePost);
        disapproveBtn.addEventListener('click',disapprovePost);
    }else{
        const PostIsHidden = document.getElementById('post-hidden').value;
        setReviewed(!PostIsHidden);
    }
}

async function init() {
    domVotesWrapper = document.getElementById('vote-wrapper');
    domAgreeButton = document.getElementById('button-like');
    domDisagreeButton = document.getElementById('button-dislike');
    domVotesDisplay = document.getElementById('votes-display');
    domVotesProgress = document.getElementById('progress');
    domUpVotes = document.getElementById('votes-up');
    domDownVotes = document.getElementById('votes-down');
    domModal = document.getElementById('popup');
    domReportButton = document.getElementById('report-button');
    domReviewWrapper = document.getElementById('review-post');
    IdeationReplyId = document.getElementById('ideation-reply-id').value;
    UpVotes = document.getElementById('ideation-upvote-count').value;
    DownVotes = document.getElementById('ideation-downvote-count').value;
    HasVoted = document.getElementById('ideation-user-voted').value;
    UserIsMod = document.getElementById('user-is-mod').value;
    HasReported = document.getElementById('user-has-reported').value;
    
    const dateElement = document.getElementById("date-created");
    const date = dateElement.innerText;
    const timeAgoText = timeAgo(date);
    dateElement.innerText = timeAgoText;
    
    
    const domUserName = document.getElementById('user-name');
    if (domUserName != null) {
        UserName = domUserName.value;
    }
    
    //
    CheckReport();
    
    domReportButton.addEventListener('click',(e) => {
       ReportPost(); 
       e.preventDefault();
    });
    
    
    //Grid layout
    const grid = document.getElementById('grid');
    const pckry = new Packery(grid, {
        // options
        itemSelector: '.grid-item',
        gutter: 10
    });

    //Comments
    const domCommentSection = document.getElementById('comment-section');
    const domNewComment = document.getElementById('new-comment');

    const details = {
        UserName,
        UserIsMod,
        IdeationReplyId,
    };

    comments.initCommentSection(domCommentSection, domNewComment, domModal, details);
    
    if (UserIsMod) {
        initReviewPost();
        const domReportedCommentSection = document.getElementById('reported-comment-section');
        comments.initShowReportedComments(domReportedCommentSection);
    }

    initVotes();
    initializeWebSockets();
    UpdateVoteCount();
    // Failsafe = Make sure all elements are loaded and reset layout 
    await sleep(500);
    pckry.layout();
    await sleep(2000);
    pckry.layout();
}

window.onload = init;
window.onunload = breakdownWebsockets;

