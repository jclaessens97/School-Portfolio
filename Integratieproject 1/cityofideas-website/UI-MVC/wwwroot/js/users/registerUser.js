import axios from 'axios';
import $ from 'jquery';
import * as toastr from 'toastr';

toastr.options.timeOut = 9999;

const baseUri = '/api/users';
const ERR_REQUIRED = 'is verplicht.';
const ERR_IDENTICAL = 'Het paswoord en het bevestigde paswoord zijn niet identiek.';
const ERR_VAT = 'Het ingegeven BTW-nummer heeft niet het correcte formaat.';
const ERR_BIRTHDAY = ' jaar is de minimumleeftijd om u te kunnen registreren.';
const ERR_ZIPCODE = 'De ingegeven postcode heeft niet het correcte formaat.';

const firstnameError = document.getElementById('firstname-error');
const lastnameError = document.getElementById('lastname-error');
const emailError = document.getElementById('email-error');
const passwordError = document.getElementById('password-error');
const confirmpasswordError = document.getElementById('confirmpassword-error');
const organisationError = document.getElementById('organisation-error');
const firmnameError = document.getElementById('firmname-error');
const vatError = document.getElementById('vat-error');
const birthdayError = document.getElementById('birthday-error');
const sexError = document.getElementById('sex-error');
const zipcodeError = document.getElementById('zipcode-error');
const otherError = document.getElementById('other-error');

function validateFirstname(val) {
    if (!val.organisation && val.firstname === '') {
        firstnameError.innerText = "voornaam " + ERR_REQUIRED;
        return false;
    } else {
        return true;
    }
}

function validateLastname(val) {
    if (!val.organisation && val.lastname === '') {
        lastnameError.innerText = "achternaam " + ERR_REQUIRED;
        return false;
    } else {
        return true;
    }
}


function validateEmail(val) {
    if (val.email === '') {
        emailError.innerText = "email " + ERR_REQUIRED;
        return false;
    } else {
        return true;
    }
}

function validatePassword(val) {
    if (val.password === '') {
        passwordError.innerText = "wachtwoord " + ERR_REQUIRED;
        return false;
    } else {
        return true;
    }

}


function validateConfirmPassword(val) {
    if (val.confirmPassword === '') {
        confirmpasswordError.innerText = "wachtwoordsbevestiging " + ERR_REQUIRED;
        return false;
    } else if (val.confirmPassword != val.password) {
        confirmpasswordError.innerText = ERR_IDENTICAL;
        return false;
    } else {
        return true;
    }
}

function validateSex(val) {
    if (!val.sexV && !val.sexM) {
        sexError.innerText = "Geslacht " + ERR_REQUIRED;
        return false;
    } else {
        return true;
    }

}

function validateBirthday(val) {
    console.log(val.birthday);
    const minAge = 12;
    var today2 = new Date(new Date().setFullYear(new Date().getFullYear() - minAge)).toISOString().slice(0, 10);
    if (val.birthday === '') {
        birthdayError.innerText = "Geboortedatum " + ERR_REQUIRED;
        return false;
    } else if (val.birthday > today2) {
        birthdayError.innerText = minAge + ERR_BIRTHDAY;
        return false;
    } else {
        return true;
    }

}

function validateZipcode(val) {
    var regex = /^(?:(?:[1-9])(?:\d{3}))$/;
    if (val.zipcode === '') {
        zipcodeError.innerText = "Postcode  " + ERR_REQUIRED;
        return false;
    } else if (!regex.test(val.zipcode)) {
        zipcodeError.innerText = ERR_ZIPCODE;
        return false;
    } else {
        return true;
    }

}

function validateFirmname(val) {
    if (val.organisation && val.firmname === '') {
        firmnameError.innerText = "Bedrijfsnaam " + ERR_REQUIRED;
        return false;
    } else {
        return true;
    }
}


function validateVAT(val) {
    var regex = /^BE[0-9]{10,10}$/;
    if (val.organisation && val.vat === '') {
        vatError.innerText = "BTW-nummer " + ERR_REQUIRED;
        return false;
    } else if (!regex.test(val.vat)) {
        vatError.innerText = ERR_VAT;
        return false;
    } else {
        return true;
    }
}

