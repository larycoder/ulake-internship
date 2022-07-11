import { ListCRUD } from "./crud/listcrud.js";
import { userApi, compressApi } from "./api.js";

window.crud = new ListCRUD({
    api: compressApi,
    name: "Compress Requests",
    nameField: "id",
    listFieldRenderer: [
        { mData: "id", render: (data, type, row) => `<a href="/compress/view?id=${data}">${data}</a>` },
        { mData: "userName" },
        { mData: "timestamp", render: (data, type, row) => data ? new Date(data).toLocaleDateString() : "In progress" },
        { mData: "finishedTime", render: (data, type, row) => data ? new Date(data).toLocaleDateString() : "In progress" },
        { mData: "id",
            render: (data, type, row) =>
                row.finishedTime <= 0? "" : `<a href="#"><i class="fas fa-stop" onclick="window.stopJob(${data})"></i></a>`
        }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "userId",
        targetId: "id",
        targetField: "userName"
    }
});

window.stopJob = function(data) {
    window.crud.confirm(data);
}

$(document).ready(() => window.crud.ready());