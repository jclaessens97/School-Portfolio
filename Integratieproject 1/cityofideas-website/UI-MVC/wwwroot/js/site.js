// Sitewide javascript
// Import babel polyfill to support async/await and spread syntax
import '@babel/polyfill'; // eslint-disable-line

// CSS dependencies
import 'bootstrap/dist/css/bootstrap.css';

// Custom css imports
import '../scss/site.scss';

// Fontawesome 5
import '@fortawesome/fontawesome-free/js/all';

// Custom colorScheme loader
import '../js/colorSchemeLoader';

// custom popup messages
import * as toastr from 'toastr';
toastr.options.timeOut = 9999;



// Search in navbar
const searchInput = document.getElementById('searchInputNav');
searchInput.addEventListener('keypress', (event) => {
    if (event.keyCode == 13) {
        window.location.href = `/search?qry=${event.target.value}`;
    }
});

const searchButton = document.getElementById('searchButtonNav');
searchButton.addEventListener('click', () => {
    window.location.href = `/search?qry=${searchInput.value}`;
});

// pre-loader
function loader() {
    toastr.clear();
    setTimeout(function () {
        if ($('#pre-loader').length > 0) {
            $('#pre-loader').removeClass('show');
        }
    }, 1);    
    var url_string = window.location.href;
    var url = new URL(url_string);
    var c = url.searchParams.get("registerSuccess");
    console.log(c);
    if (c === "true"){
        toastr.success('Succesvol geregistreerd');
        toastr.info('Wij sturen u een mail zodat uw account bevestigd kan worden. Na deze bevestiging kan u zich inloggen.');

    } 
    
}

loader();