function isValid(val) {
    let valid = true;
    if (!val.exists) {
        if (val.organisation) {
            if (!validateFirmname(val)) {
                valid = false;
            }

            if (!validateVAT(val)) {
                valid = false;
            }
            if (!validateEmail(val)) {
                valid = false;
            }

            if (!validatePassword(val)) {
                valid = false;
            }

            if (!validateConfirmPassword(val)) {
                valid = false;
            }
            if (!validateZipcode(val)) {
                valid = false;
            }
        } else {
            if (!validateFirstname(val)) {
                valid = false;
            }

            if (!validateLastname(val)) {
                valid = false;
            }

            if (!validateEmail(val)) {
                valid = false;
            }

            if (!validatePassword(val)) {
                valid = false;
            }

            if (!validateConfirmPassword(val)) {
                valid = false;
            }

            if (!validateSex(val)) {
                valid = false;
            }

            if (!validateBirthday(val)) {
                valid = false;
            }

            if (!validateZipcode(val)) {
                valid = false;
            }
        }
    } else {
        if (!validateEmail(val)) {
            valid = false;
        }

        if (!validatePassword(val)) {
            valid = false;
        }
    }
    return valid;
}

function clearErrors() {
    firstnameError.innerText = null;
    lastnameError.innerText = null;
    emailError.innerText = null;
    passwordError.innerText = null;
    confirmpasswordError.innerText = null;
    organisationError.innerText = null;
    firmnameError.innerText = null;
    vatError.innerText = null;
    otherError.innerText = null;
    sexError.innerText = null;
    birthdayError.innerText = null;
    zipcodeError.innerText = null;
}

async function registerUser(val) {
    toastr.clear();
    clearErrors();

    if (!val.exists) {
        if (!val.organisation) {
            var firstname = val.firstname[0].toUpperCase()+val.firstname.substring(1).toLowerCase();
            var lastname = val.lastname[0].toUpperCase()+val.lastname.substring(1).toLowerCase();
            val.username = firstname + "_" + lastname;
        } else {
            val.username = val.firmname.replace(" ", "_");
        }
    }

    if (!isValid(val)) {
        return;
    }

    const postUrl = `${baseUri}/registeruser`;
    const headers = {
        'Content-Type': 'application/json',
    };

    try {
        console.log(val);
        await axios.post(postUrl, JSON.stringify(val), {headers});
        if (val.exists) {
            window.location.replace("/");
        } else {
            window.location.replace("/?registerSuccess=true");
        }
    } catch (err) {
        console.log(err.response.data);
        err.response.data.forEach((error) => {
            if (error.includes("Wachtwoord")) {
                passwordError.innerText = error;
                confirmpasswordError.innerText = error;
            } else if (error.includes("Email")) {
                emailError.innerText = error;
            } else {
                otherError.innerText = error;
            }
        });
    }
}

