import { ListCRUD } from "./crud/listcrud.js";
import { userApi, folderApi } from "./api.js";

class DataCRUD extends ListCRUD {
    constructor() {
        super({
            api: folderApi,
            listUrl: "/folders",
            name: "Folders",
            nameField: "name",
            listFieldRenderer: [
                { mData: "id" },
                { mData: "name" },
                { mData: "ownerId" }
            ]});

    }
}

window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());