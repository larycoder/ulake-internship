import { EditCRUD } from '../crud/editcrud.js';
import { groupApi } from "../api.js";

const crud = new EditCRUD({
    api: groupApi,
    listUrl: "/groups",
    name: "Group",
    nameField: "name",
    readonly: "id",
    hidden: "users"
});

$(document).ready(() => crud.ready());