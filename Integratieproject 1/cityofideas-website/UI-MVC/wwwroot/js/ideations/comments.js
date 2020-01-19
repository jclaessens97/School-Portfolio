import axios from 'axios';
import {createButton, CreateModal, ICONS, timeAgo} from '../util';
import * as reports from '../users/reportPanel';
import * as constants from '../constants';


const commentsDomElements = {};
const reportedCommentsDomElements = {};
const newCommentDomElements = {};

let domModal; // for popups

let IdeationReplyId;
let UserName;
let UserIsMod;


let commentsLoaded = 0;

function checkForNoComments(){
    const noComments = commentsDomElements.noComments;
    if (commentsLoaded === 0){
        commentsDomElements.comments.appendChild(noComments);
    } else {
        if (noComments.parentNode != null) noComments.parentNode.removeChild(noComments);
    }
}

function ConfirmDeleteComment(commentId, domComment) {
    const url = `/api/ideations/comments/${commentId}`;

    axios.delete(url)
        .then(() => {
            domComment.remove();
            commentsLoaded--;
            checkForNoComments();
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het verwijderen van een comment :(');
            console.log(err);
        });
}

function ConfirmFlagComment(comment, reason) {
    const data = {
        commentId: comment.commentId,
        reason,
    };
    const url = '/api/ideations/reports';
    axios.post(url, data)
        .then((result) => {
            comment.reportId = result.data;
            comment.flagActive.hidden = true;
            comment.flagInActive.hidden = false;
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het raporteren van een comment :(');
            console.log(err);
            return false;
        });
}

function FlagComment(comment) {
    // modal for popup
    const modal = CreateModal(domModal);

    modal.title.innerText = constants.REPORT_COMMENT;

    // Element to show comment to report
    const domtext = document.createElement('p');
    domtext.className = 'italic';
    domtext.innerText = comment.commentText;
    modal.body.appendChild(domtext);

    // Wrapper for user to give reason
    const domReason = document.createElement('div');
    domReason.className = 'report-reason';
    modal.body.appendChild(domReason);

    const domInput = document.createElement('textarea');
    domInput.className = 'report-input';
    domInput.required = true;
    domInput.setAttribute('placeholder', constants.GIVE_REASON);
    domReason.appendChild(domInput);

    const domConfirm = document.createElement('button');
    domConfirm.innerText = constants.SEND;
    domConfirm.className = 'btn-default report-confirm';
    domReason.appendChild(domConfirm);

    const domError = document.createElement('span');
    domError.className = 'error';

    domInput.addEventListener('input', () => {
        domError.className = 'error';
        domError.innerText = '';
    });

    modal.body.appendChild(domError);

    domConfirm.addEventListener('click', () => {
        if (domInput.validity.valid) {
            ConfirmFlagComment(comment, domInput.value);
            domModal.style.display = 'none';
        } else {
            domError.innerText = constants.GIVE_REASON;
            domError.className += ' active';
        }
    });
}

function ConfirmUnFlagComment(comment) {
    const url = `/api/ideations/reports/remove/${comment.reportId}`;
    axios.delete(url)
        .then(() => {
            comment.flagActive.hidden = false;
            comment.flagInActive.hidden = true;
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het on-raporteren van een comment :(');
            console.log(err);
        });
}

function UnFlagComment(comment) {
    const modal = CreateModal(domModal);
    modal.title.innerText = constants.UNREPORT_COMMENT;
    const domConfirmButtom = document.createElement('button');
    domConfirmButtom.className = 'btn-default';
    domConfirmButtom.innerText = constants.CONFIRM;
    modal.body.appendChild(domConfirmButtom);
    domConfirmButtom.addEventListener('click', () => {
        ConfirmUnFlagComment(comment);
        domModal.style.display = 'none';
    });
}

function DeleteComment(commentId, domComment) {
    // Modal wrapper for confirmation
    const modal = CreateModal(domModal);
    modal.title.innerText = constants.DELETE_COMMENT;
    const domConfirmButton = document.createElement('button');
    domConfirmButton.className = 'btn-default';
    domConfirmButton.innerText = constants.CONFIRM;
    modal.body.appendChild(domConfirmButton);
    domConfirmButton.addEventListener('click', () => {
        ConfirmDeleteComment(commentId, domComment);
        domModal.style.display = 'none';
    });
}

function HideComment(commentId, domComment) {
    const url = `/api/ideations/comments/hide/${commentId}`;
    domComment.classList.add('hidden-comment');

    axios.post(url)
        .then(() => {

        })
        .catch((err) => {
            console.log('Er ging iets mis bij het posten van een comment :(');
            console.log(err);
        });
}

function UnHideComment(commentId, domComment) {
    const url = `/api/ideations/comments/unhide/${commentId}`;
    domComment.classList.remove('hidden-comment');

    axios.post(url)
        .then(() => {

        })
        .catch((err) => {
            console.log('Er ging iets mis bij het posten van een comment :(');
            console.log(err);
        });
}

function AppendComment(comment, wrapper = commentsDomElements.comments, newComment = false) {
    // Wrapper for comment
    const domComment = document.createElement('div');
    domComment.className = 'comment';

    // If comment just added make it distinguishable
    if (newComment) {
        domComment.className += ' new-comment';
    }

    // Header of comment
    const domHeader = document.createElement('div');
    domHeader.className = 'comment-header';

    // Body of comment
    const domBody = document.createElement('div');
    domBody.className = 'comment-body';

    // Add header and body to wrap per
    domComment.appendChild(domHeader);
    domComment.appendChild(domBody);

    // Details text of user and date-time posted
    let createdDetails;
    if (UserIsMod) { // Moderator gets to see full name
        createdDetails = `<span class="font-weight-bold">${comment.userFullName}</span> ${timeAgo(comment.dateTime)}`;
    } else {
        createdDetails = `<span class="font-weight-bold">${comment.userDisplayName}</span> ${timeAgo(comment.dateTime)}`;
    }

    // DOM element of details
    const domCreatedDetails = document.createElement('p');
    domCreatedDetails.innerHTML = createdDetails;
    domCreatedDetails.className = 'font-italic';
    domHeader.appendChild(domCreatedDetails);

    // DOM element for comment text
    const domCommentText = document.createElement('p');
    domCommentText.innerText = comment.commentText;
    domBody.appendChild(domCommentText);

    /* ################ USER OPTIONS #################### */
    // Wrapper for options
    const domOptionsWrapper = document.createElement('div');

    // If user is the commenter -> add delete button
    if (UserName === comment.userName) {
        const domDeleteBtn = createButton(ICONS.DELETE);
        domDeleteBtn.addEventListener('click', () => {
            DeleteComment(comment.commentId, domComment);
        });
        domOptionsWrapper.appendChild(domDeleteBtn);
    }

    // Moderator options if user has mod rights
    if (UserIsMod) {
        const domHideBtn = createButton(ICONS.HIDE);
        const domShowBtn = createButton(ICONS.UNHIDE);
        domShowBtn.hidden = true;
        domHideBtn.addEventListener('click', () => {
            HideComment(comment.commentId, domComment);
            domShowBtn.hidden = false;
            domHideBtn.hidden = true;
        });
        domShowBtn.addEventListener('click', () => {
            UnHideComment(comment.commentId, domComment);
            domShowBtn.hidden = true;
            domHideBtn.hidden = false;
        });
        domOptionsWrapper.appendChild(domHideBtn);
        domOptionsWrapper.appendChild(domShowBtn);
    }

    // Report button
    const domFlagBtnActive = createButton(ICONS.FLAGINACTIVE);
    const domFlagBtnInActive = createButton(ICONS.FLAGACTIVE);

    domFlagBtnInActive.hidden = true;

    comment.flagActive = domFlagBtnActive;
    comment.flagInActive = domFlagBtnInActive;
    
    if(comment.reportedByMe){
        domFlagBtnActive.hidden = true;
        domFlagBtnInActive.hidden = false;
    }

    domFlagBtnActive.addEventListener('click', () => {
        FlagComment(comment);
    });
    domFlagBtnInActive.addEventListener('click', () => {
        UnFlagComment(comment);
    });

    domOptionsWrapper.appendChild(domFlagBtnActive);
    domOptionsWrapper.appendChild(domFlagBtnInActive);

    // Add user options element to header
    domHeader.appendChild(domOptionsWrapper);
    /* ##################################################### */

    // If this is a new comment, prepend instead of append
    if (newComment) {
        wrapper.prepend(domComment);
    } else {
        wrapper.appendChild(domComment);
    }
}

// Add new comment
function AddComment() {
    // Check if input is valid -> otherwise show error and abort
    if (!newCommentDomElements.input.validity.valid) {
        newCommentDomElements.error.className += ' active';
        newCommentDomElements.error.innerHTML = constants.ERR_EMPTY;
        return;
    }

    // Comment text to add
    const commentText = newCommentDomElements.input.value;

    // Data to post to server
    const dataObject = {
        commentText,
        ideationReplyId: IdeationReplyId,
    };

    // Data in json format
    const dataJson = JSON.stringify(dataObject);

    // Post url
    const url = '/api/ideations/comments';

    // Headers
    const headers = {
        'Content-Type': 'application/json',
    };

    axios.post(url, dataJson, { headers })
        .then((result) => {
            newCommentDomElements.input.value = ''; // empty input field
            AppendComment(result.data, commentsDomElements.main, true);
            commentsLoaded++;
            checkForNoComments();
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het posten van een comment :(');
            console.log(err);
        });
}

// Load reported comments for moderators to handle - MOD ONLY //Code moved to reportpanel.js
/*
function LoadReportedComments(skip, take) {
    const url = `/api/ideations/flaggedcomments/${IdeationReplyId}/${skip}/${take}`;

    axios.get(url)
        .then((result) => {
            result.data.forEach(c => AppendComment(c, reportedCommentsDomElements.comments, false));
        })
        .catch((err) => {
            console.log('Er liep iets mis:');
            console.log(err);
        });
} */

function LoadComments(skip, take) {
    const url = `/api/ideations/comments/${IdeationReplyId}/${skip}/${take}`;

    axios.get(url)
        .then((result) => {
            result.data.forEach((element) => {
                AppendComment(element);
            });
            commentsLoaded += result.data.length;
            commentsDomElements.loadMore.hidden = result.data.length < take;
            checkForNoComments();
        })
        .catch((err) => {
            console.log('Er ging iets mis bij het laden van de comments :(');
            console.log(err);
        });
}

function LoadMoreComments() {
    LoadComments(commentsLoaded, constants.COMMENTS_TO_LOAD);
}

export function initCommentSection(commentSection, newComment, modal, details) {
    // initialize needed info
    UserName = details.UserName;
    UserIsMod = details.UserIsMod;
    IdeationReplyId = details.IdeationReplyId;

    // initialize DOM elements
    commentsDomElements.main = commentSection;
    newCommentDomElements.main = newComment;
    domModal = modal;
    
    //Text to show if there are no comments
    const noCommentsContent = document.createElement('p');
    noCommentsContent.className = "italic";
    noCommentsContent.innerText = constants.NO_COMMENTS;
    commentsDomElements.noComments = noCommentsContent;

    // Wrapper for comments
    const domComments = document.createElement('div');
    commentsDomElements.main.appendChild(domComments);
    commentsDomElements.comments = domComments;

    /* ################### Add new comment section ##################### */
    if (UserName != null) {
        // Wrapper for new comment input and button
        const domCommentWrapper = document.createElement('div');
        domCommentWrapper.className = 'comment-wrapper';
        newCommentDomElements.main.appendChild(domCommentWrapper);

        // Input for new comment
        const domNewCommentInput = document.createElement('textarea');
        domNewCommentInput.className = 'comment-area';
        domNewCommentInput.setAttribute('type', 'text');
        domNewCommentInput.required = true;
        domCommentWrapper.appendChild(domNewCommentInput);
        newCommentDomElements.input = domNewCommentInput;

        // Button for adding comment
        const domNewCommentButton = document.createElement('button');
        domNewCommentButton.className = 'btn-default btn-add-comment';
        domNewCommentButton.innerText = constants.REACT_BUTTON;
        domCommentWrapper.appendChild(domNewCommentButton);
        newCommentDomElements.button = domNewCommentButton;

        domNewCommentButton.addEventListener('click', AddComment);

        // Error span if comment input is empty and user tries to add comment
        const domNewCommentErr = document.createElement('span');
        domNewCommentErr.className = 'error';
        newComment.appendChild(domNewCommentErr);
        newCommentDomElements.error = domNewCommentErr;

        // Empty error when user inputs text
        domNewCommentInput.addEventListener('input', () => {
            domNewCommentErr.className = 'error';
            domNewCommentErr.innerHTML = '';
        });
    }
    /* ########################################## */

    // button to load more comments
    const domLoadMoreButton = document.createElement('button');
    domLoadMoreButton.className = 'btn-default';
    domLoadMoreButton.innerText = constants.LOAD_MORE;
    domLoadMoreButton.addEventListener('click', LoadMoreComments);
    commentsDomElements.loadMore = domLoadMoreButton;

    commentsDomElements.main.appendChild(domLoadMoreButton);


    LoadComments(0, constants.COMMENTS_TO_LOAD);
}

// !!! To be called AFTER initCommentSection !!!
export function initShowReportedComments(commentSection) {
    reportedCommentsDomElements.main = commentSection;

    // Wrapper for comments
    const domComments = document.createElement('div');
    reportedCommentsDomElements.main.appendChild(domComments);
    reportedCommentsDomElements.comments = domComments;

    // LoadReportedComments(0,COMMENTS_TO_LOAD);
    reports.initReportPanel(IdeationReplyId, commentSection, domModal);
}
