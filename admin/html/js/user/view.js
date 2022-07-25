import { ViewCRUD } from '../crud/viewcrud.js';
import { userApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

const crud = new ViewCRUD({
    api: userApi,
    name: "User",
    nameField: "userName",
    listUrl: "/users",
    hidden: "department, failedLogins, groups"
});

$(document).ready(() => crud.ready());
