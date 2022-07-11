import { EditCRUD } from '../crud/editcrud.js';
import { userApi } from "../api.js";

const crud = new EditCRUD({
    api: userApi,
    name: "User",
    nameField: "userName",
    listUrl: "/users",
    hidden: "department, failedLogins, groups",
    readonly: "id, registerTime, userName, isAdmin"
});

$(document).ready(() => crud.ready());