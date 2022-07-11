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

function getCompressUrl() {
    return window.location.protocol + "//compress." + getBaseDomain();
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
    if (param.headers) headers = param.headers;
    else headers = {};
    const token = getToken();
    if (token && typeof token === 'string' && token !=='undefined') headers.Authorization = "Bearer " + token;
    param.headers = headers;
    if (!param.method) param.method = "GET";

    const resp = await fetch(param.url, param);
    console.log("ajax resp", resp);
    if (!resp.ok) {
        const error = resp.error;
        window.alert(`No response from server. Error: ${JSON.stringify(error)}`);
        if (error && error.status && error.status === 401) {
            window.location = "/login";
        }
    };
    return await resp.json();
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
 * Show a bootstrap toast
 * @param {string} title of the toast
 * @param {string} content of the toast
 * @param {int} delay
 */
 function showToast(title, content, delay) {
    const toast = $('.toast');
    toast.find("strong").text(title);
    toast.find(".toast-body").text(content);
    toast.toast('show');
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