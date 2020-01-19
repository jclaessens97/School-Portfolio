import axios from 'axios';
import $ from 'jquery';
import * as toastr from 'toastr';

const baseUri = '/api/platformrequests';

async function acceptRequest(val, answer) {
    let message = answer;
    if (!message || message === '') {
        message = 'Uw aanvraag voor een platform werd geaccepteerd door onze administrators.';
    }

    const data = {
        id: val,
        text: message,
    };

    const postUrl = `${baseUri}/acceptrequest`;
    const headers = {
        'Content-Type': 'application/json',
    };

    try {
        await axios.post(postUrl,
            JSON.stringify(data),
            { headers });

        const count = parseInt(data.id, 10);
        const row1 = document.getElementById(`UntreatedAnswerRow${count}`);
        const row2 = document.getElementById(`UntreatedManageRow${count}`);
        const answerRequest = document.getElementById(`AnswerRequest${count}`);
        const treated = document.getElementById(`Treated${count}`);


        while (row1.hasChildNodes()) {
            row1.removeChild(row1.firstChild);
        }
        while (row2.hasChildNodes()) {
            row2.removeChild(row2.firstChild);
        }

        answerRequest.style.visibility = 'visible';
        answerRequest.innerText = `Answer on request: ${data.text}`;

        treated.innerText = `Behandeld? ${true}`;
        toastr.clear();
        toastr.info('Het request is succesvol geaccepteerd.');
    } catch (err) {
        toastr.clear();
        toastr.error(err);
    }
}

async function denyRequest(val, answer) {
    let message = answer;
    if (!message || message === '') {
        message = 'Sorry maar uw aanvraag voor een platform werd geweigerd door onze administrators.';
    }

    const data = {
        id: val,
        text: message,
    };

    const postUrl = `${baseUri}/denyrequest`;

    const headers = {
        'Content-Type': 'application/json',
    };

    try {
        await axios.post(postUrl,
            JSON.stringify(data),
            { headers });

        const count = parseInt(data.id, 10);
        const row1 = document.getElementById(`UntreatedAnswerRow${count}`);
        const row2 = document.getElementById(`UntreatedManageRow${count}`);
        const answerRequest = document.getElementById(`AnswerRequest${count}`);
        const treated = document.getElementById(`Treated${count}`);

        while (row1.hasChildNodes()) {
            row1.removeChild(row1.firstChild);
        }
        while (row2.hasChildNodes()) {
            row2.removeChild(row2.firstChild);
        }

        answerRequest.style.visibility = 'visible';
        answerRequest.innerText = `Answer on request: ${data.text}`;

        treated.innerText = `Behandeld? ${true}`;
        toastr.clear();
        toastr.info('Het request is succesvol afgekeurd.')

    } catch (err) {
        toastr.error(err);
    }
}

async function getRequests() {
    try {
        const response = await axios.get(baseUri);
        return response.data;
    } catch (err) {
        toastr.clear();
        toastr.error(err);
        return [];
    }
}

async function loadRequests() {
    const requests = await getRequests();

    requests.forEach((request) => {
        const div = document.getElementById('panel');
        const table = document.createElement('TABLE');
        table.id = `Table${request.platformRequestId}`;
        table.style.borderBottom = 'thick solid #3C8DBC';
        table.style.borderTop = 'thick solid #3C8DBC';
        table.align = 'center';
        table.width = '100%';

        const rowHeading = document.createElement('tr');
        const requestIdColumn = document.createElement('td');
        requestIdColumn.id = 'RequestId';
        requestIdColumn.innerText = `VerzoeksId: ${request.platformRequestId}`;
        rowHeading.appendChild(requestIdColumn);
        const usernameColumn = document.createElement('td');
        usernameColumn.id = 'UserID';
        usernameColumn.innerText = `Organisatienaam: ${request.organisationName}`;
        rowHeading.appendChild(usernameColumn);
        table.appendChild(rowHeading);

        const rowDescription = document.createElement('tr');
        const reasonColumn = document.createElement('td');
        reasonColumn.id = 'Reason';
        reasonColumn.colSpan = '2';
        reasonColumn.innerText = `Reden voor platform-aanvraag: ${request.reason}`;
        rowDescription.appendChild(reasonColumn);
        table.appendChild(rowDescription);

        const rowComment = document.createElement('tr');
        rowComment.id = 'Comment';
        const rowColumn = document.createElement('td');
        rowColumn.id = `AnswerRequest${request.requestId}`;
        rowColumn.colSpan = '2';
        rowColumn.innerText = `Antwoord op request: ${request.answer}`;
        rowComment.appendChild(rowColumn);
        table.appendChild(rowComment);

        if (!request.treated) {
            rowComment.style.display = 'none';

            const rowAnswer = document.createElement('tr');
            rowAnswer.id = `UntreatedAnswerRow${request.requestId}`;
            const answerColumn = document.createElement('td');
            answerColumn.id = 'Answer';
            answerColumn.colSpan="2";
            const text = document.createElement('textarea');
            text.className="form-control";
            text.setAttribute('rows', '3');
            text.setAttribute('cols', '25');
            text.placeholder = 'Add comment to your answer';
            answerColumn.appendChild(text);
            rowAnswer.appendChild(answerColumn);
            table.appendChild(rowAnswer);

            const rowManage = document.createElement('tr');
            rowManage.id = `UntreatedManageRow${request.requestId}`;
            const manageColumn1 = document.createElement('td');
            manageColumn1.id = 'Accept';
            const answer1 = document.createElement('button');
            answer1.innerText = 'Goedkeuren';
            answer1.className='btn btn-block btn-primary btn-flat';
            answer1.style.width="200px";
            answer1.style.backgroundColor = "#3C8DBC";
            answer1.style.borderColor = "#3C8DBC";
            answer1.addEventListener('click', () => {
                acceptRequest(request.platformRequestId, text.value);
            });
            const manageColumn2 = document.createElement('td');
            manageColumn2.id = 'Deny';
            const answer2 = document.createElement('button');
            answer2.innerText = 'Afkeuren';
            answer2.style.backgroundColor = "#3C8DBC";
            answer2.style.borderColor = "#3C8DBC";
            answer2.className='btn btn-block btn-primary btn-flat';
            answer2.style.width="200px";
            answer2.addEventListener('click', () => {
                denyRequest(request.platformRequestId, text.value);
            });
            manageColumn1.appendChild(answer1);
            manageColumn2.appendChild(answer2);
            rowManage.appendChild(manageColumn1);
            rowManage.appendChild(manageColumn2);
            table.appendChild(rowManage);
        }

        const rowInfo = document.createElement('tr');
        const treatedColumn = document.createElement('td');
        treatedColumn.id = `Treated${request.requestId}`;
        treatedColumn.innerText = `Behandeld? ${request.treated}`;
        rowInfo.appendChild(treatedColumn);
        const dateColumn = document.createElement('td');
        dateColumn.id = 'Date';
        dateColumn.innerText = "Datum aanvraag: "+request.date;
        rowInfo.appendChild(dateColumn);
        table.appendChild(rowInfo);

        div.appendChild(table);
    });
}

function init() {
    loadRequests();
}

window.onload = init;