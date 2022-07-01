/**
 * Generalized Api class for a specific CRUD endpoint
 */
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
        console.log(data);
        if (data && data.code === 200) return data.resp;
        return {};
    }

    async get(url) {
        return await this.call(url);
    }

    async post(url, body, headers) {
        return await this.call(url, "POST", body, headers);
    }

    async put(url, body, headers) {
        return await this.call(url, "PUT", body, headers);
    }

    async delete(url) {
        return await this.call(url, "DELETE");
    }

    async all() {
        return await this.get("");
    }

    async one(id) {
        return await this.get(`/${id}`);
    }

    async deleteOne(id) {
        return await this.delete(`/${id}`);
    }

    async save(id, body) {
        return await this.put(`/${id}`, body, { "Content-Type": "application/json; charset=utf-8" });
    }
}

/**
 * Specific API for User CRUD management
 */
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