/**
 * provide ulake transfer data model
 */

/**
 * hold user information
 */
class UserModel {
    constructor() {
        this.username = "";
        this.password = "";
        this.token = "";
    }

    setUsername(username) {
        this.username = username;
    }
    setPassword(password) {
        this.password = password;
    }
    setToken(token) {
        this.token = token;
    }

    getUsername() {
        return this.username;
    }
    getPassword() {
        return this.password;
    }
    getToken() {
        return this.token;
    }
}


/**
 * Hold response data of ulake
 * @param {Object} resp response objects from lake
 */
class DataListModel {

    /**
     * @param {Object} resp response result from ulake
     */
    constructor(resp) {
        if (resp != undefined) {
            this.data = resp.resp;
            this.code = resp.code;
        }
        this.#collectHead();
    }

    /**
     * Collect header of data from data resp
     */
    #collectHead() {
        if (this.data != undefined && this.data.length > 0) {
            this.head = Object.keys(this.data[0]);
        } else {
            this.head = [];
        }
    }

    getAllData() {
        return this.data;
    }

    /**
     * Get data by index
     * @param {int} index row index of data
     */
    getData(index) {
        if (this.data != undefined && this.data.length >= index) {
            return this.data[index];
        } else {
            return undefined;
        }
    }

    getCode() {
        return this.code;
    }

    getHead() {
        return this.head;
    }
}
