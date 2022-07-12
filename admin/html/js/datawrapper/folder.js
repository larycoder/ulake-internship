import { BaseWrapper } from "./base.js";
import { folderApi } from "../api.js";

export class FolderWrapper extends BaseWrapper {
    constructor (config) {
        if (!config) config = {};
        config.api = folderApi;
        super(config);
    }

    transform(raw) {
        return raw.map(f => { return {
            id: `F${f.id}`,
            name: f.name,
            size: 0,
            type: "Folder",
            action: u.id
        }});
    }

    async fetch(parent) {
        if (parent == 0) {
            return await this.api.root();
        }
        else {
            const folder = await this.api.one(parent);

        }
    }

    getAllRenderers() {
        let ret = super.getAllRenderers();
        ret.name = (data, type, row) => `<a href="#" onclick="window.crud.clickFolder('${row.id}')">${data}</a>`;
        ret.action = (data, type, row) => `<a href="/user/edit?id=${data}"><i class="fas fa-user-edit"></i></a>`;
        return ret;
    }
}