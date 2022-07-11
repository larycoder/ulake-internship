import { CRUD } from "./crud/crud.js";
import { ListCRUD } from "./crud/listcrud.js";
import { DataWrapper } from "./data/wrapper.js";
import { userApi, folderApi, fileApi } from "./api.js";

// data browser, first level is users
class DataCRUD extends CRUD {
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
        this.folderApi = folderApi;
        $.fn.dataTable.ext.errMode = 'none';
    }

    async detail() {
        if (!this.table) {
            this.table = $('#table').DataTable(  {
                data: this.data,
                paging: true,
                aoColumns: this.listFieldRenderer
            });
        }
        else this.reloadTable(data);
    }

    async fetch() {
        this.data = await this.api.all();
        // this.data.map(e => { e.name = e.userName; e.id = `u${e.id}`; e.type = "User"; return e;});
    }

    delete(id) {
        console.log("nah, not yet", id);
    }
}

window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());