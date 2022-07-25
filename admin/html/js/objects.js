import { ListCRUD } from "./crud/listcrud.js";
import { objectApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

function formatDate(date) {
    const year = date.getFullYear(),
            month = date.getMonth() + 1,
            day = date.getDate(),
            hour = date.getHours(),
            minute = date.getMinutes(),
            hourFormatted = hour % 24,
            minuteFormatted = minute < 10 ? "0" + minute : minute;
    return `${year}-${month}-${day} ${hourFormatted}:${minuteFormatted}`;
}

window.crud = new ListCRUD({
    api: objectApi,
    name: "Object",
    nameField: "cid",
    listFieldRenderer: [
        { data: "id" },
        { data: "cid", render: (data, type, row) => `<a href="#">${data}</a>` },
        { data: "createTime", render: (data, type, row) => formatDate(new Date(data)) },
        { data: "accessTime", render: (data, type, row) => formatDate(new Date(data)) },
        { data: "parentId" }
    ]
});

$(document).ready(() => window.crud.ready());
