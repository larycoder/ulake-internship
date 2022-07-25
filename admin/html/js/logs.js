import { ListCRUD } from "./crud/listcrud.js";
import { userApi, logApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.crud = new ListCRUD({
    api: logApi,
    name: "Logs",
    nameField: "content",
    listFieldRenderer: [
        { data: "id" },
        { data: "userName" },
        { data: "content" },
        { data: "service" },
        { data: "tag" },
        { data: "timestamp", render: (data, type, row) => new Date(data).toLocaleDateString() }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "ownerId",
        targetId: "id",
        targetField: "userName"
    }
});

$(document).ready(() => window.crud.ready());