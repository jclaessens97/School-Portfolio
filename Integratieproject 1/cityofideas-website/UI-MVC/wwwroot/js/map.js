const loadGoogleMapsApi = require('load-google-maps-api');

const API_KEY = 'AIzaSyAcjLH-kOiE6TOc84tcRYRewC9bPiRKqvo';
class Map {
    static loadGoogleMapsApi() {
        return loadGoogleMapsApi({ key: API_KEY, libraries: ['places'] });
    }
}
export { Map };
