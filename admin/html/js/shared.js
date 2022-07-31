import { ListCRUD } from "./crud/listcrud.js";
import { fileApi, folderApi, aclApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

function uniq(value, index, self) {
    return self.indexOf(value) === index;
  }

window.crud = new ListCRUD({
    api: aclApi,
    name: "Data",
    listFieldRenderer: [
        { data: "userId", render: (data, type, row) => `<a href="/user/view?id=${data}">${data}</a>` },
        { data: "type", render: (data, type, row) => `<input type="checkbox" ${data === "file"? "checked" : ""} disabled >` },
        { data: "name", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` }
    ],
    joins: [
        {   // join files
            apiMethod: async (keys) => {
                // get unique ids of files only
                const ids = keys.filter(k => k.type == "file")
                                .map(k => k.id)
                                .filter(uniq)
                                .join(",");
                if (!isEmpty(ids)) return await fileApi.many(ids);
                else return [];
            },
            fkMapper: (e) => { return {id : e.objectId, type : e.type} },   // fk is a combination of (type,id)
            fkField: "objectId",
            targetId: "id",
            targetField: "name"
        },
        {   // join folders
            apiMethod: async (keys) => {
                // get unique ids of folders only
                const ids = keys.filter(k => k.type == "folder")
                                .map(k => k.id)
                                .filter(uniq)
                                .join(",");
                if (!isEmpty(ids)) return await folderApi.many(ids);
                else return [];
            },
            fkMapper: (e) => { return {id : e.objectId, type : e.type}},   // fk is a combination of (type,id)
            fkField: "objectId",    // for matchng with other table
            targetId: "id",
            targetField: "name"
        }
    ]
});


$(document).ready(() => window.crud.ready());
