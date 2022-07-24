import { BaseAdapter } from "./base.js";
import { folderApi } from "http://common.dev.ulake.sontg.net/js/api.js";

export class FolderAdapter extends BaseAdapter {
    constructor (config) {
        if (!config) config = {};
        config.api = folderApi;
        super(config);
        this.zipMime = [ "application/zip", "application/x-zip-compressed" ];
        this.itemClick = config.itemClick || "window.crud.click";
        this.itemTypes = config.itemTypes || [ "folders", "files"]
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
            id: `${f.id}`,
            name: f.name,
            size: f.size,
            type: `File (${f.mime})`,
            mime: f.mime,
            action: f.id
        }});
    }

    transform(raw) {
        let ret = [];
        for (const type of this.itemTypes) {
            console.log("transforming type", type);
            if (type === "folders") ret = ret.concat(this.transformFolders(raw.subFolders));
            if (type === "files") ret = ret.concat(this.transformFiles(raw.files));
        }
        return ret;
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
        ret.name = (data, type, row) => `<a href="#" onclick="${this.itemClick}('${row.type === "Folder"? "F" : "f"}', '${row.id}', '${data}')">${data}</a>`;
        ret.size = (data, type, row) => `${humanFileSize(data)}`;
        ret.action = (data, type, row) => {
            let html = "";
            html += `<a href="#" title="Rename" onclick="window.crud.renameClick('${row.type === "Folder"? "F" : "f"}', '${row.id}', '${row.name}')"><i class="fas fa-edit"></i></a>`
            html += `<a href="#" title="Delete" onclick="window.crud.deleteClick('${row.type === "Folder"? "F" : "f"}', '${row.id}', '${row.name}')"><i class="fas fa-trash"></i></a>`
            if (this.zipMime.includes(row.mime)) {
                html += `<a href="#" title="Extract" onclick="window.crud.extractClick('${row.type === "Folder"? "F" : "f"}', '${row.id}', '${row.name}')"><i class="fas fa-box-open"></i></a>`
            }
            if (row.mime && row.mime.startsWith("image/")) {
                html += `<a href="#" title="Find" onclick="window.crud.findClick('${row.type === "Folder"? "F" : "f"}', '${row.id}', '${row.name}')"><i class="fas fa-search"></i></a>`
            }
            return html;
        };
        return ret;
    }
}