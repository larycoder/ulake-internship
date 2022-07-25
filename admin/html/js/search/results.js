import { ListCRUD } from "../crud/listcrud.js";
import { searchApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.crud = new ListCRUD({
    api: searchApi,
    name: "Query",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userName", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` },
        { mData: "registerTime", render: (data, type, row) => formatTime(data*1000) },
        { mData: "isAdmin", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} disabled >` }
    ]
});

function resultReady() {
    const param = parseParam("query", "/");
    if (isEmpty(param.query)) window.location = "/";
    console.log(`query is "${param.query}"`);
    searchApi.query = {
        userQuery: {
            keywords: [param.query]
        }
    };
    window.crud.ready();
}

$(document).ready(resultReady);
