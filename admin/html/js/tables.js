import { ListCRUD } from "./crud/listcrud.js";
import { userApi, tableApi } from "./api.js";

window.crud = new ListCRUD({
    api: tableApi,
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userName" },
        { mData: "name", render: (data, type, row) => `<a href="/table/view?id=${row.id}">${data}</a>` },
        { mData: "format" },
        { mData: "creationTime", render: (data, type, row) => new Date(data).toLocaleDateString() },
        { mData: "id",
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