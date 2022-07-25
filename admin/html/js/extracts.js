import { ListCRUD } from "./crud/listcrud.js";
import { userApi, extractApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.crud = new ListCRUD({
    api: extractApi,
    name: "Extract Requests",
    nameField: "id",
    listFieldRenderer: [
        { data: "id", render: (data, type, row) => `<a href="/extract/view?id=${data}">${data}</a>` },
        { data: "userName" },
        { data: "timestamp", render: (data, type, row) => data ? formatTime(data) : "In progress" },
        { data: "finishedTime", render: (data, type, row) => data ? formatTime(data) : "In progress" },
        { data: "id",
            render: (data, type, row) =>
                row.finishedTime <= 0? `<a href="#"><i class="fas fa-stop" onclick="window.stopJob(${data})"></i></a>` : ""
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
