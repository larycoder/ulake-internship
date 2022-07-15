import { ListCRUD } from "./crud/listcrud.js";
import { UserAdapter } from "./adapter/user.js";
import { FolderAdapter } from "./adapter/folder.js";
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
        this.userAdapter = new UserAdapter();
        this.folderAdapter = new FolderAdapter();
        this.dataAdapter = this.userAdapter;
        this.fields = ["id", "name", "type", "size", "action" ];

        this.breadcrumb = new Breadcrumb({
                name: "Users",
                click: "window.crud.click('u', '0', 'Users')",
            });
        this.breadcrumb.render();
        this.addModal = new AddFolderFileModal((folderName) => this.add(folderName));
        $.fn.dataTable.ext.errMode = 'none';
    }

    /**
     * Update UI renderer whenver we change our data adapter
     */
    setRenderers() {
        this.listFieldRenderer = this.fields.map(f => {return {
            data: f,
            render: this.dataAdapter.getRenderer(f)
        }});
    }

    /**
     * Get transformed data from the data adapter
     */
    async fetch() {
        this.startSpinner();
        // select the correct adapter
        if (this.type === "u" && this.id === 0) this.dataAdapter = this.userAdapter;
        else this.dataAdapter = this.folderAdapter;

        const raw = await this.dataAdapter.fetch(this.type, this.id);
        this.data = this.dataAdapter.transform(raw);

        // prepare for detail() to render the table
        this.setRenderers();
        this.stopSpinner();
    }

    recreateTable() {
        this.table.destroy();
        this.table = null;
        this.detail();
    }

    async delete(type, id) {
        if (type === "u") {
            console.log("nah, not yet", id);
        }
        else if (type === "F") {
            console.log("DELETE folder", id);
            const resp = await dashboardFolderApi.deleteOne(id);
            if (resp) {
                showToast("Error", "Cannot delete folder. There are still files inside.")
            }
            else {
                await this.fetch();
                this.recreateTable();
            }
        }
        else if (type === "f") {
            console.log("DELETE file", id);
            const resp = await dashboardFileApi.deleteOne(id);
            if (resp) {
                showToast("Error", "Cannot delete file. Permission error?");
            }
            else {
                await this.fetch();
                this.recreateTable();
            }
        }
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
            // yes, strip the remaining
            this.path = this.path.slice(0, itemPos + 1);
            this.breadcrumb.keep(itemPos);
        }
        else {
            // that's a new entry
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

    /**
     * Event handler for 'add' button click on modal
     * @param {String} folderName
     */
    async add(folderName) {
        const file = this.addModal.file;
        console.log("Folder name", folderName);
        console.log("Parent id", this.id);
        console.log("upload file", file);
        if (file) this.upload(file);
        else this.mkdir(folderName);
    }

    async upload(file) {
        const fileInfo = {
            name: file.name,
            mime: file.type,
            size: file.size,
            parent_id: 0
        }
        const ret = await adminApi.upload(fileInfo, file);
        if (ret && ret.id) {
            // good upload, close and refresh
            this.addModal.modal.modal("hide");
        }
    }

    async mkdir(folderName) {
        let id = this.id;
        let ownerId = null;
        if (this.type ==="u") {
            id = 0;
            ownerId = this.id;
        }
        const ret = await dashboardFolderApi.mkdir(folderName, id, ownerId);
        if (ret && ret.id) {
            // good mkdir, close and refresh
            this.addModal.modal.modal("hide");
            await this.fetch();
            this.recreateTable();
        }
    }

    addClick() {
        console.log("on add", this.id);
        if (this.id === 0) {
            showToast("Error", "Please select a user first");
        }
        else {
            this.addModal.modal.modal('show');
        }
    }
}


window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());