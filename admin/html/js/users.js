import { ListCRUD } from "./crud/listcrud.js";
import { userApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

window.crud = new ListCRUD({
    api: userApi,
    name: "User",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userName", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` },
        { mData: "registerTime", render: (data, type, row) => formatTime(data*1000) },
        { mData: "isAdmin", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} disabled >` },
        { mData: "id",
            render: (data, type, row) =>
                `<a href="/user/edit?id=${data}"><i class="fas fa-user-edit"></i></a>
                 <a href="#"><i class="fas fa-trash" onclick="window.crud.confirm(${data})"></i></a>`
        }
    ]
});

$(document).ready(() => window.crud.ready());