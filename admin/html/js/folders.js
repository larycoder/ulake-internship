import { ListCRUD } from "./crud/listcrud.js";
import { UserWrapper } from "./datawrapper/user.js";
import { FolderWrapper } from "./datawrapper/folder.js";
import { userApi, folderApi, fileApi } from "./api.js";

// data browser, first level is users
class DataCRUD extends ListCRUD {
    constructor() {
        super({
            api: userApi,
            listUrl: "/folders",
            name: "Folders",
            nameField: "name"
        });

        this.id = 0;        // default 0: everyone. negative: userid, positive: folderid
        this.path = [ ];    //

        this.userWrapper = new UserWrapper();
        this.folderWrapper = new FolderWrapper();
        this.dataWrapper = this.userWrapper;
        this.fields = ["id", "name", "type", "size", "action" ];
        $.fn.dataTable.ext.errMode = 'none';
    }

    /**
     * Update UI renderer whenver we change our data wrapper
     */
    setRenderers() {
        this.listFieldRenderer = this.fields.map(f => {return {
            data: f,
            render: this.dataWrapper.getRenderer(f)
        }});
    }

    /**
     * Get transformed data from the data wrapper
     */
    async fetch() {
        // select the correct adapter
        if (this.id == 0) this.dataWrapper = this.userWrapper;
        else this.dataWrapper = this.folderWrapper;

        const raw = await this.dataWrapper.fetch(this.id);
        this.data = this.dataWrapper.transform(raw);

        // prepare for detail() to render the table
        this.setRenderers();
    }

    recreateTable() {
        this.table.destroy();
        this.table = null;
        this.detail();
    }

    delete(id) {
        console.log("nah, not yet", id);
    }

    async clickUser(id) {
        if (id.indexOf("u") === 0) id = id.slice(1);
        this.id = -parseInt(id);
        console.log("Switching to folders of user", this.id);
        await this.fetch();
        this.recreateTable();
    }

    async clickFolder(id) {
        if (id.indexOf("F") === 0) id = id.slice(1);
        this.id = parseInt(id);
        console.log("Going into detail of folder", this.id);
        await this.fetch();
        this.recreateTable();
    }
}


window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());