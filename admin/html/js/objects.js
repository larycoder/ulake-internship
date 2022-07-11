import { ListCRUD } from "./crud/listcrud.js";
import { objectApi } from "./api.js";

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

const crud = new ListCRUD({
    api: objectApi,
    name: "Object",
    nameField: "cid",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "cid", render: (data, type, row) => `<a href="#">${data}</a>` },
        { mData: "createTime", render: (data, type, row) => formatDate(new Date(data)) },
        { mData: "accessTime", render: (data, type, row) => formatDate(new Date(data)) },
        { mData: "parentId" }
    ]
});

$(document).ready(() => crud.ready());