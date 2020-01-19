import axios from 'axios';
import { isSubdomain, getSubdomainTenant } from './util';

let stylesheet;

// source: https://css-tricks.com/converting-color-spaces-in-javascript/
function hexToRGB(h) {
    let r = 0, g = 0, b = 0;

    // 3 digits
    if (h.length == 4) {
        r = "0x" + h[1] + h[1];
        g = "0x" + h[2] + h[2];
        b = "0x" + h[3] + h[3];

    // 6 digits
    } else if (h.length == 7) {
        r = "0x" + h[1] + h[2];
        g = "0x" + h[3] + h[4];
        b = "0x" + h[5] + h[6];
    }

    return `rgb(${+r}, ${+g}, ${+b})`;
}

function changeStylesheetRule(stylesheet, selector, property, value) {
    selector = selector.toLowerCase();
    property = property.toLowerCase();
    value = value.toLowerCase();
    
    for(var i = 0; i < stylesheet.cssRules.length; i++) {
        const rule = stylesheet.cssRules[i];
        if(rule.selectorText === selector) {
            rule.style[property]  = value;
            return;
        }
    }
}

function replaceColors(colorScheme) {
    if (stylesheet == null) {
        // for some reason if you press the 'previous button' on the subdomain index and 
        // go back the index page an then press the 'next button' the stylesheet is null, 
        // re-initiating fixes this 
        init();
    }else{
        changeStylesheetRule(stylesheet, ".btn-color", "background-color", colorScheme.button);
        changeStylesheetRule(stylesheet, ".background-color", "background-color", colorScheme.body);
        changeStylesheetRule(stylesheet, ".btn-default", "background-color", colorScheme.button);
        changeStylesheetRule(stylesheet, ".reply-link", "color", colorScheme.button);
        changeStylesheetRule(stylesheet, ".socialbar-color", "background-color", colorScheme.socialBar);
        changeStylesheetRule(stylesheet, ".navbar-color", "background-color", colorScheme.navBar);
        changeStylesheetRule(stylesheet, ".banner-color", "background-color", colorScheme.banner);
        changeStylesheetRule(stylesheet, "body", "background-color", colorScheme.body);
    }

    
}

async function getColorScheme(tenant) {
    try {
        const response = await axios.get(`/api/platforms/colorscheme/${tenant}`);

        const {
            socialBarColor,
            navBarColor,
            bannerColor,
            buttonColor,
            buttonTextColor,
            textColor,
            bodyColor
        } = response.data;

        return {
            socialBar: hexToRGB(socialBarColor),
            navBar: hexToRGB(navBarColor),
            banner: hexToRGB(bannerColor),
            button: hexToRGB(buttonColor),
            buttonText: hexToRGB(buttonTextColor),
            text: hexToRGB(textColor),
            body: hexToRGB(bodyColor)
        };
    } catch (err) {
        console.error('error while retrieving color scheme');
    }
}

async function init() {
    if (isSubdomain()) {
        const tenant = getSubdomainTenant();
        const newColorScheme = await getColorScheme(tenant);
        stylesheet = document.styleSheets[1];
        replaceColors(newColorScheme);
    }
}

init();