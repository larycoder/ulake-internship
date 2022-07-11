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
                { mData: "type" },
                { mData: "id",
                   render: (data, type, row) => `<a href="#"><i class="fas fa-trash" onclick="window.crud.delete('${data}')"></i></a>`
                }
            ]});
        this.folderId = 0;  // default at root
        this.folderPath = [ ];
        this.fileApi = fileApi;
        $.fn.dataTable.ext.errMode = 'none';
    }

    async fetch() {
        await super.fetch();
        this.data.map(e => { e.name = e.userName; e.id = `u${e.id}`; e.type = "User"; return e;});

    }

    delete(id) {
        console.log("nah, not yet", id);
    }
}

window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());