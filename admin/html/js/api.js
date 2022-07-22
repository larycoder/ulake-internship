/**
 * Generalized Api class for a specific CRUD endpoint
 */
export class Api {
    constructor(server, endpoint, processJson) {
        this.server = server;
        this.endpoint = endpoint;

        // default processing json response
        if (processJson === undefined || processJson === null) {
            processJson = true;
        }
        this.processJson = processJson;
    }

    /**
     * Ajax function, with token if any
     */
    async ajax(param) {
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
        if (body) {
            if (typeof body === 'string' || body instanceof String || body instanceof FormData)
                req.body = body;
            else req.body = JSON.stringify(body);
        }
        const data = await this.ajax(req);
        console.log(data);
        if (this.processJson) {
            if (data && data.code === 200) return data.resp;
            return {};
        }
        return data;
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

    async deleteRecursive(id) {
        return await this.delete(`/${id}/recursive`);
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

    async count(id) {
        return await this.get(`/${id}/count`);
    }

    async list(id) {
        return await this.get(`/${id}/files`);
    }

    async add(reqId, type, id) {
        if (type === "f") {
            return await this.post(`/${reqId}/file`, { fileId: id }, { "Content-Type": "application/json; charset=utf-8" } );
        }
        else if (type === "F") {
            return await this.post(`/${reqId}/folder`, { folderId: id }, { "Content-Type": "application/json; charset=utf-8" } );
        }
    }

    async start(id) {
        return await this.post(`/${id}/start`);
    }

    async stop(id) {
        return await this.delete(`/${id}`);
    }

    async result(id) {
        return await this.get(`/${id}/result`);
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
        let formData = new FormData();
        formData.append("fileInfo", new Blob([JSON.stringify(fileInfo)], {
            type: "application/json"
        }));
        formData.append("file", new File([file], {
            type: "application/octet-stream"
        }));
        return await this.post("", formData);
    }

    async rename(id, name) {
        const info = {
            id: id,
            name: name
        }
        return await this.save(id, info);
    }
}

/**
 * Specific API for Dashboard folder management
 */
 class DashboardFolderApi extends Api {
    constructor () {
        super(getDashboardUrl(), "/api/folder")
    }

    async mkdir(name, parentId, ownerId) {
        const info = {
            name: name,
        };
        if (parentId) {
            info.parent = { id : parentId };
        }
        if (ownerId) info.ownerId = ownerId;
        return await this.create(info);
    }

    async rename(id, name) {
        const info = {
            id: id,
            name: name
        }
        return await this.save(id, info);
    }
}

/**
 * Specific API for Dashboard object management
 */
 class DashboardObjectApi extends Api {
    constructor () {
        super(getDashboardUrl(), "/api/object", false);
    }

    async download(id) {
        return await this.get(`${id}/fileData`);
    }
}


/**
 * Specific API for Admin CRUD management
 */
 class AdminApi extends Api {
    constructor () {
        super(getAdminUrl(), "/api/admin")
    }

    async coreStats() {
        return await this.get("/objects/stats");
    }

    async userStats() {
        return await this.get("/users/stats");
    }

    async folderStats() {
        return await this.get("/folders/stats");
    }

    async fileStats() {
        return await this.get("/files/stats");
    }
}

/**
 * Specific API for Extract request management
 */
 class ExtractApi extends Api {
    constructor () {
        super(getExtractUrl(), "/api/extract")
    }

    async start(id) {
        return await this.post(`/${id}/start`);
    }

    async result(id) {
        return await this.get(`/${id}/result`);
    }
}

/**
 * Specific API for Ingestion Configuration CRUD management
 */
class IngestionTemplateApi extends Api {
    constructor () {
        super(getIngestionUrl(), "/api/template")
    }

    async query(id) {
        return await this.get(`/${id}/query`);
    }
}

/**
 * Specific API for Ingestion Progress CRUD management
 */
class IngestionApi extends Api {
    constructor () {
        super(getIngestionUrl(), "/api/ingest")
    }

    async crawl(policy, folderId) {
        return await this.create(`/crawl`, policy);
    }

    async fetch(policy) {
        return await this.create(`/fetch`, policy);
    }

    async files(id) {
        return await this.get(`/${id}/files`);
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
const dashboardObjectApi = new DashboardObjectApi();
const adminApi = new AdminApi();
const extractApi = new ExtractApi();
const ingestionApi = new IngestionApi();
const ingestionTemplateApi = new IngestionTemplateApi();

$("#userName").text(getUserName());

export { userApi, authApi, groupApi, objectApi, tableApi };
export { logApi, compressApi, fileApi, folderApi };
export { dashboardFileApi, dashboardFolderApi, dashboardObjectApi };
export { extractApi, adminApi, ingestionApi, ingestionTemplateApi };