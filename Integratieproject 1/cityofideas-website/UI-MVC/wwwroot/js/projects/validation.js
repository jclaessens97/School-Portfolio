import { parseDate } from '../util';

const ERR_REQUIRED = 'Dit veld is verplicht.';
const ERR_PHASE = 'Gelieve alle fases in te vullen of de fases die je niet nodig hebt te verwijderen.';
const ERR_END_DATE = 'Eind datum moet na start datum liggen!';

const titleInput = document.getElementById('Title');
const goalInput = document.getElementById('Goal');
const logoInput = document.getElementById('logo-input');
const startDateInput = document.getElementById('StartDateInput');
const endDateInput = document.getElementById('EndDateInput');
const phaseTitleInputs = document.getElementsByClassName('phase-title');
const phaseDescriptionInputs = document.getElementsByClassName('phase-description');

const titleError = document.getElementById('title-error');
const goalError = document.getElementById('goal-error');
const logoError = document.getElementById('logo-error');
const startDateError = document.getElementById('startDate-error');
const endDateError = document.getElementById('endDate-error');
const phaseError = document.getElementById('phase-error');

function validateTitle() {
    
    if (!titleInput.validity.valid) {
        titleError.innerText = ERR_REQUIRED;
        titleError.classList.add('active');
        return false;
    }

    return true;
}

function validateGoal() {
    if (!goalInput.validity.valid) {
        goalError.innerText = ERR_REQUIRED;
        goalError.classList.add('active');
        return false;
    }

    return true;
}

function validateLogo() {
    if (!logoInput.validity.valid) {
        logoError.innerText = ERR_REQUIRED;
        logoError.classList.add('active');
        return false;
    }

    return true;
}

function validateDates() {
    let valid = true;

    if (startDateInput.value === '') {
        startDateError.innerText = ERR_REQUIRED;
        startDateError.classList.add('active');
        valid = false;
    }

    if (endDateInput.value === '') {
        endDateError.innerText = ERR_REQUIRED;
        endDateError.classList.add('active');
        valid = false;
    }

    if (!valid) return false;

    const startDate = parseDate(startDateInput.value);
    const endDate = parseDate(endDateInput.value);

    if (startDate > endDate) {
        endDateError.innerText = ERR_END_DATE;
        endDateError.classList.add('active');
        return false;
    }

    return true;
}

function validatePhases() {
    Array.from(phaseTitleInputs).forEach((titleInput) => {
        if (!titleInput.validity.valid) {
            phaseError.innerText = ERR_PHASE;
            phaseError.classList.add('active');
            return false;
        }
    });

    Array.from(phaseDescriptionInputs).forEach((descriptionInput) => {
        if (!descriptionInput.validity.valid) {
            phaseError.innerText = ERR_PHASE;
            phaseError.classList.add('active');
            return false;
        }
    });

    return true;
}

export function init() {
    titleInput.addEventListener('input', () => {
        titleError.innerText = '';
        titleError.classList.remove('active');
    });

    goalInput.addEventListener('input', () => {
        goalError.innerText = '';
        goalError.classList.remove('active');
    });

    logoInput.addEventListener('input', () => {
        logoError.innerText = '';
        logoError.classList.remove('active');
    });
}

export function isValid(isValidateLogo = true) {
    let valid = true;
    if (!validateTitle()) {
        valid = false;
    }

    if (!validateGoal()) {
        valid = false;
    }

    if (isValidateLogo) {
        if (!validateLogo()) {
            valid = false;
        }
    }

    if (!validateDates()) {
        valid = false;
    }

    if (!validatePhases()) {
        valid = false;
    }

    return valid;
}
