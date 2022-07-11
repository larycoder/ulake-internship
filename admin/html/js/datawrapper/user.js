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
}