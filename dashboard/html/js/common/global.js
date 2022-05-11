/**
 * this file declare global variable of site
 * this file should be included first in any html file
 */

var globalObject = {
    "tokenId": "ulake-query-token",
    "token": undefined, // load from storage
    "dataType": ["object", "file"], // list of type can be query
    "properties": {
        "object": ["id", "parentId", "accessTime", "createTime", "cid"],
        "file": ["object", "cid", "id", "mime", "name", "ownerId", "size"],
    }, // filter properties
    "operators": ["=", "<", ">", "like"] // filter operators
}

// load token from storage
function loadGlobal() {
    let token = localStorage.getItem(globalObject.tokenId);
    if (token != undefined && token.length > 0) {
        globalObject.token = token;
        document.cookie = "Authorization=" + token + ";path=/;";
    }
}
loadGlobal();

/**
 * open modal
 */
function openModal(modalId) {
    $("#" + modalId).modal("show");
}

/**
 * trigger modal
 */
function toggleModal(modalId) {
    $("#" + modalId).modal("toggle");
}

/**
 * hide modal
 */
function closeModal(modalId) {
    $("#" + modalId).modal("hide");
}
