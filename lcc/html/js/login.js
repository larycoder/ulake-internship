import { authApi, userApi } from "http://common.dev.ulake.sontg.net/js/api.js";

function showError(msg) {
    $(".text-danger").text(msg);
}

window.login = async () => {
    const uid = document.querySelector("#uid").value,
          pwd = document.querySelector("#pwd").value;

    console.log(`Logging in as ${uid}, ${pwd}, domain ${getBaseDomain()}`);
    const button = $("form button");
    button.text("").append($(`<i class="fas fa-spinner fa-spin"></i>`));
    const data = await authApi.login(uid, pwd);
    if (data && Object.keys(data).length > 0) {
        console.log(`Login ok, token=${data}`);
        setToken(data);
        const name = await userApi.getName(getUid());
        setUserName(name);
        window.location = "/";
    }
    else {
        showError("Sai tên đăng nhập hoặc mật khẩu.");
        button.text("Đăng nhập");
    }
}

window.ready = function () {
    if (getToken()) {
        window.location = "/";
    }
}

$(document).ready(window.ready);
