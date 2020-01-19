import {EnableValidation, OnSubmit} from './validation';

function init() {
    EnableValidation();
    const form = document.getElementById('form');
    form.addEventListener('submit', (event) => {
        const isValid = OnSubmit();
        if (!isValid) event.preventDefault();
    });
}

window.onload = init;