async function loadFields() {
    var param = location.search.split('account=')[1];
    if (param) {
        showOrganisationFields();
        toastr.info('U hebt reeds een account maar nog niet voor dit platform. Gelieve u email en wachtwoord opnieuw in te geven voor u te registreren op dit platform.');
        const firstnameDiv = document.getElementById('firstnameDiv');
        firstnameDiv.style.display = "none";
        const inputFirstname = document.getElementById('firstname');
        inputFirstname.required = false;
        firstnameDiv.appendChild(inputFirstname);

        const lastnameDiv = document.getElementById('lastnameDiv');
        lastnameDiv.style.display = "none";
        const inputLastname = document.getElementById('lastname');
        inputLastname.required = false;
        lastnameDiv.appendChild(inputLastname);

        const confirmPasswordDiv = document.getElementById('confirmPasswordDiv');
        confirmPasswordDiv.style.display = "none";
        const inputConfirmPassword = document.getElementById('confirmpassword');
        inputConfirmPassword.required = false;
        confirmPasswordDiv.appendChild(inputConfirmPassword);

        const birthdayDiv = document.getElementById('birthdayDiv');
        birthdayDiv.style.display = "none";
        const birthdayInput = document.getElementById('birthday');
        birthdayInput.required = false;
        birthdayDiv.appendChild(birthdayInput);

        const sexDiv = document.getElementById('sexDiv');
        sexDiv.style.display = "none";
        const sexMDiv = document.getElementById('sexMDiv');
        const sexVDiv = document.getElementById('sexVDiv');
        const inputSexM = document.getElementById('sexM');
        const inputSexV = document.getElementById('sexV');
        /*sexMDiv.style.padding="100px";
        sexVDiv.style.padding="100px";*/
        inputSexM.setAttribute("value", "M");
        inputSexM.setAttribute("name", "sex");
        inputSexM.required = false;

        inputSexV.setAttribute("value", "V");
        inputSexV.setAttribute("name", "sex");
        inputSexV.required = false;

        sexMDiv.appendChild(inputSexM);
        sexVDiv.appendChild(inputSexV);
        sexDiv.appendChild(sexMDiv);
        sexDiv.appendChild(sexVDiv);

        const zipcodeDiv = document.getElementById('zipcodeDiv');
        zipcodeDiv.style.display = "none";
        const inputZipcode = document.getElementById('zipcode');
        inputZipcode.required = false;
        zipcodeDiv.appendChild(inputZipcode);

        const organisationDiv = document.getElementById('organisationDiv');
        organisationDiv.style.display = "none";
        const checkboxOrganisation = document.getElementById('organisation');
        organisationDiv.appendChild(checkboxOrganisation);

        const firmNameDiv = document.getElementById('firmNameDiv');
        firmNameDiv.style.display = 'none';
        const inputFirmname = document.getElementById('firmname');
        inputFirmname.required = false;
        firmNameDiv.appendChild(inputFirmname);

        const VATDiv = document.getElementById('VATDiv');
        VATDiv.style.display = 'none';
        const inputVAT = document.getElementById('vat');
        inputVAT.required = false;
        VATDiv.appendChild(inputVAT);

        const emailDiv = document.getElementById('emailDiv');
        const inputEmail = document.getElementById('email');
        inputEmail.className = 'form-control';
        inputEmail.placeholder = "voorbeeld@mail.com";
        inputEmail.setAttribute("type", "email");
        inputEmail.required = true;
        emailDiv.appendChild(inputEmail);
        const emailError = document.getElementById("email-error");
        emailError.className = "error";
        emailDiv.appendChild(emailError);

        const passwordDiv = document.getElementById('passwordDiv');
        const inputPassword = document.getElementById('password');
        inputPassword.className = 'form-control';
        inputPassword.setAttribute("type", "password");
        inputPassword.required = true;
        passwordDiv.appendChild(inputPassword);
        const passwordError = document.getElementById("password-error");
        passwordError.className = "error";
        passwordDiv.appendChild(passwordError);

        const registerButton = document.getElementById('register');
        registerButton.addEventListener('click', (e) => {
            e.preventDefault();
            const userObj = {
                email: inputEmail.value,
                password: inputPassword.value,
                exists: true
            };
            registerUser(userObj);
            console.log("gekend");

        });
        
        const alreadyAccount = document.getElementById("alreadyAccount");
        alreadyAccount.style.display="none";
    } else {
        showOrganisationFields();

        const otherDiv = document.getElementById('otherDiv');
        const otherError = document.getElementById("other-error");
        otherError.className = "error";
        otherDiv.appendChild(otherError);

        const firstnameDiv = document.getElementById('firstnameDiv');
        const inputFirstname = document.getElementById('firstname');
        inputFirstname.className = 'form-control';
        inputFirstname.setAttribute("required", "true");
        firstnameDiv.appendChild(inputFirstname);
        const firstnameError = document.getElementById("firstname-error");
        firstnameError.className = "error";
        firstnameDiv.appendChild(firstnameError);

        const lastnameDiv = document.getElementById('lastnameDiv');
        const inputLastname = document.getElementById('lastname');
        inputLastname.className = 'form-control';
        inputLastname.setAttribute("required", "true");
        lastnameDiv.appendChild(inputLastname);
        const lastnameError = document.getElementById("lastname-error");
        lastnameError.className = "error";
        lastnameDiv.appendChild(lastnameError);

        const emailDiv = document.getElementById('emailDiv');
        const inputEmail = document.getElementById('email');
        inputEmail.className = 'form-control';
        inputEmail.placeholder = "voorbeeld@mail.com";
        inputEmail.setAttribute("type", "email");
        inputEmail.setAttribute("required", "true");
        emailDiv.appendChild(inputEmail);
        const emailError = document.getElementById("email-error");
        emailError.className = "error";
        emailDiv.appendChild(emailError);

        const passwordDiv = document.getElementById('passwordDiv');
        const inputPassword = document.getElementById('password');
        inputPassword.className = 'form-control';
        inputPassword.setAttribute("type", "password");
        inputPassword.setAttribute("required", "true");
        passwordDiv.appendChild(inputPassword);
        const passwordError = document.getElementById("password-error");
        passwordError.className = "error";
        passwordDiv.appendChild(passwordError);

        const confirmPasswordDiv = document.getElementById('confirmPasswordDiv');
        const inputConfirmPassword = document.getElementById('confirmpassword');
        inputConfirmPassword.className = 'form-control';
        inputConfirmPassword.setAttribute("type", "password");
        inputConfirmPassword.setAttribute("required", "true");
        confirmPasswordDiv.appendChild(inputConfirmPassword);
        const confirmPasswordError = document.getElementById("confirmpassword-error");
        confirmPasswordError.className = "error";
        confirmPasswordDiv.appendChild(confirmPasswordError);

        const birthdayDiv = document.getElementById('birthdayDiv');
        const birthdayInput = document.getElementById('birthday');
        birthdayInput.className = 'form-control';
        birthdayInput.setAttribute("type", "date");
        birthdayInput.required = true;
        birthdayDiv.appendChild(birthdayInput);
        const birthdayError = document.getElementById("birthday-error");
        birthdayError.className = "error";
        birthdayDiv.appendChild(birthdayError);

        const sexDiv = document.getElementById('sexDiv');
        const sexMDiv = document.getElementById('sexMDiv');
        const sexVDiv = document.getElementById('sexVDiv');
        const inputSexM = document.getElementById('sexM');
        const inputSexV = document.getElementById('sexV');
        inputSexM.setAttribute("value", "M");
        inputSexM.setAttribute("name", "sex");


        inputSexV.setAttribute("value", "V");
        inputSexV.setAttribute("name", "sex");
        const sexError = document.getElementById("sex-error");
        sexError.className = "error";

        sexMDiv.appendChild(inputSexM);
        sexVDiv.appendChild(inputSexV);
        sexDiv.appendChild(sexMDiv);
        sexDiv.appendChild(sexVDiv);
        sexDiv.appendChild(sexError);

        const zipcodeDiv = document.getElementById('zipcodeDiv');
        const inputZipcode = document.getElementById('zipcode');
        inputZipcode.className = 'form-control';
        inputZipcode.setAttribute("type", "number");
        inputZipcode.required = true;
        zipcodeDiv.appendChild(inputZipcode);
        const zipcodeError = document.getElementById("zipcode-error");
        zipcodeError.className = "error";
        zipcodeDiv.appendChild(zipcodeError);

        const organisationDiv = document.getElementById('organisationDiv');
        const checkboxOrganisation = document.getElementById('organisation');
        checkboxOrganisation.className = 'form-control';
        checkboxOrganisation.addEventListener('click', (event) => {
            showOrganisationFields();
        });
        organisationDiv.appendChild(checkboxOrganisation);
        const organisationError = document.getElementById("organisation-error");
        organisationError.className = "error";
        organisationDiv.appendChild(organisationError);

        const firmNameDiv = document.getElementById('firmNameDiv');
        firmNameDiv.style.display = 'none';
        const inputFirmname = document.getElementById('firmname');
        inputFirmname.className = 'form-control';
        inputFirmname.setAttribute("required", "false");
        firmNameDiv.appendChild(inputFirmname);
        const firmnameError = document.getElementById("firmname-error");
        firmnameError.className = "error";
        firmNameDiv.appendChild(firmnameError);

        const VATDiv = document.getElementById('VATDiv');
        VATDiv.style.display = 'none';
        const inputVAT = document.getElementById('vat');
        inputVAT.placeholder = "BE0999999999";
        inputVAT.className = 'form-control';
        inputVAT.setAttribute("required", "false");
        VATDiv.appendChild(inputVAT);
        const VATError = document.getElementById("vat-error");
        VATError.className = "error";
        VATDiv.appendChild(VATError);

        const registerButton = document.getElementById('register');
        registerButton.addEventListener('click', (e) => {
            e.preventDefault();
            const userObj = {
                firstname: inputFirstname.value,
                lastname: inputLastname.value,
                email: inputEmail.value,
                password: inputPassword.value,
                confirmPassword: inputConfirmPassword.value,
                organisation: checkboxOrganisation.checked,
                firmname: inputFirmname.value,
                vat: inputVAT.value,
                username: null,
                birthday: (birthdayInput.value !== '') ? birthdayInput.value : undefined,
                zipcode: inputZipcode.value,
                sexM: inputSexM.checked,
                sexV: inputSexV.checked,
                exists: false
            };
            registerUser(userObj);
            console.log("niet gekend");
            console.log(userObj);
        });
    }
}

