// check for valid token before proceeding to main
function indexReady() {
    if (getToken() === null) {
        window.location = "/login";
    }
}