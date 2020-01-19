import { HOSTNAMES } from './constants';

export function getBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result);
        reader.onerror = error => reject(error);
    });
}

export function generateId() {
    return `_${Math.random().toString(36).substr(2, 9)}`;
}

export function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

export function CreateModal(domModal, closeEasy = true) {
    const domElementModal = domModal;
    const modal = {};
    domElementModal.innerText = '';
    domElementModal.className = 'modal-2 ';
    modal.modal = domElementModal;

    const domModalContent = document.createElement('div');
    domModalContent.className = 'modal-content-2';
    domElementModal.appendChild(domModalContent);
    modal.content = domModalContent;

    const domModalHeader = document.createElement('div');
    domModalHeader.className = 'modal-header-2';
    modal.header = domModalHeader;
    const domModalTitle = document.createElement('h2');
    modal.title = domModalTitle;
    const domClose = document.createElement('span');
    modal.close = domClose;
    domClose.className = 'close';
    domClose.innerHTML = '&times;';
    domModalHeader.appendChild(domModalTitle);
    domModalHeader.appendChild(domClose);

    const domModalBody = document.createElement('div');
    domModalBody.className = 'modal-body-2';
    modal.body = domModalBody;

    domModalContent.appendChild(domModalHeader);
    domModalContent.appendChild(domModalBody);


    domElementModal.style.display = 'flex';

    domClose.addEventListener('click', () => {
        domElementModal.style.display = 'none';
    });

    window.onclick = (event) => {
        if (event.target === domElementModal && closeEasy) {
            domElementModal.style.display = 'none';
        }
    };

    return modal;
}

export const ICONS = Object.freeze(
    {
        UP: 'fas fa-arrow-up',
        DOWN: 'fas fa-arrow-down',
        DELETE: 'fas fa-trash-alt',
        MOVE: 'fas fa-arrows-alt',
        FLAGACTIVE: 'fas fa-flag',
        FLAGINACTIVE: 'far fa-flag',
        HIDE: 'fas fa-eye-slash',
        UNHIDE: 'fas fa-eye',
        ADD: 'fas fa-plus-square'
    },
);

// Types: 0: up, 1: down, 2: delete, 3: move, 4: flag-active, 5: flag-inactive
export function createButton(icon) {
    const className = icon;
    const btn = document.createElement('a');
    btn.className = 'icon-btn';
    const ico = document.createElement('i');
    ico.className = className;
    btn.appendChild(ico);
    return btn;
}

export function getSubdomainTenant() {
    return window.location.hostname.split('.')[0];
}

export function isSubdomain() {
    const tenant = getSubdomainTenant();

    if (tenant === 'www' || HOSTNAMES.includes(tenant)) {
        return false;
    } else {
        return true;
    }
}

export function formatDate(date) {
    const d = new Date(date);

    // TODO: padding 0's

    const dateFormat = [
        d.getDate(),
        d.getMonth() + 1,
        d.getFullYear(),
    ].join('/');

    const timeFormat = [
        d.getHours(),
        d.getMinutes(),
        d.getSeconds(),
    ].join(':');

    return `${dateFormat} ${timeFormat}`;
}

export const TypesEnum = Object.freeze({
    OPEN: 0,
    IMAGE: 1,
    VIDEO: 2,
    SINGLECHOICE: 3,
    MULTIPLECHOICE: 4,
    LOCATION: 5,
    DROPDOWN: 6,
    STATEMENT: 7,
});

export const TypesString = ['Open vraag', 'Afbeelding', 'Video', 'Meerkeuze', 'Selectievakje(s)', 'Locatie', 'Dropdown','Stelling'];

/**
 * Parses a string to a date
 * @param {string} string in the format of dd/mm/yyyy
 * @returns date object
 */
export function parseDate(dateString) {
    const parts = dateString.split('/');
    return new Date(
        parseInt(parts[2], 10),
        parseInt(parts[1], 10) - 1,
        parseInt(parts[0], 10),
    );
}


// Credits to https://muffinman.io/javascript-time-ago-function/ //
const MONTH_NAMES = [
    'Januari', 'Februari', 'Maart', 'April', 'Mei', 'Juni',
    'Juli', 'Augustus', 'September', 'October', 'November', 'December'
];


export function getFormattedDate(date, prefomattedDate = false, hideYear = false) {
    const day = date.getDate();
    const month = MONTH_NAMES[date.getMonth()];
    const year = date.getFullYear();
    const hours = date.getHours();
    let minutes = date.getMinutes();

    if (minutes < 10) {
        // Adding leading zero to minutes
        minutes = `0${ minutes }`;
    }

    if (prefomattedDate) {
        // Today at 10:20
        // Yesterday at 10:20
        return `${ prefomattedDate } om ${ hours }:${ minutes }`;
    }

    if (hideYear) {
        // 10. January at 10:20
        return `${ day } ${ month } om ${ hours }:${ minutes }`;
    }

    // 10. January 2017. at 10:20
    return `${ day }. ${ month } ${ year } om ${ hours }:${ minutes }`;
}


// --- Main function
export function timeAgo(dateParam) {
    if (!dateParam) {
        return null;
    }

    const date = typeof dateParam === 'object' ? dateParam : new Date(dateParam);
    const DAY_IN_MS = 86400000; // 24 * 60 * 60 * 1000
    const today = new Date();
    const yesterday = new Date(today - DAY_IN_MS);
    const seconds = Math.round((today - date) / 1000);
    const minutes = Math.round(seconds / 60);
    const isToday = today.toDateString() === date.toDateString();
    const isYesterday = yesterday.toDateString() === date.toDateString();
    const isThisYear = today.getFullYear() === date.getFullYear();


    if (seconds < 5) {
        return 'nu';
    } else if (seconds < 60) {
        return `${ seconds } seconden geleden`;
    } else if (seconds < 90) {
        return 'ongeveer een minuut geleden';
    } else if (minutes < 60) {
        return `${ minutes } minuten geleden`;
    } else if (isToday) {
        return getFormattedDate(date, 'Vandaag'); // Today at 10:20
    } else if (isYesterday) {
        return getFormattedDate(date, 'Gisteren'); // Yesterday at 10:20
    } else if (isThisYear) {
        return getFormattedDate(date, false, true); // 10. January at 10:20
    }

    return getFormattedDate(date); // 10. January 2017. at 10:20
}

// credits to: https://www.w3schools.com/js/js_cookies.asp
export function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

export function initLazyLoad(){
    var lazyloadImages = document.querySelectorAll(".lazy-image");
    var lazyloadThrottleTimeout;

    function lazyload () {
        if(lazyloadThrottleTimeout) {
            clearTimeout(lazyloadThrottleTimeout);
        }

        lazyloadThrottleTimeout = setTimeout(function() {
            var scrollTop = window.pageYOffset;
            lazyloadImages.forEach(function(img) {
                if(img.offsetTop < (window.innerHeight + scrollTop)) {
                    img.src = img.dataset.src;
                    img.classList.remove('loading');
                }
            });
            if(lazyloadImages.length == 0) {
                document.removeEventListener("scroll", lazyload);
                window.removeEventListener("resize", lazyload);
                window.removeEventListener("orientationChange", lazyload);
            }
        }, 20);
    }

    document.addEventListener("scroll", lazyload);
    window.addEventListener("resize", lazyload);
    window.addEventListener("orientationChange", lazyload);

    lazyload();
}

//
