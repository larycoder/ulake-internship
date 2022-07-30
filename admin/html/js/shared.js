import { ListCRUD } from "./crud/listcrud.js";
import { aclApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.crud = new ListCRUD({
    api: aclApi,
    name: "Data",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userId", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` },
        { mData: "objectId" },
        { mData: "type", render: (data, type, row) => `<input type="checkbox" ${data === "file"? "checked" : ""} disabled >` },
        { mData: "id",
            render: (data, type, row) =>
                `<a href="/user/edit?id=${data}"><i class="fas fa-user-edit"></i></a>
                 <a href="#"><i class="fas fa-trash" onclick="window.crud.confirm(${data})"></i></a>`
        }
    ]
});

$(document).ready(() => window.crud.ready());
