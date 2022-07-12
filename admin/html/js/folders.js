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

    async click(id, name) {
        if (id[0] >= '0' && id[0] <= '9') {
            // not a valid id. should start with 'u', 'f', or 'F'
            return;
        }
        const type = id[0];
        id = id.slice(1);
        this.id = -parseInt(id);
        this.updateBreadcrumb(type, name);

        await this.fetch();
        this.recreateTable();
    }


    updateBreadcrumb(type, name) {
        const bclist = $("ol[class=breadcrumb]");
        if (parseInt(this.id) === 0) {
            // remove all sub breadcrumbs
            bclist.find('li:gt(0)').remove();
        }
        else {
            // add a new one
            const bc = $(`<li class="breadcrumb-item"><a href="#" onclick="window.crud.click('${type}${this.id}')">${name}</a></li>`)
            bclist.append(bc);
        }
    }
}


window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());