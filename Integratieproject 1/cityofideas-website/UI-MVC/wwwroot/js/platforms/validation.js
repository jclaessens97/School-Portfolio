import axios from 'axios';

const ERR_REQUIRED = 'Dit veld is verplicht.';
const ERR_TENANT_EXISTS = 'De tenant voor dit platform bestaat al.';

const nameInput = document.getElementById('Name');
const tenantInput = document.getElementById('Tenant');
const logoInput = document.getElementById('logo-input');
const descriptionInput = document.getElementById('Description');

const nameError = document.getElementById('name-error');
const tenantError = document.getElementById('tenant-error');
const logoError = document.getElementById('logo-error');
const descriptionError = document.getElementById('description-error');

function validateName() {
    if (!nameInput.validity.valid) {
        nameError.innerText = ERR_REQUIRED;
        nameError.classList.add('active');
        return false;
    }

    return true;
}

function validateTenant() {
    if (!tenantInput.validity.valid) {
        tenantError.innerText = ERR_REQUIRED;
        tenantError.classList.add('active');
        return false;
    }

    return true;
}

function validateLogo(editting) {
    if (editting) return true;

    if (!logoInput.validity.valid) {
        logoError.innerText = ERR_REQUIRED;
        logoError.classList.add('active');
        return false;
    }

    return true;
}

function validateDescription() {
    if (!descriptionInput.validity.valid) {
        descriptionError.innerText = ERR_REQUIRED;
        descriptionError.classList.add('active');
        return false;
    }

    return true;
}

export function init() {
    nameInput.addEventListener('input', () => {
        nameError.innerText = '';
        nameError.classList.remove('active');
    });

    tenantInput.addEventListener('input', () => {
        tenantError.innerText = '';
        tenantError.classList.remove('active');
    });

    logoInput.addEventListener('input', () => {
        logoError.innerText = '';
        logoError.classList.remove('active');
    });

    descriptionInput.addEventListener('input', () => {
        descriptionError.innerText = '';
        descriptionError.classList.remove('active');
    });
}

export function isValid(editting = false) {
    let valid = true;

    if (!validateName()) {
        valid = false;
    }

    if (!validateTenant()) {
        valid = false;
    }

    if (!validateLogo(editting)) {
        valid = false;
    }

    if (!validateDescription()) {
        valid = false;
    }

    return valid;
}

export async function checkIfTenantExists(event) {
    const { value } = event.target;

    try {
        const response = await axios.get(`/api/platforms/exists?tenant=${value}`);
        if (response.data.exists === true) {
            tenantError.innerText = ERR_TENANT_EXISTS;
            tenantError.classList.add('active');
            return true;
        }
        tenantError.innerText = '';
        tenantError.classList.remove('active');
        return false;
    } catch (err) {
        console.error(err);
        return true;
    }
}
