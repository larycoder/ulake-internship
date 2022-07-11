import { BaseWrapper } from "./base.js";
import { userApi } from "../api.js";

export class UserWrapper extends BaseWrapper {
    constructor (config) {
        if (!config) config = {};
        config.api = userApi;
        super(config);
    }

    transform() {
        return this.data.map(u => { return {
            id: `u${u.id}`,
            name: u.userName,
            size: 0,
            type: "User",
            action: u.id
        }});
    }

    getAllRenderers() {
        let ret = super.getAllRenderers();
        ret.name = (data, type, row) => `<a href="#">${data}</a>`;
        ret.action = (data, type, row) => `<a href="/user/edit?id=${data}"><i class="fas fa-user-edit"></i></a>`;
        return ret;
    }
}