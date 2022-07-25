import { ListCRUD } from "../crud/listcrud.js";
import { userApi, ingestionTemplateApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.crud = new ListCRUD({
    api: ingestionTemplateApi,
    name: "Data Collection Templates",
    nameField: "id",
    listFieldRenderer: [
        { data: "id" },
        { data: "userName" },
        { data: "description", render: (data, type, row) => `<a href="/ingestion/add?id=${row.id}">${data}</a>` },
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
