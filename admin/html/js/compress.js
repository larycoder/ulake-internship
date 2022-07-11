import { ListCRUD } from "./crud/listcrud.js";
import { userApi, compressApi } from "./api.js";

window.crud = new ListCRUD({
    api: compressApi,
    listUrl: "/compress",
    name: "Compress Requests",
    nameField: "id",
    listFieldRenderer: [
        { mData: "id", render: (data, type, row) => `<a href="/compress/view?id=${data}">${data}</a>` },
        { mData: "userName" },
        { mData: "timestamp", render: (data, type, row) => data ? new Date(data).toLocaleDateString() : "In progress" },
        { mData: "finishedTime", render: (data, type, row) => data ? new Date(data).toLocaleDateString() : "In progress" },
        { mData: "id",
            render: (data, type, row) =>
                row.finishedTime <= 0? "" : `<a href="#"><i class="fas fa-stop" onclick="stopJob(${data})"></i></a>`
        }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "userId",
        targetId: "id",
        targetField: "userName"
    }
});

function stopJob(data) {
    window.crud.listDeleteItem(data);
}

$(document).ready(() => window.crud.ready());