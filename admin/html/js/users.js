import { ListCRUD } from "./crud/listcrud.js";
import { userApi } from "./api.js";

const userCrud = new ListCRUD({
    api: userApi,
    listUrl: "/users",
    name: "User",
    nameField: "userName",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userName", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` },
        { mData: "registerTime", render: (data, type, row) => new Date(data*1000).toLocaleDateString() },
        { mData: "isAdmin", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} disabled >` },
        { mData: "id",
            render: (data, type, row) =>
                `<a href="/user/edit?id=${data}"><i class="fas fa-user-edit"></i></a>
                 <a href="#"><i class="fas fa-trash" onclick="userCrud.listDeleteItem(${data})"></i></a>`
        }
    ]
});

// TODO: add user - usergroup - group relation
$(document).ready(() => userCrud.ready());