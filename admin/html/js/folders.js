import { ListCRUD } from "./crud/listcrud.js";
import { userApi, folderApi } from "./api.js";

window.crud = new ListCRUD({
    api: folderApi,
    listUrl: "/folders",
    name: "Folder",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "name" },
        { mData: "userName" }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "ownerId",
        targetId: "id",
        targetField: "userName"
    }
});

$(document).ready(() => window.crud.ready());