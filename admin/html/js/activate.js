import { userApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

function showMsg(msg) {
    if (typeof msg === "string") {
        $("#msg").empty().text(msg);
    }
    else {
        $("#msg").empty().append(msg);
    }
}

window.activate = async function () {
    const param = parseParam("code", "/login");
    const data = await userApi.activate(param.code);
    if (data && Object.keys(data).length > 0) {
        showMsg($(`<span>Activated succesfully!<br/>You will be redirected in 5 seconds...<span>`));
    }
    else {
        showMsg("Cannot activate at the moment.");
    }
}

$(document).ready(window.activate);
