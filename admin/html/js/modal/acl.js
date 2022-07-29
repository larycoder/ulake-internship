import { BaseModal } from "./base.js";
import { UserModal } from "./user.js";
import { fileApi, userApi, groupApi, aclApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

/**
 * A modal for showing image retrieval results
 */
export class AclModal extends BaseModal {
    constructor(callback) {
        super(callback, "#acl-modal");
        this.userModal = new UserModal({
            itemClick: "window.crud.aclModal.selectUser",
        });
        this.footer.find(".btn-primary").off().on("click", () => {
            this.save(this.dataType, this.dataId, this.dataName);
        });
        this.footer.find("#add-user").off().on("click", () => {
            this.dismiss();
            this.userModal.show();
        });
    }

     /**
     * Show permission modal for a specific file or folder
     * @param {char} dataType F for folder, f for file. u (user) is unsupported
     * @param {int} dataId id of file/folder to manage permission
     * @param {string} dataName name of file/folder to manage permission
     * @returns
     */
    async show(dataType, dataId, dataName) {
        this.startSpinner();
        // these fields are only for saving... We try to make this modal as stateless as we can
        this.dataType = dataType;
        this.dataId = dataId;
        this.dataName = dataName;

        const data = await this.fetch(dataType, dataId, dataName);
        const entries = this.transformToEntry(dataType, dataId, dataName, data.acls)
        this.stopSpinner();
        this.render(dataType, dataId, dataName, entries);
        super.show();
    }

    /**
     * Make a new data-table entry from acl row in backend
     * @param {char} userType u for user, g for group
     * @param {*} entity ACL entity from DB
     * @returns a new data-table entry
     */
    makeEntry(userType, entity) {
        let entry = {
            type: userType,
            name: entity.name,
            read: entity.permission.contains("READ"),
            write: entity.permission.contains("WRITE"),
            execute: entity.permission.contains("EXECUTE"),
            add: entity.permission.contains("ADD"),
            delete: entity.permission.contains("DELETE")
        }
        if (userType === "u") entry.id = entity.userId;
        if (userType === "g") entry.id = entity.groupId;

    }

    /**
     * Make db entity from a data-table entry
    * @param {char} userType u for user, g for group
    * @param {*} entry data-table entry data
    * @param {*} fileId
    * @param {*} entry
     * @returns a new db acl entity
     */
    makeEntity(userType, entry, fileFolderId) {
        let entity = {
            permission: []
        };
        if (userType === "u") entity.userId = entry.userId;
        if (userType === "g") entity.groupId = entry.groupId;
        if (entry.read) entity.permission.push("READ");
        if (entry.write) entity.permission.push("WRITE");
        if (entry.execute) entity.permission.push("EXECUTE");
        if (entry.add) entity.permission.push("ADD");
        if (entry.delete) entity.permission.push("DELETE");
        return entity;
    }

    /**
     * Transform ACL, users, and groups to a DataTable-friendly array
     * @param {char} dataType F for folder, f for file. u (user) is unsupported
     * @param {int} dataId id of file/folder to manage permission
     * @param {string} dataName name of file/folder to manage permission
     * @param {*} acls ACL entries from server { user, group }
     * @param {*} users
     * @param {*} groups
     */
    transformToEntry(dataType, dataId, dataName, acls, users, groups) {
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
        acls.user.forEach(u => entries.push(this.makeEntry("u", u)));
        acls.group.forEach(g => entries.push(this.makeEntry("g", g)));
        return entries;
    }

    /**
     * Transform ACL, users, and groups to a DataTable-friendly array
     * @param {char} dataType F for folder, f for file. u (user) is unsupported
     * @param {int} dataId id of file/folder to manage permission
     * @param {string} dataName name of file/folder to manage permission
     * @param {*} acls ACL entries from server { user, group }
     * @param {*} users
     * @param {*} groups
     */
    transformToEntity(dataType, dataId, dataName, entries) {
        let users = [];
        let groups = [];
        if (entries.length > 0) {
            users = entries.filter(e => e.type === "u").map(u => this.makeEntry(u));
            groups = entries.filter(e => e.type === "g").map(g => this.makeEntry(g));
        }
        let entities = {
            users: users,
            groups: groups
        };
        return entities;
    }

    /**
     * Save the permission table to database
     */
    save(dataType, dataId, dataName) {
        console.log("preparing data to save");
        const entries = this.table.rows().data();
        const entities = this.transformToEntity(dataType, dataId, dataName, entries);
        if (dataType === "F") {
            aclApi.saveFolder(dataId, entities.users, entities.groups);
        }
        else if (dataType === "f") {
            aclApi.saveFile(dataId, entities.users, entities.groups);
        }
    }

    /**
     * Fetch data from backends
     * @param {char} dataType F for folder, f for file. u (user) is unsupported
     * @param {int} dataId id of file/folder to manage permission
     * @param {string} dataName name of file/folder to manage permission
     * @returns acls, users and groups
     */
    async fetch(dataType, dataId, dataName) {
        let acls = [];
        if (dataType === "F") acls = await aclApi.folder(dataId);
        else if (dataType === "f") acls = await aclApi.file(dataId);
        else {
            showModal("Error", "Permission for user is not supported.");
            return;
        }

        // find unique user ids and group ids
        const userIds = [... new Set(acls.user.map(u => u.userId))];
        const groupIds = [... new Set(acls.group.map(g => g.groupId))];
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
     * @param {char} dataType F for folder, f for file. u (user) is unsupported
     * @param {int} dataId id of file/folder to manage permission
     * @param {string} dataName name of file/folder to manage permission
     * @param {string} entries normalized data-table entries to show onto UI
     */
    render(dataType, dataId, dataName, entries) {
        console.log("rendering", entries);
        this.table = $(`#acl-table`);
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

    selectUser(id) {
        console.log(`selected user on click ${id}`);
        this.userModal.dismiss();
        super.show();
    }
}
