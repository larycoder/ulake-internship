/**
 * ulake query client supporting interaction to query API
 */
class ULakeQueryClient {
    /**
     * private method to perform API call with appropriate headers
     * @param {String} api - relative path to API
     * @param {String} method - http method
     * @param {Object} body - JSON object passing to API
     */
    async #callMethod(api, method, body) {
        let args = {
            method: method,
            headers: {
                "accept": "application/json",
                "content-type": "application/json"
            }
        }

        // load authentication
        if (globalObject.token != undefined && globalObject.token.length > 0) {
            args.headers["authorization"] = "bearer " + globalObject.token;
        }

        if (method == "POST") {
            let bodyString = JSON.stringify(body);
            args.body = bodyString;
        }

        return await fetch(api, args);
    }

    /**
     * generic method to call list API
     * @param {String} api - relative path to API
     * @param {Function} callback - callback to handle return data
     */
    #getList(api, callback) {
        this.#callMethod(api, "GET", undefined)
            .then(async (raw) => {
                try {
                    return await raw.json();
                } catch (err) {
                    console.log("[ULake Query] Get list data err: " + err);
                    return undefined;
                }
            })
            .then((resp) => callback(new DataListModel(resp)));
    }

    /**
     * build query parameters string
     * @param {Array.<String>} filters - list of filters
     */
    #getQueryParamString(filters) {
        let params = new URLSearchParams();
        if (filters.length > 0)
            for (let filter of filters)
                params.append("filter", filter);

        let queryString = params.toString();
        if (queryString.length > 0)
            return "?" + queryString;
        else
            return ""
    }

    /**
     * get object list
     * @param {Function} callback - handle function for returned data
     * @param {Array.<String>} filters - list of object filters
     */
    getObjectList(callback, filters = []) {
        let queryParams = this.#getQueryParamString(filters);
        let api = "/api/object" + queryParams;
        this.#getList(api, callback);
    }

    /**
     * get file list
     * @param {Function} callback - handle function for returned data
     * @param {Array.<String>} filters - list of file filters
     */
    getFileList(callback, filters = []) {
        let queryParams = this.#getQueryParamString(filters);
        let api = "/api/file" + queryParams;
        this.#getList(api, callback);
    }

    /**
     * retrieve token from lake
     * @param {UserModel} user - lake user information
     * @param {Function} callback - handle function for returned data
     */
    login(user, callback) {
        delToken();
        let body = {
            "userName": user.getUsername(),
            "password": user.getPassword()
        }
        let api = "/api/user/login";
        this.#callMethod(api, "POST", body).then((resp) => {
            resp.json().then((data) => {
                user.setToken(data.resp);
                callback(user);
            });
        });
    }

    /**
     * Generate download link
     * @param {String} cid content id of object
     * @returns hyperlink for data stream
     */
    static getDownloadLink(cid) {
        return "/api/object/" + cid + "/data";
    }
}
