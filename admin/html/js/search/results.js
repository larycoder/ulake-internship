import { searchApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

function renderUser(data) {
    $('#user-table').DataTable(  {
        data: data,
        paging: false,
        columns: [
            { data: "id" },
            { data: "userName", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` },
            { data: "firstName" },
            { data: "lastName" },
            { data: "registerTime", render: (data, type, row) => formatTime(data*1000) },
            { data: "isAdmin", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} disabled >` }
        ],
        order: []
    });
}

function renderFile(data) {
    $('#file-table').DataTable(  {
        data: data,
        paging: false,
        columns: [
            { data: "id" },
            { data: "name", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` },
            { data: "mime" },
            { data: "size" }
        ],
        order: []
    });
}

async function fetchUser(query) {
    return await searchApi.user(query);
}

async function fetchFile(query) {
    return await searchApi.file(query);
}

async function resultReady() {
    const param = parseParam("query", "/");
    if (isEmpty(param.query)) window.location = "/";
    const query = param.query.split(",").map(q => q.trim());
    const userResult = await fetchUser({
        "user.keywords": {
            values: query,
            fields: [ "userName", "firstName", "lastName" ]
        }
    });
    console.log(userResult);
    // const fileResult = await fetchFile({
    //     "file.keywords": {
    //         values: query,
    //         fields: [ "name", "mime" ]
    //     }
    // });
    renderUser(userResult);
    // renderFile(fileResult);

    $("i.fa-spin").remove();
}

$(document).ready(resultReady);
