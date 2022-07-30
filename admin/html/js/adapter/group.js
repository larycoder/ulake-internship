import { BaseAdapter } from "./base.js";
import { groupApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

export class GroupAdapter extends BaseAdapter {
    constructor (config) {
        if (!config) config = {};
        config.api = groupApi;
        super(config);
    }

    transform (raw) {
        return raw.map(g => { return {
            id: g.id,
            name: g.name,
            size: 0,
            type: "Group",
            action: g.id
        }});
    }

    getAllRenderers () {
        let ret = super.getAllRenderers();
        ret.name = (data, type, row) => {
            return `<a href="#" onclick="${this.itemClick}('g', '${row.id}', '${data}')">${data}</a>`
        };
        return ret;
    }
}
