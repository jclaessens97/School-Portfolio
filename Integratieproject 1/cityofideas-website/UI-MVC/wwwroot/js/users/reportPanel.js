import axios from 'axios';
import {CreateModal, timeAgo} from '../util';
import * as constants from '../constants';

let domCommentsWrapper;
let domComments;
let domModal;
let domNoComments;
let domLoadMore;

let id;
let commentsLoaded = 0;


function checkForNoReportedComments(){
    const noComments = domNoComments;
    if (commentsLoaded === 0){
        domCommentsWrapper.appendChild(noComments);
    } else {
        if (noComments.parentNode != null) noComments.parentNode.removeChild(noComments);
    }
}

function AllowPost(comment) {
    const url = `/api/ideations/comments/allow/${comment.commentId}`;

    axios.post(url)
        .then(() => {
            console.log('Comment allowed');
            domModal.style.display = 'none';
            comment.domElement.innerText = constants.COMMENT_ALLOWED;
        })
        .catch((err) => {
            console.log('Er liep iets mis:');
            console.log(err);
        });
}

function ConfirmHidePost(comment, banDuration) {
    // Ban user (if asked)
    if (banDuration !== -2) {
        const url = `/api/users/lockuser?username=${comment.userName}&duration=${banDuration}`;
        axios.post(url)
            .then(() => {
                console.log('User banned');
            })
            .catch((err) => {
                console.log('Er liep iets mis:');
                console.log(err);
            });
    }

    const url = `/api/ideations/comments/hide/${comment.commentId}`;

    axios.post(url)
        .then(() => {
            comment.domElement.innerText = constants.COMMENT_HIDDEN;
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het posten van een comment :(');
            console.log(err);
        });
}

function HidePost(comment) {
    // Hide post confirmation screen
    const modal = CreateModal(domModal);
    modal.title.innerText = constants.HIDE_POST;

    // Show text of comment again
    const domtext = document.createElement('p');
    domtext.className = 'italic';
    domtext.innerText = comment.commentText;
    modal.body.appendChild(domtext);

    // Checkbox for ban user
    const domBanLabel = document.createElement('label');
    domBanLabel.setAttribute('for', 'banUser');
    domBanLabel.className = 'w-100';

    const domBanSelect = document.createElement('input');
    domBanSelect.setAttribute('id', 'banUser');
    domBanSelect.setAttribute('type', 'checkbox');
    domBanLabel.appendChild(domBanSelect);

    const domBanSpan = document.createElement('span');
    domBanSpan.innerText = constants.BAN_USER;
    domBanLabel.appendChild(domBanSpan);

    // radio button for permanent ban
    const domPermanentLabel = document.createElement('label');
    domPermanentLabel.setAttribute('for', 'banPermanent');
    domPermanentLabel.className = 'w-100';

    const domPermanentSelect = document.createElement('input');
    domPermanentSelect.setAttribute('id', 'banPermanent');
    domPermanentSelect.setAttribute('name', 'banDuration');
    domPermanentSelect.setAttribute('type', 'radio');
    domPermanentSelect.checked = true;
    domPermanentLabel.appendChild(domPermanentSelect);

    const domPermanentSpan = document.createElement('span');
    domPermanentSpan.innerText = 'Voor altijd?';
    domPermanentLabel.appendChild(domPermanentSpan);

    // radio button for temporary ban
    const domTemporaryLabel = document.createElement('label');
    domTemporaryLabel.setAttribute('for', 'banTemporary');
    domTemporaryLabel.className = 'w-100';

    const domTemporarySelect = document.createElement('input');
    domTemporarySelect.setAttribute('id', 'banTemporary');
    domTemporarySelect.setAttribute('name', 'banDuration');
    domTemporarySelect.setAttribute('type', 'radio');
    domTemporaryLabel.appendChild(domTemporarySelect);

    const domTemporarySpan = document.createElement('span');
    domTemporarySpan.innerText = 'Tijdelijk';
    domTemporaryLabel.appendChild(domTemporarySpan);

    modal.body.appendChild(domBanLabel);
    modal.body.appendChild(domPermanentLabel);
    modal.body.appendChild(domTemporaryLabel);


    // Input for ban duration
    const domBanDuration = document.createElement('div');

    // Input for minutes
    const domBanDurationMinutesInput = document.createElement('input');
    domBanDurationMinutesInput.className = 'duration-input form-control';
    domBanDurationMinutesInput.id = 'minutes';
    domBanDurationMinutesInput.setAttribute('type', 'number');
    domBanDurationMinutesInput.disabled = true;

    const domBanDurationMinutesLabel = document.createElement('label');
    domBanDurationMinutesLabel.innerText = 'Minuten';
    domBanDurationMinutesLabel.setAttribute('for', 'minutes');

    // Input for hours
    const domBanDurationHoursInput = document.createElement('input');
    domBanDurationHoursInput.className = 'duration-input form-control';
    domBanDurationHoursInput.id = 'minutes';
    domBanDurationHoursInput.setAttribute('type', 'number');
    domBanDurationHoursInput.disabled = true;

    const domBanDurationHoursLabel = document.createElement('label');
    domBanDurationHoursLabel.innerText = 'Uren';
    domBanDurationHoursLabel.setAttribute('for', 'minutes');

    // Set input disbled/enabled
    domPermanentSelect.addEventListener('input', () => {
        domBanDurationMinutesInput.disabled = true;
        domBanDurationHoursInput.disabled = true;
        domBanDurationMinutesInput.value = '';
        domBanDurationHoursInput.value = '';
    });

    domTemporarySelect.addEventListener('input', () => {
        domBanDurationMinutesInput.disabled = false;
        domBanDurationHoursInput.disabled = false;
    });

    function setSelectStates() {
        if (!domBanSelect.checked) {
            domTemporarySelect.disabled = true;
            domPermanentSelect.disabled = true;
            domBanDurationMinutesInput.disabled = true;
            domBanDurationHoursInput.disabled = true;
        } else {
            domTemporarySelect.disabled = false;
            domPermanentSelect.disabled = false;
            if (domTemporarySelect.checked === true) {
                domBanDurationMinutesInput.disabled = false;
                domBanDurationHoursInput.disabled = false;
            }
        }
    }

    domBanSelect.addEventListener('input', () => {
        setSelectStates();
    });

    domBanDuration.appendChild(domBanDurationHoursInput);
    domBanDuration.appendChild(domBanDurationHoursLabel);
    domBanDuration.appendChild(domBanDurationMinutesInput);
    domBanDuration.appendChild(domBanDurationMinutesLabel);

    modal.body.appendChild(domBanDuration);

    // Button to confirm
    const domConfirm = document.createElement('button');
    domConfirm.innerText = 'Bevestig';
    domConfirm.className = 'btn-default report-confirm';
    modal.body.appendChild(domConfirm);

    domConfirm.addEventListener('click', () => {
        if (!domBanSelect.checked) { // User will not be banned => duration of-2
            ConfirmHidePost(comment, -2);
        } else if (domPermanentSelect.checked) { // USer wil be permanently banned => duration of -1
            ConfirmHidePost(comment, -1);
        } else { // User will be temporarely banned
            let duration = 0;
            duration += +domBanDurationMinutesInput.value;
            duration += +domBanDurationHoursInput.value * 60;
            ConfirmHidePost(comment, duration);
        }
        domModal.style.display = 'none';
    });

    // Set buttons en input fields in correct states (disabled or not)
    setSelectStates();
}

function AppendReport(comment, report, wrapper) {
    // Wrapper object for report
    const domReport = document.createElement('div');
    domReport.className = 'comment';

    // Header
    const domReportHeader = document.createElement('div');
    domReportHeader.className = 'comment-header';

    // body
    const domReportBody = document.createElement('div');
    domReportBody.className = 'comment-body';

    // Details of user that send in report
    const domUserDetails = document.createElement('p');
    domUserDetails.innerText = report.userFullName;
    domReportHeader.appendChild(domUserDetails);

    // User reason for report
    const domReportText = document.createElement('p');
    domReportText.innerText = report.reason;

    domReportBody.appendChild(domReportText);
    domReport.appendChild(domReportHeader);
    domReport.appendChild(domReportBody);
    wrapper.appendChild(domReport);
}

function ShowReasons(comment) {
    // modal for popup
    const modal = CreateModal(domModal);
    modal.title.innerText = constants.REASON_OF_USER;

    // Text of comment
    const domCommentText = document.createElement('p');
    domCommentText.className = 'italic';
    domCommentText.innerText = comment.commentText;
    modal.body.appendChild(domCommentText);

    // Wrapper for reports
    const domReports = document.createElement('div');
    modal.body.appendChild(domReports);

    // Append each report to list
    comment.reports.forEach((element) => {
        AppendReport(comment, element, domReports);
    });

    // Button to go to hide post confirmationi
    const domHidePostBtn = document.createElement('button');
    domHidePostBtn.innerText = constants.HIDE_POST;
    domHidePostBtn.className = 'btn-default';
    domHidePostBtn.addEventListener('click', () => {
        HidePost(comment);
    });

    // Button to allow post
    const domAllowPostBtn = document.createElement('button');
    domAllowPostBtn.innerText = constants.ALLOW_POST;
    domAllowPostBtn.className = 'btn-default';
    domAllowPostBtn.addEventListener('click', () => {
        AllowPost(comment);
    });

    modal.body.appendChild(domHidePostBtn);
    modal.body.appendChild(domAllowPostBtn);
}

function AppendComment(comment) {
    commentsLoaded++;
    // Wrapper for report
    const domReportWrapper = document.createElement('div');
    domReportWrapper.className = 'report';
    comment.domElement = domReportWrapper;

    // wrapper for comment itself
    const domComment = document.createElement('div');
    domComment.className = 'comment';

    // header
    const domHeader = document.createElement('div');
    domHeader.className = 'comment-header';

    // body
    const domBody = document.createElement('div');
    domBody.className = 'comment-body';

    // User details
    const domUserDetails = document.createElement('span');
    domUserDetails.innerHTML = `<span class="font-weight-bold">${comment.userFullName}</span> ${timeAgo(comment.dateTime)}`;

    // Text from comment
    const domCommentText = document.createElement('p');
    domCommentText.innerText = `${comment.commentText}`;

    // Button to show reasons for user(s) that reported
    const domShowReasonBtn = document.createElement('button');
    domShowReasonBtn.innerText = 'Toon reden';
    domShowReasonBtn.className = 'btn-default';
    domShowReasonBtn.addEventListener('click', () => {
        ShowReasons(comment);
    });

    // Button to open popup for hide post confirmation
    const domHidePostBtn = document.createElement('button');
    domHidePostBtn.innerText = 'Post verbergen';
    domHidePostBtn.className = 'btn-default ml-2';
    domHidePostBtn.addEventListener('click', () => {
        HidePost(comment);
    });

    // Button to allow post and delete reports
    const domAllowPostBtn = document.createElement('button');
    domAllowPostBtn.innerText = 'Post toestaan';
    domAllowPostBtn.className = 'btn-default ml-2';
    domAllowPostBtn.addEventListener('click', () => {
        AllowPost(comment);
    });


    domComment.appendChild(domHeader);
    domHeader.appendChild(domUserDetails);
    domComment.appendChild(domBody);
    domBody.appendChild(domCommentText);
    domReportWrapper.appendChild(domComment);

    domReportWrapper.appendChild(domShowReasonBtn);
    domReportWrapper.appendChild(domHidePostBtn);
    domReportWrapper.appendChild(domAllowPostBtn);
    domComments.appendChild(domReportWrapper);
}

function LoadReportedComments(id, skip, take) {
    const url = `/api/ideations/flaggedcomments/${id}/${skip}/${take}`;

    axios.get(url)
        .then((response) => {
            response.data.forEach(AppendComment);
            checkForNoReportedComments();
            domLoadMore.hidden = response.data.length < take;
        })
        .catch((err) => {
            console.log('Er liep iets mis:');
            console.log(err);
        });
}

function LoadMoreComments() {
    LoadReportedComments(id, commentsLoaded, constants.COMMENTS_TO_LOAD);
}

function initElements(){
    //div for comments
    domComments = document.createElement('div');
    domCommentsWrapper.appendChild(domComments);
    
    //Text to show if there are no reported comments
    const noCommentsContent = document.createElement('p');
    noCommentsContent.className = "italic";
    noCommentsContent.innerText = constants.NO_REPORTED_COMMENTS;
    domNoComments = noCommentsContent;

    // button to load more comments
    const domLoadMoreButton = document.createElement('button');
    domLoadMoreButton.className = 'btn-default';
    domLoadMoreButton.innerText = constants.LOAD_MORE;
    domLoadMoreButton.addEventListener('click', LoadMoreComments);
    domLoadMore = domLoadMoreButton;
    domCommentsWrapper.appendChild(domLoadMore);
    
    LoadReportedComments(id, 0, constants.COMMENTS_TO_LOAD);
}


// Used when reports are imported
export function initReportPanel(ideationReplyId, wrapper, modal) {
    domCommentsWrapper = wrapper;
    domModal = modal;
    id = ideationReplyId;
    initElements();
    
}

// Used in report panel
function init() {
    domCommentsWrapper = document.getElementById('comments');
    domModal = document.getElementById('popup');
    id = -1; // id = -1, load all reported comments;
    initElements();
}

window.onload = init;
