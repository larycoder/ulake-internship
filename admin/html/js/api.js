class Api {
    constructor(server, endpoint) {
        this.server = server;
        this.endpoint = endpoint;
    }

    async call(url, method, body, headers) {
        const req = { url: this.server + this.endpoint + url };
        if (method) req.method = method;
        if (headers) req.headers = headers;
        if (body) req.data = body;
        const data = await ajax(req);
        if (data && data.code === 200) return data.resp;
        return {};
    }

    async get(url) {
        return this.call(url);
    }

    async post(url, body, headers) {
        return this.call(url, "POST", body, headers);
    }

    async put(url, body, headers) {
        return this.call(url, "PUT", body, headers);
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

    async save(id, body) {
        return this.put(`/${id}`, body);
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