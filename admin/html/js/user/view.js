import { ViewCRUD } from '../crud/viewcrud.js';
import { userApi } from "../api.js";

const crud = new ViewCRUD({
    api: userApi,
    listUrl: "/users",
    name: "User",
    nameField: "userName",
    hidden: "department, failedLogins, groups"
});

$(document).ready(() => crud.ready());