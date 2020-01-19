import axios from 'axios';
import CreateForm from '../forms/formCreator';

// Saving the form
/*
async function SaveForm() {
    const projectId = document.getElementById('ProjectId').value;
    const phaseId = document.getElementById('PhaseId').value;
    const formTitle = document.getElementById('FormTitle').value;
    const form = {
        projectId,
        phaseId,
        formTitle,
        questions: Questions,
    };

    const headers = {
        'Content-Type': 'application/json',
    };

    try {
        await axios.post('/api/ideations', JSON.stringify(form, replacer), { headers });
    } catch (err) {

    }
}*/

function init() {
    // Method in formCreator.js
    CreateForm(2);
}

window.onload = init;
