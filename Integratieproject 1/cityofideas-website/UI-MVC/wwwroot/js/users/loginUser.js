import axios from 'axios';
import $ from 'jquery';
import * as toastr from 'toastr';

const baseUri = '/api/users';
const ERR_REQUIRED = 'is verplicht in te geven.';
const emailError = document.getElementById('email-error');
const passwordError = document.getElementById('password-error');
const otherError = document.getElementById('other-error');


function validateEmail(val) {
    if (val.email === '') {
        emailError.innerText = 'Email ' + ERR_REQUIRED;
        return false;
    } else {
        return true;
    }
}

function validatePassword(val) {
    if (val.password === '') {
        passwordError.innerText = 'Wachtwoord ' + ERR_REQUIRED;
        return false;
    } else {
        return true;
    }
}

function isValid(val) {
    let valid = true;

    if (!validateEmail(val)) {
        valid = false;
    }

    if (!validatePassword(val)) {
        valid = false;
    }
    return valid;
}

async function loginUser(val) {
    toastr.clear();
    emailError.innerText = null;
    passwordError.innerText = null;

    if (!isValid(val)) {
        return;
    }

    const postUrl = `${baseUri}/loginuser`;
    const headers = {
        'Content-Type': 'application/json',
    };

    try {
        console.log(val);
        await axios.post(postUrl, JSON.stringify(val), {headers});
        // toastr.success('Succesvol ingelogd');
        window.location.replace("/");
    } catch (err) {
        console.log(err.response.data);
        err.response.data.forEach((error) => {
            if (error.includes("platform")) {
                var link = "/Identity/Account/Register?account=true";
                window.location.replace(link);
            } else if (error.includes("emailadres")) {
                emailError.innerText = error;
            } else if (error.includes("geblokkeerd")) {
                otherError.innerText = error;
            } else if (error.includes("bevestigd")) {
                otherError.innerText = error;
            } else if (error.includes("administrator")) {
                otherError.innerText = error;
            } else {
                passwordError.innerText = error;
            }
        });
    }
}

async function loadFields() {
    const otherDiv = document.getElementById('otherDiv');
    const otherError = document.getElementById("other-error");
    otherError.className = "error";
    otherDiv.appendChild(otherError);

    const emailDiv = document.getElementById('emailDiv');
    const inputEmail = document.getElementById('email');
    inputEmail.setAttribute("required", "true");
    const emailError = document.getElementById("email-error");
    emailError.className = "error";
    emailDiv.appendChild(inputEmail);
    emailDiv.appendChild(emailError);

    const passwordDiv = document.getElementById('passwordDiv');
    const inputPassword = document.getElementById('password');
    inputPassword.setAttribute("type", "password");
    inputPassword.setAttribute("required", "true");
    const passwordError = document.getElementById("password-error");
    passwordError.className = "error";
    passwordDiv.appendChild(inputPassword);
    passwordDiv.appendChild(passwordError);

    const rememberDiv = document.getElementById('rememberDiv');
    const inputRemember = document.getElementById('remember');
    rememberDiv.appendChild(inputRemember);

    const loginButton = document.getElementById('login');
    loginButton.addEventListener('click', (e) => {
        e.preventDefault();
        const userObj = {
            email: inputEmail.value,
            password: inputPassword.value,
            remember: inputRemember.checked
        };
        loginUser(userObj);
        console.log(userObj);
    });
}


function addEventHandlers() {
    // TODO: make work without jquery
    $('#Search').keyup(async () => {
        const search = $('#Search').val().toUpperCase();
        console.log(search);
        const response = await axios.get(`/api/users/${search}`);
        console.log(response.data.list);
        $('#Search').autocomplete({source: response.data.list});
    });
}

function init() {
    loadFields();
    addEventHandlers();
}

window.onload = init;