function showOrganisationFields() {
    const firmNameDiv = document.getElementById('firmNameDiv');
    const VATDiv = document.getElementById('VATDiv');
    const firstNameDiv = document.getElementById('firstnameDiv');
    const lastnameDiv = document.getElementById("lastnameDiv");
    const birthdayDiv = document.getElementById("birthdayDiv");
    const sexDiv = document.getElementById("sexDiv");
    const zipcodeDiv = document.getElementById("zipcodeDiv");
    const emailDiv = document.getElementById("emailDiv");
    const passwordDiv = document.getElementById("passwordDiv");
    const confirmPasswordDiv = document.getElementById("confirmPasswordDiv");

    const inputFirmname = document.getElementById('firmname');
    const inputVAT = document.getElementById('vat');
    const checkboxOrganisation = document.getElementById('organisation');
    const inputFirstname = document.getElementById('firstname');
    const inputLastname = document.getElementById('lastname');
    const inputBirthday = document.getElementById("birthday");
    const inputSexM = document.getElementById("sexM");
    const inputSexV = document.getElementById("sexV");
    const inputZipcode = document.getElementById("zipcode");
    const inputPassword = document.getElementById("password");
    const inputConfirmPassword = document.getElementById("confirmpassword");
    const inputEmail = document.getElementById("email");

    if (checkboxOrganisation.checked) {
        firstNameDiv.style.display = "none";
        lastnameDiv.style.display = "none";
        sexDiv.style.display = "none";
        birthdayDiv.style.display = "none";
        firmNameDiv.style.display = 'inline';
        VATDiv.style.display = 'inline';
        passwordDiv.style.display = "inline";
        zipcodeDiv.style.display = "inline";
        emailDiv.style.display = "inline";
        confirmPasswordDiv.style.display = "inline";

        inputFirmname.required = true;
        inputVAT.required = true;
        inputZipcode.required = true;
        inputPassword.required = true;
        inputConfirmPassword.required = true;
        inputEmail.required = true;

        inputFirstname.required = false;
        inputLastname.required = false;
        inputBirthday.required = false;
        inputSexV.required = false;
        inputSexM.required = false;
    } else {
        firstNameDiv.style.display = "inline";
        lastnameDiv.style.display = "inline";
        sexDiv.style.display = "inline";
        birthdayDiv.style.display = "inline";
        firmNameDiv.style.display = 'none';
        VATDiv.style.display = 'none';
        passwordDiv.style.display = "inline";
        zipcodeDiv.style.display = "inline";
        emailDiv.style.display = "inline";
        confirmPasswordDiv.style.display = "inline";

        inputFirmname.required = false;
        inputVAT.required = false;

        inputZipcode.required = true;
        inputEmail.required = true;
        inputPassword.required = true;
        inputConfirmPassword.required = true;
        inputFirstname.required = true;
        inputLastname.required = true;
        inputBirthday.required = true;
    }
}
/*function addEventHandlers() {
    // TODO: make work without jquery
    $('#Search').keyup(async () => {
        const search = $('#Search').val().toUpperCase();
        console.log(search);
        const response = await axios.get(`/api/users/${search}`);
        console.log(response.data.list);
        $('#Search').autocomplete({source: response.data.list});
    });
}*/


function init() {
    loadFields();
    // addEventHandlers();
}

window.onload = init;
