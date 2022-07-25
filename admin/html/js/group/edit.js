import { EditCRUD } from '../crud/editcrud.js';
import { groupApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

const crud = new EditCRUD({
    api: groupApi,
    name: "Group",
    nameField: "name",
    listUrl: "/groups",
    readonly: "id",
    hidden: "users"
});

$(document).ready(() => crud.ready());
