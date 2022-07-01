class Api {
    constructor(server, endpoint) {
        this.server = server;
        this.endpoint = endpoint;
    }

    async call(url, method, body) {
        const req = { url: this.server + this.endpoint + url };
        if (method) req.method = method;
        if (body) req.data = body;
        const data = await ajax(req);
        if (data && data.code === 200) return data.resp;
        return {};
    }

    async get(url) {
        return this.call(url);
    }

    async post(url, body) {
        return this.call(url, "POST", body);
    }

    async put(url, body) {
        return this.call(url, "PUT");
    }

    async delete(url) {
        return this.call(url, "DELETE");
    }

    async all() {
        return this.get("");
    }

    async one(id) {
        return this.get(`/${id}`);
    }

    async deleteOne(id) {
        return this.delete(`/${id}`);
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

const userApi = new User();

$("#userName").text(getUserName());