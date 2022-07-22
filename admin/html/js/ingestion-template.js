import { ListCRUD } from "./crud/listcrud.js";
import { userApi, ingestionTemplateApi } from "./api.js";

window.crud = new ListCRUD({
    api: ingestionTemplateApi,
    name: "Data collection templates",
    nameField: "id",
    listFieldRenderer: [
        { data: "id" },
        { data: "userName" },
        { data: "description", render: (data, type, row) => `<a href="/ingesttemplate/view?id=${data}">${data}</a>` },
        { data: "updatedTime", render: (data, type, row) => new Date(data).toLocaleDateString() },
        { data: "id",
            render: (data, type, row) =>
                row.finishedTime <= 0? "" : `<a href="#"><i class="fas fa-trash" /i></a>`
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