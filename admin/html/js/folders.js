import { ListCRUD } from "./crud/listcrud.js";
import { UserWrapper } from "./datawrapper/user.js";
import { FolderWrapper } from "./datawrapper/folder.js";
import { userApi, folderApi, fileApi } from "./api.js";
import { Breadcrumb } from "./breadcrumb.js"

// data browser, first level is users
class DataCRUD extends ListCRUD {
    constructor() {
        super({
            api: userApi,
            listUrl: "/folders",
            name: "Folders",
            nameField: "name"
        });

        this.id = 0;        // 0: everyone. else: userid or folderid
        this.type = "u";    // u, F: user or folder
        this.path = [ ];    //
        this.userWrapper = new UserWrapper();
        this.folderWrapper = new FolderWrapper();
        this.dataWrapper = this.userWrapper;
        this.fields = ["id", "name", "type", "size", "action" ];

        this.breadcrumb = new Breadcrumb({
                name: "Users",
                click: "window.crud.click('u', '0', 'Users')",
                data: 0
            });
        this.breadcrumb.render();
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
        this.startSpinner();
        // select the correct adapter
        if (this.type === "u" && this.id === 0) this.dataWrapper = this.userWrapper;
        else this.dataWrapper = this.folderWrapper;

        const raw = await this.dataWrapper.fetch(this.type, this.id);
        this.data = this.dataWrapper.transform(raw);

        // prepare for detail() to render the table
        this.setRenderers();
        this.stopSpinner();
    }

    recreateTable() {
        this.table.destroy();
        this.table = null;
        this.detail();
    }

    delete(id) {
        console.log("nah, not yet", id);
    }

    async click(type, id, name) {
        this.id = Math.abs(parseInt(id));
        this.type = type;
        console.log(`clicked on ${type} ${this.id}`);


        this.path.push({
            type: this.type,
            id: this.id
        })

        this.breadcrumb.append({
            name: name,
            click: `window.crud.click('${this.type}', '${this.id}', '${this.name}')`,
        })

        await this.fetch();
        this.recreateTable();

        console.log(this.path);
    }

    startSpinner() {
        const bclist = $("ol[class=breadcrumb]");
        bclist.append('<li class="breadcrumb-item"><i class="fas fa-spinner fa-spin"></i></li>');
    }

    stopSpinner() {
        const bclist = $("ol[class=breadcrumb]");
        bclist.find('i[class*="fa-spinner"]').parent().remove();
    }
}


window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());