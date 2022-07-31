import { ListCRUD } from "./crud/listcrud.js";
import { dashboardFileApi, dashboardFolderApi, aclApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.crud = new ListCRUD({
    api: aclApi,
    name: "Data",
    listFieldRenderer: [
        { mData: "userId", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` },
        { mData: "objectId" },
        { mData: "type", render: (data, type, row) => `<input type="checkbox" ${data === "file"? "checked" : ""} disabled >` },
        { mData: "id", render: (data, type, row) => "" }
    ],
    joins: [
        {   // join files
            apiMethod: (keys) => dashboardFileApi.many(keys.filter(k => k.type == "file").map(k => k.id)),
            fkMapper: (e) => { return {id : e.userId, type : e.type}},
            targetId: "id",
            targetField: "objectName"
        },
        {   // join folders
            apiMethod: (keys) => dashboardFolderApi.many(keys.filter(k => k.type == "folder").map(k => k.id)),
            fkMapper: (e) => { return {id : e.userId, type : e.type}},
            targetId: "id",
            targetField: "objectName"
        }
    ]
});


$(document).ready(() => window.crud.ready());
