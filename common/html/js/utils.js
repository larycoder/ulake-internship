function getBaseDomain() {
    let host = window.location.hostname;
    let parts = host.split(".");
    if (parts.length <= 3) {
        // level 3 domains, such as ulake.sontg.net
        return host;
    }
    parts.splice(0, 1);
    return parts.join(".");
}

function getUserUrl() {
    return window.location.protocol + "//user." + getBaseDomain();
}

function getFolderUrl() {
    return window.location.protocol + "//folder." + getBaseDomain();
}

function getCoreUrl() {
    return window.location.protocol + "//core." + getBaseDomain();
}

function getTableUrl() {
    return window.location.protocol + "//table." + getBaseDomain();
}

function getToken() {
    // todo: don't use session storage to prevent XSS attacks
    return sessionStorage.getItem('jwt');
}

function setToken(token) {
    sessionStorage.setItem("jwt", token);
}

function getUid() {
    if (jwt_decode) {
        try {
            const jwt = jwt_decode(getToken());
            if (jwt.sub) {
                return jwt.sub;
            }
        }
        catch (e) {
        }
    }
    return -1;
}

function getGroups() {
    if (jwt_decode) {
        try {
            const jwt = jwt_decode(getToken());
            if (jwt.groups) {
                return jwt.groups;
            }
        }
        catch (e) {
        }
    }
    return [];
}

ajax = async function (param){
    let headers;
    if (param.headers) {
        headers = param.headers;
    }
    else {
        headers = {};
    }
    // headers["Authorization-Key"] = apiKey;
    let token = getToken();
    if (token) {
        headers.Authorization = "Bearer " + token;
    }
    let oldSuccess = param.success;
    param.headers = headers;
    if (!param.method) {
        param.method = "GET";
    }
    param.success = function (data) {
        if (!data) {
            window.alert("No response from server.");
            return;
        }
        if (data.code === 400) {
            window.alert(data.msg);
            return;
        }
        if (oldSuccess) {
            oldSuccess(data);
        }
    };
    param.error = function (error) {
        window.alert(`No response from server. Error: ${JSON.stringify(error)}`);
        if (error && error.status && error.status === 401) {
            window.location = "/login";
        }
    };
    return $.ajax(param);
};

function setUserName(userName) {
    sessionStorage.setItem("username", userName);
}

function getUserName() {
    sessionStorage.getItem("username");
}



function showModal(title, content) {
    $("#confirm-title").text(title);
    $("#confirm-content").text(content);
    $('#confirm-modal').modal();
}