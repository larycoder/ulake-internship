/**
 * Generalized Api class for a specific CRUD endpoint
 */
class Api {
    constructor(server, endpoint) {
        this.server = server;
        this.endpoint = endpoint;
    }

    /**
     * Ajax function, with token if any
     */
    async ajax(param){
        const headers = param.headers || {};
        const token = getToken();
        if (token && typeof token === 'string' && token !=='undefined') headers.Authorization = "Bearer " + token;
        param.headers = headers;
        if (!param.method) param.method = "GET";

        const resp = await fetch(param.url, param);
        if (!resp.ok) {
            const error = resp.error;
            window.alert(`No response from server. Error: ${JSON.stringify(error)}`);
            if (error && error.status && error.status === 401) {
                window.location = "/login";
            }
        };
        return await resp.json();
    };

    async call(url, method, body, headers) {
        const req = { url: this.server + this.endpoint + url };
        if (method) req.method = method;
        if (headers) req.headers = headers;
        if (body) req.body = typeof body === 'string' || body instanceof String? body : JSON.stringify(body);
        const data = await this.ajax(req);
        console.log(data);
        if (data && data.code === 200) return data.resp;
        return {};
    }

    // general http methods
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

    // getters
    async all() {
        return await this.get("");
    }

    async one(id) {
        return await this.get(`/${id}`);
    }

    async many(ids) {
        // server should support one and many at the same endpoint
        return await this.get(`/${ids}`);
    }

    async data(id) {
        return await this.get(`/${id}/data`);
    }

    // create, update, delete
    async create(entity) {
        return await this.post("", entity, { "Content-Type": "application/json; charset=utf-8" });
}

    async save(id, entity) {
        return await this.put(`/${id}`, entity, { "Content-Type": "application/json; charset=utf-8" });
    }

    async deleteOne(id) {
        return await this.delete(`/${id}`);
    }

}

/**
 * Specific API for User CRUD management
 */
class UserApi extends Api {
    constructor () {
        super(getUserUrl(), "/api/user")
    }

    async getName(id) {
        const ret = await this.one(id);
        return ret.userName;
    }
}

/**
 * Specific API for Authentication
 */
 class AuthApi extends Api {
    constructor () {
        super(getUserUrl(), "/api/auth")
    }

    async login(userName, password) {
        return await this.post(`/login`, {
            userName: userName,
            password: password
        },
        { "Content-Type": "application/json" });
    }
}


/**
 * Specific API for Group CRUD management
 */
class GroupApi extends Api {
    constructor () {
        super(getUserUrl(), "/api/user/group")
    }
}

/**
 * Specific API for Object CRUD management
 */
 class ObjectApi extends Api {
    constructor () {
        super(getCoreUrl(), "/api/object")
    }
}

/**
 * Specific API for Table CRUD management
 */
 class TableApi extends Api {
    constructor () {
        super(getTableUrl(), "/api/table")
    }
}

/**
 * Specific API for Log CRUD management
 */
 class LogApi extends Api {
    constructor () {
        super(getLogUrl(), "/api/log")
    }
}

/**
 * Specific API for Compress Taks CRUD management
 */
 class CompressApi extends Api {
    constructor () {
        super(getCompressUrl(), "/api/compress")
    }

    async countFiles(id) {
        return await this.get(`/${id}/count`);
    }
}

/**
 * Specific API for File CRUD management
 */
 class FileApi extends Api {
    constructor () {
        super(getFolderUrl(), "/api/file")
    }
}

/**
 * Specific API for File CRUD management
 */
 class FolderApi extends Api {
    constructor () {
        super(getFolderUrl(), "/api/folder")
    }

    async root(id) {
        return await this.get("/root" + (id ? `/${id}` : ""));
    }
}

/**
 * Specific API for Dashboard file management
 */
 class DashboardFileApi extends Api {
    constructor () {
        super(getDashboardUrl(), "/api/file")
    }

    async upload(fileInfo, file) {
        return await this.post("");
    }
}

/**
 * Specific API for Dashboard folde management
 */
 class DashboardFolderApi extends Api {
    constructor () {
        super(getDashboardUrl(), "/api/folder")
    }

    async mkdir(name, parentId, ownerId) {
        const info = {
            name: name,
            parentId: parentId
        }
        if (ownerId) info.ownerId = ownerId;
        return await this.create(info);
    }
}

/**
 * Specific API for Admin CRUD management
 */
 class AdminApi extends Api {
    constructor () {
        super(getAdminUrl(), "/api/admin")
    }

    async upload(fileInfo, file) {
        let formData = new FormData();
        formData.append("fileInfo", new Blob([JSON.stringify(fileInfo)], {
            type: "application/json"
        }));
        formData.append("file", new File([file], {
            type: "application/octet-stream"
        }));
        return await this.post("", formData);
    }
}

const userApi = new UserApi();
const authApi = new AuthApi();
const groupApi = new GroupApi();
const objectApi = new ObjectApi();
const tableApi = new TableApi();
const logApi = new LogApi();
const compressApi = new CompressApi();
const fileApi = new FileApi();
const folderApi = new FolderApi();
const dashboardFileApi = new DashboardFileApi();
const dashboardFolderApi = new DashboardFolderApi();
const adminApi = new AdminApi();

$("#userName").text(getUserName());

export { userApi, authApi, groupApi, objectApi, tableApi, logApi, compressApi, fileApi, folderApi, dashboardFileApi, dashboardFolderApi, adminApi };