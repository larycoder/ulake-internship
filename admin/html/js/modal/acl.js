import { BaseModal } from "./base.js";
import { fileApi, userApi, groupApi, aclApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

/**
 * A modal for showing image retrieval results
 */
export class AclModal extends BaseModal {
    constructor(callback) {
        super(() => {
            this.save();
        }, "#acl-modal");
    }

     /**
     * Show permission modal for a specific file or folder
     * @param {char} type F for folder, f for file. u (user) is unsupported
     * @param {int} id id of file/folder to manage permission
     * @param {string} name name of file/folder to manage permission
     * @returns
     */
    async show(type, id, name) {
        this.startSpinner();
        const data = await this.fetch(type, id, name);
        const entries = this.transform(type, id, name, data.acls)
        this.stopSpinner();
        this.render(type, id, name, entries);
        this.modal.modal("show");
    }

    /**
     * Make a new data-table entry from acl row
     * @param {char} type u for user, g for group
     * @param {*} entry
     * @returns a new data-table entry
     */
    makeEntry(type, entry) {
        return {
            id: entry.id,
            type: type,
            name: entry.name,
            read: entry.permission.contains("READ"),
            write: entry.permission.contains("WRITE"),
            execute: entry.permission.contains("EXECUTE"),
            add: entry.permission.contains("ADD"),
            delete: entry.permission.contains("DELETE")
        }
    }

    /**
     * Transform ACL, users, and groups to a DataTable-friendly array
     * @param {char} type F for folder, f for file. u (user) is unsupported
     * @param {int} id id of file/folder to manage permission
     * @param {string} name name of file/folder to manage permission
     * @param {*} acls ACL entries from server { user, group }
     * @param {*} users
     * @param {*} groups
     */
    transform(type, id, name, acls, users, groups) {
        // join users
        for (const acl of acls.user) {
            if (acl.userId) acl.name = users.find(u => u.id === acl.userId);
        }
        // join groups
        for (const acl of acls.group) {
            if (acl.groupId) acl.name = groups.find(g => g.id === acl.groupId);
        }
        // normalize fields
        let entries = [];
        acls.users.forEach(u => entries.push(this.makeEntry("u", u)));
        acls.groups.forEach(g => entries.push(this.makeEntry("g", g)));
        return entries;
    }

    save() {
        console.log("preparing data to save");

    }

    /**
     * Fetch data from backends
     * @param {char} type F for folder, f for file. u (user) is unsupported
     * @param {int} id id of file/folder to manage permission
     * @param {string} name name of file/folder to manage permission
     * @returns acls, users and groups
     */
    async fetch(type, id, name) {
        let acls = [];
        if (type === "F") acls = await aclApi.folder(id);
        else if (type === "f") acls = await aclApi.file(id);
        else {
            showModal("Error", "Permission for user is not supported.");
            return;
        }

        // find unique user ids and group ids
        const userIds = [... new Set(acls.user.map(u => u.id))];
        const groupIds = [... new Set(acls.grou.map(g => g.id))];
        const users = await userApi.many(userIds);
        const groups = await groupApi.many(groupIds);

        return {
            acls: acls,
            users: users,
            groups: groups
        }
    }

    /**
     * Render to the table
     * @param {array}} results of image retrieval
     * @param {array} files returned file info from folder.
     */
    render(type, id, name, entries) {
        this.table = $(`<table id="acl-table" class="table hover stripe">`);
        this.body.empty().append(table);

        this.table.DataTable({
            data: entries,
            paging: false,
            searching: false,
            info: false,
            columns: [
                { data: "id" },
                { data: "type", render: (data, type, row) => `<i class="fas fa-${data === "u" ? "user" : "group"}"></i>`},
                { data: "name" },
                { data: "read", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""}>` },
                { data: "write", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""}>` },
                { data: "execute", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""}>` },
                { data: "add", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""}>` },
                { data: "delete", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""}>` },
                { data: "id",
                    render: (data, type, row) =>
                        `<a href="#"><i class="fas fa-trash" onclick="window.crud.aclModal.delete("${row.type}", "${data}", "${row.name}")"></i></a>`
                }
            ],
            order: []
        });
    }
}
