import { ListCRUD } from "./crud/listcrud.js";
import { userApi, tableApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.crud = new ListCRUD({
    api: tableApi,
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { data: "id" },
        { data: "userName" },
        { data: "name", render: (data, type, row) => `<a href="/table/view?id=${row.id}">${data}</a>` },
        { data: "format" },
        { data: "creationTime", render: (data, type, row) => formatTime(data) },
        { data: "id",
            render: (data, type, row) => `<a href="#"><i class="fas fa-trash" onclick="window.crud.listDeleteItem(${data})"></i></a>`
        }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "ownerId",
        targetId: "id",
        targetField: "userName"
    }
});

$(document).ready(() => window.crud.ready());
