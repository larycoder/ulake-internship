import { EditCRUD } from '../crud/editcrud.js';
import { userApi } from "../api.js";

const crud = new EditCRUD({
    api: userApi,
    listUrl: "/users",
    name: "User",
    nameField: "userName",
    hidden: "department, failedLogins, groups",
    readonly: "id, registerTime, userName, isAdmin"
});

$(document).ready(() => crud.ready());