import { authApi, userApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.ready = function () {
    if (!getToken()) {
        window.location = "/";
    }
    else {
        setToken(null);
        window.setTimeout(function () {
            window.location = "/";
        }, 3000);
    }
}

$(document).ready(window.ready);
