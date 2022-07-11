import { ListCRUD } from "./crud/listcrud.js";
import { userApi, logApi } from "./api.js";

const crud = new ListCRUD({
    api: logApi,
    name: "Logs",
    nameField: "content",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userName" },
        { mData: "content" },
        { mData: "service" },
        { mData: "tag" },
        { mData: "timestamp", render: (data, type, row) => new Date(data).toLocaleDateString() }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "ownerId",
        targetId: "id",
        targetField: "userName"
    }
});

$(document).ready(() => crud.ready());