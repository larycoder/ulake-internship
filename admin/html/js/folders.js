import { ListCRUD } from "./crud/listcrud.js";
import { UserAdapter } from "./adapter/user.js";
import { FolderAdapter } from "./adapter/folder.js";
import { fileApi, userApi, dashboardObjectApi, dashboardFileApi, dashboardFolderApi, extractApi, compressApi, irApi } from "http://common.dev.ulake.sontg.net/js/api.js";
import { Breadcrumb } from "./breadcrumb.js";
import { AddFolderFileModal } from "./folders/add.js";
import { RenameModal } from "./folders/rename.js";

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
        this.path = [ { type: "u", id: 0 } ];    //
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
        this.renameModal = new RenameModal((name) => this.rename(name));
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

    async detail() {
        await super.detail();
        if (this.table) {
            // allow ctrl-click to select multiple row
            this.table.select.style("os");
            if (this.type === "u" && this.id === 0)  {
                $("#compress").addClass("disabled");
            }
            else {
                this.table.off("select").on("select", () => $("#compress").removeClass("disabled"));
                this.table.off("deselect").on("deselect", () => $("#compress").addClass("disabled"));
            }
        }
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

    async deleteClick(type, id) {
        this.startSpinner();
        if (type === "u") {
            showToast("Error", "No, deleting user should not be here...");
        }
        else if (type === "F") {
            const resp = await dashboardFolderApi.deleteRecursive(id);
            if (resp) {
                showToast("Error", "Cannot delete folder. There are still files inside.")
            }
            else {
                await this.fetch();
                this.recreateTable();
            }
        }
        else if (type === "f") {
            const resp = await dashboardFileApi.deleteOne(id);
            if (resp) {
                showToast("Error", "Cannot delete file. Permission error?");
            }
            else {
                await this.fetch();
                this.recreateTable();
            }
        }
        this.stopSpinner();
    }

    /**
     * Handles most
     * @param {string} type 'u' for user, 'F' for folder
     * @param {string} id id of the user or folder
     * @param {*} name
     */
    async click(type, id, name) {
        console.log(`clicked on item type ${type}`);
        if (type === "F" || type === "u") {
            this.id = Math.abs(parseInt(id));
            this.type = type;
            this.updateBreadcrumb(type, id, name);
            await this.fetch();
            this.recreateTable();
        }
        else {
            downloadFile(id, name);
        }
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

    /**
     * Event handler for 'rename' button click on list
     */
    renameClick(type, id, name) {
        this.renameModal.id = id;
        this.renameModal.type = type;
        this.renameModal.oldName = name;
        this.renameModal.modal.modal('show');
    }

    /**
     * Event handler for 'extract' button click on list
     */
    async extractClick(type, id, name) {
        const req = {
            fileId: id,
            folderId: this.type === "u" ? 0: this.id
        }
        const ret = await extractApi.create(req);
        console.log(ret);
        if (ret && ret.id) {
            const retStart = await extractApi.start(ret.id);
            if (retStart && retStart.id) {
                showToast("Info", $('<span>Extracting <i class="fas fa-spinner fa-spin"></i></span>'));
                this.checkExtractResult(retStart.id);
            }
            else {
                showToast("Error", $('<span>Error while extracting <i class="fas fa-exclamation-circle"></i></span>'));
            }
        }
    }

    /**
     * Perform rename file/folder
     * @param {String} folderName new folder name
     */
    async rename(newName) {
        console.log(`renaming id ${this.renameModal.id} of type ${this.renameModal.type} to ${newName}`);
        let ret = null;
        if (this.renameModal.type === "f") {
            ret = await dashboardFileApi.rename(this.renameModal.id, newName);
        }
        else if (this.renameModal.type === "F") {
            ret = await dashboardFolderApi.rename(this.renameModal.id, newName);
        }
        if (ret && ret.id) {
            // good rename, refresh
            await this.fetch();
            this.recreateTable();
            this.renameModal.modal.modal("hide");
            showToast("Info", $(`<span>Renamed ${this.renameModal.type === "F"? "folder" : "file"} to ${newName} successfully <i class="far fa-smile"></i>.</span>`));
        }
    }


    async upload(file) {
        this.addModal.modal.modal("hide");
        this.startSpinner();
        const fileInfo = {
            name: file.name,
            mime: file.type,
            size: file.size
        }
        if (this.id !== 0 && this.type === "F") {
            fileInfo.parent = { id: this.id };
        }
        const ret = await dashboardFileApi.upload(fileInfo, file);
        if (ret && ret.id) {
            // good upload, refresh
            await this.fetch();
            this.recreateTable();
            showToast("Info", $(`<span>File ${fileInfo.name} uploaded <i class="far fa-smile"></i>.</span>`));
        }
        else {
            showToast("Error", $(`<span>Cannot upload file ${fileInfo.name} <i class="far fa-sad-tear"></i>.</span>`));
        }
        this.stopSpinner();
    }

    async mkdir(folderName) {
        let id = this.id;
        let ownerId = null;
        if (this.type === "u") {
            id = null;
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
        if (this.id === 0) {
            showToast("Error", "Please select a user first");
        }
        else {
            this.addModal.modal.modal('show');
        }
    }

    async checkExtractResult(extractId) {
        const ret = await extractApi.one(extractId);
        if (ret && ret.id) {
            if (ret.finishedTime > 0) {
                showToast("Info", $(`Finished!`));
                await this.fetch();
                this.recreateTable();
            }
            else {
                const result = await extractApi.result(extractId);
                showToast("Info", $(`Extracted ${result.progress} files <i class="fas fa-spinner fa-spin"></i>`));
                window.setTimeout(() => {
                    this.checkExtractResult(extractId);
                }, 3000);
            }
        }
    }

    async compressClick() {
        showToast("Info", $('<span>Compressing <i class="fas fa-spinner fa-spin"></i></span>'));

        // get selected items and convert to a readable array of models
        const select = this.table.rows({ selected: true }).data();
        const items = [];
        select.each(i => {
            items.push({
                id: i.id,
                type: i.type === "Folder"? "F":"f",
                name: i.name
            });
        });

        // make a new compression request
        const compReq = { folderId: this.type === "F" ? this.id : 0 };
        console.log("compressing to target folder", compReq.folderId);
        const resp = await compressApi.create(compReq);
        if (!resp || !resp.id) {
            showToast("Error", "Cannot create new compression request");
            return;
        }
        console.log(`Created compression request ${resp.id}`);

        // add files into this request
        for (var item of items) {
            console.log(`adding file ${item.name} into request ${resp.id}`);
            const itemResp = await compressApi.add(resp.id, item.type, item.id);
            if (!itemResp || !itemResp.id) {
                showToast("Error", `Cannot add file to compression request`);
                return;
            }
        }
        console.log("finished, prepare to start");

        // alright lets go
        compressApi.start(resp.id);

        window.setTimeout(() => {
            this.checkCompressResult(resp.id);
        }, 3000);
    }

    async checkCompressResult(compressId) {
        console.log("check compress result for comp id", compressId);
        const ret = await compressApi.result(compressId);
        if (ret && ret.id) {
            if (!ret.progress) ret.progress = 0;
            console.log(`finished ${ret.progress}/${ret.totalFiles}`);
            if (ret.progress == ret.totalFiles) {
                showToast("Info", $(`<span>Compression finished! <i class="far fa-smile"></i></span>`));
                await this.fetch();
                this.recreateTable();
            }
            else {
                showToast("Info", $(`<span>Still compressing, ${ret.progress}/${ret.totalFiles} <i class="fas fa-spinner fa-spin"></i></span>`));
                window.setTimeout(() => {
                    this.checkCompressResult(compressId);
                }, 3000);
            }
        }
    }

    async findClick(type, id, name) {
        if (type !== "f") {
            showToast("Error", "Sorry, similarity search is only available to images!");
            return;
        }
        const resp = await irApi.search(id);
        console.log("image search results", resp);
        if (resp && resp.length) {
            // get file details
            const ids = [... new Set(resp.map(f => f.fid))];
            const files = await fileApi.many(ids);
            console.log(files);
        }
    }
}


window.crud = new DataCRUD();

$(document).ready(() => window.crud.ready());