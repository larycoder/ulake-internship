import { BaseWrapper } from "./base.js";
import { folderApi } from "../api.js";

export class FolderWrapper extends BaseWrapper {
    constructor (config) {
        if (!config) config = {};
        config.api = folderApi;
        super(config);
    }

    transformFolders(folders) {
        return folders.map(f => { return {
            id: `F${f.id}`,
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
        console.log("folder transform from ", raw);
        const folders = this.transformFolders(raw.subFolders);
        const files = this.transformFiles(raw.files);
        return folders.concat(files);
    }

    async fetch(parent) {
        if (parent < 0) {
            return await this.api.root(-parent);
        }
        else {
            const folder = await this.api.one(parent);
            console.log('folder returns ', folder);
            return folder;
        }
    }

    getAllRenderers() {
        let ret = super.getAllRenderers();
        ret.name = (data, type, row) => `<a href="#" onclick="window.crud.click('${row.id}')">${data}</a>`;
        return ret;
    }
}