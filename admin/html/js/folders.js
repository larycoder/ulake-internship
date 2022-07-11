import { ListCRUD } from "./crud/listcrud.js";
import { UserWrapper } from "./datawrapper/user.js";
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
                { mData: "size" },
                { mData: "id",
                   render: (data, type, row) => `<a href="#"><i class="fas fa-trash" onclick="window.crud.delete('${data}')"></i></a>`
                }
            ]});
        this.id = 0;        // default 0: everyone. negative: userid, positive: folderid
        this.path = [ ];    //

        this.userWrapper = new UserWrapper();
        this.dataWrapper = this.userWrapper;
        $.fn.dataTable.ext.errMode = 'none';
    }

    async fetch() {
        const raw = await this.dataWrapper.fetch();
        this.data = this.dataWrapper.transform(raw);
    }

    delete(id) {
        console.log("nah, not yet", id);
    }
}

window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());