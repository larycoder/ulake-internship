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

function getLogUrl() {
    return window.location.protocol + "//log." + getBaseDomain();
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

/**
 *
 * @returns Logged in user id
 */
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

/**
 *
 * @returns Groups that logged in user belongs
 */
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

/**
 * Ajax function, with token if any
 */
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
    return sessionStorage.getItem("username");
}

/**
 * Convert an object to an array of key/value pairs
 * @param {object} object to convert
 * @param {array} field exclusions
 * @returns
 */
 function toTable(object, excludes) {
    var table = [];
    for (var key in object) {
        if (!excludes || (excludes && excludes.indexOf(key) < 0 )) {
            table.push( {
                key: key,
                value: object[key]
            });
        }
    }
    return table;
}

/**
 * Show a bootstrap modal
 * @param {string} title of the modal
 * @param {string} content of the modal
 * @param {func} onOk callback function when user pressed on OK
 */
function showModal(title, content, onOk) {
    $("#confirm-title").text(title);
    $("#confirm-content").text(content);
    if (onOk) {
        $("#confirm-ok").off("click");
        $("#confirm-ok").on("click", onOk);
    }
    else {
        $("#confirm-ok").off("click");
        $("#confirm-ok").on("click", () => {
            $("#confirm-modal").modal("toggle");
        });
    }
    $('#confirm-modal').modal();
}

/**
 *
 * @param {string} requiredParams Required search parameter
 * @param {string} defaultLocation If any of the required params do not exist, redirect to this location
 * @returns
 */
function parseParam(requiredParams, defaultLocation) {
    let urlParams = new URLSearchParams(location.search);
    if (requiredParams) {
        if (!Array.isArray(requiredParams)) requiredParams = [ requiredParams ];
        for (const requiredParam of requiredParams) {
            if (!urlParams.has(requiredParam)) {
                window.location = defaultLocation;
                return;
            }
        }
    }
    let ret = {};
    for (const [key, value] of urlParams) {
        ret[key] = value;
    }
    return ret;
}

function isEmpty(o) {
    return o === undefined ||
        o === null ||
        typeof o === 'undefined' ||
        (typeof o === "string" && (o === "" || o === "null"));
}