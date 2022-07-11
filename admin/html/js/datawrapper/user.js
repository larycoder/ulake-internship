import { DataWrapper } from "./base.js";
import { userApi } from "../api.js";

export class UserWrapper extends DataWrapper {
    constructor (config) {
        if (!config) config = {};
        config.api = userApi;
        super(config);
    }

    transform() {
        return this.data.map(u => { return {
            name: u.userName,
            id: `u${u.id}`,
            size: 0,
            type: "User"
        }});
    }
}