class Api {
    constructor(server, endpoint) {
        this.server = server;
        this.endpoint = endpoint;
    }

    async get(url) {
        const data = await ajax({
            url: this.server + this.endpoint + url
        });
        if (data && data.code === 200) {
            return data.resp;
        }
        return {};
    }

    async all() {
        return this.get("");
    }

    async one(id) {
        return this.get(`/${id}`);
    }
}

class User extends Api {
    constructor () {
        super(getUserUrl(), "/api/user")
    }

    async getName(id) {
        const ret = await this.one(id);
        return ret.userName;
    }
}

const user = new User();