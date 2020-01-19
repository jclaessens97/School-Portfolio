// Admin javascript
// Import babel polyfill to support async/await and spread syntax
import '@babel/polyfill'; // eslint-disable-line

// CSS dependencies
import 'bootstrap/dist/css/bootstrap.css';

// Custom css imports
import '../../scss/admin/admin.scss';

// Fontawesome 5
import '@fortawesome/fontawesome-free/js/all';

// pre-loader
function loader() {
    setTimeout(function () {
        if ($('#pre-loader').length > 0) {
            $('#pre-loader').removeClass('show');
        }
    }, 1);
}

loader();
