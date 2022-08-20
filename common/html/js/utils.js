function getBaseDomain() {
    let host = window.location.hostname;
    let parts = host.split(".");
    if (parts.length <= 3 || parts[0] === "ulake" || parts[0] === "dev") {
        // level 3 domains, such as ulake.usth.edu.vn
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

function getDashboardUrl() {
    return window.location.protocol + "//dashboard." + getBaseDomain();
}

function getAdminUrl() {
    return window.location.protocol + "//admin." + getBaseDomain();
}

function getExtractUrl() {
    return window.location.protocol + "//extract." + getBaseDomain();
}

function getIngestionUrl() {
    return window.location.protocol + "//ingest." + getBaseDomain();
}

function getSearchUrl() {
    return window.location.protocol + "//search." + getBaseDomain();
}

function getIrUrl() {
    return window.location.protocol + "//ir." + getBaseDomain();
}

function getAclUrl() {
    return window.location.protocol + "//acl." + getBaseDomain();
}

function getToken() {
    // todo: don't use session storage to prevent XSS attacks
    return sessionStorage.getItem('jwt');
}

function setToken(token) {
    if (token) {
        sessionStorage.setItem("jwt", token);
    }
    else {
        sessionStorage.removeItem("jwt");
    }
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
 * @param {func} transformers transform the object to another value
 * @returns
 */
function toTable(object, excludes, transformers) {
    var table = [];
    for (var key in object) {
        if (!excludes || (excludes && excludes.indexOf(key) < 0)) {
            table.push({
                key: key,
                value: (transformers && transformers[key]) ? transformers[key](object[key]) : object[key]
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
    const body = toast.find(".toast-body");
    if (content instanceof jQuery) body.empty().append(content);
    else toast.find(".toast-body").empty().text(content);
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
        if (!Array.isArray(requiredParams)) requiredParams = [requiredParams];
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

function humanFileSize(bytes, si = true, dp = 1) {
    const thresh = si ? 1000 : 1024;

    if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
    }

    const units = si
        ? ['KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
        : ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
    let u = -1;
    const r = 10 ** dp;

    do {
        bytes /= thresh;
        ++u;
    } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);
    return bytes.toFixed(dp) + ' ' + units[u];
}

function nstr(n, digits) {
    return String(n).padStart(digits, "0");
}

function formatTime(timestamp) {
    const d = new Date(timestamp);
    return `${nstr(d.getFullYear(), 4)}-${nstr(d.getMonth() + 1, 2)}-${nstr(d.getDate(), 2)} ${nstr(d.getHours(), 2)}:${nstr(d.getMinutes(), 2)}:${nstr(d.getSeconds(), 2)}`;
}

/**
 * Download a file from dashboard, using POST bearer body
 * @param {Long} id file id to download
 */
function downloadFile(id, name) {
    const url = `${getDashboardUrl()}/api/object/content/${name}`;
    var bearerInput = $('<input type="hidden" name="bearer">').val(getToken());
    var idInput = $('<input type="hidden" name="id">').val(id);
    $('<form method="post" target="_blank"></form>')
            .attr("action", url)
            .append(idInput)
            .append(bearerInput)
            .appendTo('body')
            .submit()
            .remove();
}

function mimeAwesome(mimeType) {
    // List of official MIME Types: http://www.iana.org/assignments/media-types/media-types.xhtml
    var classes = {
        // Media
        image: "fa-file-image",
        audio: "fa-file-audio",
        video: "fa-file-video",
        // Documents
        "application/pdf": "fa-file-pdf",
        "application/msword": "fa-file-word",
        "application/vnd.ms-word": "fa-file-word",
        "application/vnd.oasis.opendocument.text": "fa-file-word",
        "application/vnd.openxmlformats-officedocument.wordprocessingml": "fa-file-word",
        "application/vnd.ms-excel": "fa-file-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml": "fa-file-excel",
        "application/vnd.oasis.opendocument.spreadsheet": "fa-file-excel",
        "application/vnd.ms-powerpoint": "fa-file-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml": "fa-file-powerpoint",
        "application/vnd.oasis.opendocument.presentation": "fa-file-powerpoint",
        "text/plain": "fa-file-text",
        "text/html": "fa-file-code",
        "text/csv": "fa-file-csv",
        "application/json": "fa-file-code",
        // Archives
        "application/gzip": "fa-file-archive",
        "application/zip": "fa-file-archive"
    };

    for (var key in classes) {
        if (classes.hasOwnProperty(key)) {
            if (mimeType.search(key) === 0) {
                return classes[key];
            }
        } else {
            return "fa-file-o";
        }
    }
  }

  function isNumeric(str) {
    if (typeof str !== "string") return false // we only process strings!
    return !isNaN(str) && // use type coercion to parse the _entirety_ of the string (`parseFloat` alone does not do this)...
           !isNaN(parseFloat(str)) // ...and ensure strings of whitespace fail
  }