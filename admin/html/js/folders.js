import { ListCRUD } from "./crud/listcrud.js";
import { UserWrapper } from "./datawrapper/user.js";
import { FolderWrapper } from "./datawrapper/folder.js";
import { userApi, adminApi, dashboardFileApi, dashboardFolderApi } from "./api.js";
import { Breadcrumb } from "./breadcrumb.js";
import { AddFolderFileModal } from "./folders/add.js";

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
        this.path = [ { type: "u", id: 0 }];    //
        this.userWrapper = new UserWrapper();
        this.folderWrapper = new FolderWrapper();
        this.dataWrapper = this.userWrapper;
        this.fields = ["id", "name", "type", "size", "action" ];

        this.breadcrumb = new Breadcrumb({
                name: "Users",
                click: "window.crud.click('u', '0', 'Users')",
            });
        this.breadcrumb.render();
        this.addFolderModal = new AddFolderFileModal((folderName) => this.upload(folderName));
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

    /**
     * Handles most
     * @param {string} type 'u' for user, 'F' for folder
     * @param {string} id id of the user or folder
     * @param {*} name
     */
    async click(type, id, name) {
        this.id = Math.abs(parseInt(id));
        this.type = type;
        this.updateBreadcrumb(type, id, name);
        await this.fetch();
        this.recreateTable();
    }

    startSpinner() {
        const bclist = $("ol[class=breadcrumb]");
        bclist.append('<li class="breadcrumb-item"><i class="fas fa-spinner fa-spin"></i></li>');
    }

    stopSpinner() {
        const bclist = $("ol[class=breadcrumb]");
        bclist.find('i[class*="fa-spinner"]').parent().remove();
    }

    updateBreadcrumb(type, id, name) {
        // check if user clicked on an item on the existing path
        id = parseInt(id);
        const itemPos = this.path.findIndex(i => i.type === type && i.id === id);
        if (itemPos >= 0) {
            this.path = this.path.slice(0, itemPos + 1);
            this.breadcrumb.keep(itemPos);
        }
        else {
            this.path.push({
                type: this.type,
                id: this.id
            });

            this.breadcrumb.append({
                name: name,
                click: `window.crud.click('${this.type}', '${this.id}', '${this.name}')`,
            });
        }
        this.breadcrumb.render();
    }

    async upload(folderName) {
        const file = this.addFolderModal.file;
        console.log("Folder name", folderName);
        console.log("Parent id", this.id);
        console.log("upload file", file);
        if (file) {
            const fileInfo = {
                name: file.name,
                mime: file.type,
                size: file.size,
                parent_id: 0
            }
            const ret = await adminApi.upload(fileInfo, file);
            console.log("uploaded file", ret);
        }
        else {
            let id = this.id;
            if (this.type ==="u") id = 0;
            const ret = await dashboardFolderApi.mkdir(folderName, id);
            console.log("mkdir", ret);
        }
    }

    add() {
        console.log("on add", this.id);
        if (this.id === 0) {
            showToast("Error", "Please select a user first");
        }
        else {
            this.addFolderModal.modal.modal('show');
        }
    }
}


window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());