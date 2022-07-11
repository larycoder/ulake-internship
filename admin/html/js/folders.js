import { ListCRUD } from "./crud/listcrud.js";
import { userApi, folderApi, fileApi } from "./api.js";

// data browser, first level is users
class DataCRUD extends ListCRUD {
    constructor() {
        super({
            api: userApi,
            listUrl: "/folders",
            name: "Folders",
            nameField: "name",
            listFieldRenderer: [
                { mData: "id" },
                { mData: "name" },
                { mData: "ownerId" }
            ]},
            );
        this.folderId = 0;  // default at root
        this.folderPath = [ ];
        this.fileApi = fileApi;
        $.fn.dataTable.ext.errMode = 'none';
    }

    async fetch() {
        await super.fetch();
        if (this.folderId !== 0) {
            // fetch files for this directory
            await this.fileApi.many
        }
    }
}

window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());