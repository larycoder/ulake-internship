import { BaseAdapter } from "./base.js";
import { folderApi } from "../api.js";

export class FolderAdapter extends BaseAdapter {
    constructor (config) {
        if (!config) config = {};
        config.api = folderApi;
        super(config);
    }

    transformFolders(folders) {
        return folders.map(f => { return {
            id: `${f.id}`,
            name: f.name,
            size: 0,
            type: "Folder",
            action: f.id
        }});
    }

    transformFiles(files) {
        return files.map(f => { return {
            id: `f${f.id}`,
            name: f.name,
            size: f.size,
            type: `File (${f.mime})`,
            action: f.id
        }});
    }

    transform(raw) {
        const folders = this.transformFolders(raw.subFolders);
        const files = this.transformFiles(raw.files);
        return folders.concat(files);
    }

    async fetch(type, parent) {
        if (type === "u") {
            return await this.api.root(parent);
        }
        else {
            const folder = await this.api.one(parent);
            return folder;
        }
    }

    getAllRenderers() {
        let ret = super.getAllRenderers();
        ret.name = (data, type, row) => `<a href="#" onclick="window.crud.click('F', '${row.id}', '${data}')">${data}</a>`;
        ret.action = (data, type, row) => `<a href="#" onclick="window.crud.delete('F', '${row.id}', '${data}')"><i class="fas fa-trash"></i></a>`;
        return ret;
    }
}