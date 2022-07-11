import { EditCRUD } from '../crud/editcrud.js';
import { groupApi } from "../api.js";

const crud = new EditCRUD({
    api: groupApi,
    name: "Group",
    nameField: "name",
    listUrl: "/groups",
    readonly: "id",
    hidden: "users"
});

$(document).ready(() => crud.ready());