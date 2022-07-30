import { BaseModal } from "./base.js";
import { UserModal } from "./user.js";
import { GroupModal } from "./group.js";
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
        this.groupModal = new GroupModal({
            itemClick: "window.crud.aclModal.selectGroup",
        });
        this.footer.find(".btn-primary").off().on("click", () => {
            this.save(this.dataType, this.dataId, this.dataName);
        });
        this.footer.find("#add-user").off().on("click", () => {
            this.dismiss();
            this.userModal.show();
        });
        this.footer.find("#add-group").off().on("click", () => {
            this.dismiss();
            this.groupModal.show();
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
        this.entries = this.transformToEntry(dataType, dataId, dataName, data.acls, data.users, data.groups);
        this.stopSpinner()
        this.render(dataType, dataId, dataName, this.entries);
        super.show();
    }

    /**
     * Make a new data-table entry from acl row in backend
     * @param {char} actorType u for user, g for group
     * @param {*} entity ACL entity from DB
     * @returns a new data-table entry
     */
    makeEntry(actorType, entity) {
        let entry = {
            type: actorType,
            name: entity.name,
            read: entity.permissions ? entity.permissions.includes("READ") : false,
            write: entity.permissions ? entity.permissions.includes("WRITE") : false,
            execute: entity.permissions ? entity.permissions.includes("EXECUTE") : false,
            add: entity.permissions ? entity.permissions.includes("ADD") : false,
            delete: entity.permissions ? entity.permissions.includes("DELETE") : false
        }
        // TODO: should be like this in the backend instewad
        // if (actorType === "u") entry.actorId = entity.userId;
        // if (actorType === "g") entry.actorId = entity.groupId;
        entry.actorId = entity.userId;
        return entry;
    }

    /**
     * Make db entity from a data-table entry
    * @param {char} actorType u for user, g for group
    * @param {*} entry data-table entry data
    * @param {*} fileId
    * @param {*} entry
     * @returns a new db acl entity
     */
    makeEntity(actorType, fileFolderId, entry) {
        let entity = {
            objectId: parseInt(fileFolderId),
            permissions: []
        };
        // TODO: should be like this in the backend instewad
        // if (actorType === "u") entity.userId = parseInt(entry.actorId);
        // if (actorType === "g") entity.groupId = parseInt(entry.actorId);
        entity.userId = parseInt(entry.actorId);
        if (entry.read) entity.permissions.push("READ");
        if (entry.write) entity.permissions.push("WRITE");
        if (entry.execute) entity.permissions.push("EXECUTE");
        if (entry.add) entity.permissions.push("ADD");
        if (entry.delete) entity.permissions.push("DELETE");
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
            if (acl.userId) {
                const user = users.find(u => u.id === acl.userId);
                if (user) acl.name = user.userName;
            }
        }
        // join groups
        for (const acl of acls.group) {
            if (acl.userId) {
                // this should be like .groupId at the backend instead
                // const group = groups.find(g => g.id === acl.groupId);
                const group = groups.find(g => g.id === acl.userId);
                if (group) acl.name = group.name;
            }
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
            users = entries.filter(e => e.type === "u").map(u => this.makeEntity("u", dataId, u));
            groups = entries.filter(e => e.type === "g").map(g => this.makeEntity("g", dataId, g));
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
    async save(dataType, dataId, dataName) {
        const entries = this.table.rows().data().toArray();
        const entities = this.transformToEntity(dataType, dataId, dataName, entries);
        const dt = dataType === "F" ?  "folder" : "file";
        // TODO: should have a single API endpoint to save permissions for many users
        const failed = [];
        for (const perm of entities.users) {
            const resp = await aclApi.sync("user", dt, perm);
            if (!resp || !resp.objectId) {
                failed.push(perm);
            }
        }
        for (const perm of entities.groups) {
            const resp = await aclApi.sync("group", dt, perm);
            if (!resp || !resp.objectId) {
                failed.push(perm);
            }
        }
        if (failed.length) {
            showModal("Error", `Unable to save permissions for ${failed.map(f => f.userId).join(', ')}`);
        }
        this.dismiss();
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
        let users = await userApi.many(userIds);
        let groups = await groupApi.many(groupIds);

        if (!Array.isArray(users)) users = [ users ];
        if (!Array.isArray(groups)) users = [ groups ];

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
        if (!this.table) {
            this.table = $(`#acl-table`).DataTable({
                data: entries,
                paging: false,
                searching: false,
                info: false,
                columns: [
                    { data: "actorId" },
                    { data: "type", render: (data, type, row) => `<i class="fas fa-${data === "u" ? "user" : "users"}"></i>`},
                    { data: "name" },
                    { data: "read", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} data-field="read" onchange="window.crud.aclModal.checkChange('${row.type}', '${data}', '${row.name}', this)">` },
                    { data: "write", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} data-field="write" onchange="window.crud.aclModal.checkChange('${row.type}', '${data}', '${row.name}', this)">` },
                    { data: "execute", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} data-field="execute" onchange="window.crud.aclModal.checkChange('${row.type}', '${data}', '${row.name}', this)">` },
                    { data: "add", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} data-field="add" onchange="window.crud.aclModal.checkChange('${row.type}', '${data}', '${row.name}', this)">` },
                    { data: "delete", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} data-field="delete" onchange="window.crud.aclModal.checkChange('${row.type}', '${data}', '${row.name}', this)">` },
                    { data: "id",
                        render: (data, type, row) =>
                            `<a href="#"><i class="fas fa-trash" onclick="window.crud.aclModal.delete('${row.type}', '${data}', '${row.name}', this)"></i></a>`
                    }
                ],
                order: []
            });
        }
        else {
            this.table.clear().draw();
            this.table.rows.add(entries);
            this.table.columns.adjust().draw();
        }
    }

    /**
     * Callback when a user is selected to be added into this popup
     * @param {string} type u for user
     * @param {integer} id id of the selected user
     * @param {string} name name of the selected user
     */
    selectUser(type, id, name) {
        console.log(`selected user on click ${id}, ${name}`);
        // check if exist
        const entries = this.table.rows().data().toArray();
        const entities = this.transformToEntity(this.dataType, this.dataId, this.dataName, entries);
        const exist = entities.users.map(u => u.userId).find(uid => uid === parseInt(id));
        if (exist) {
            this.userModal.dismiss();
            super.show();   // this.show() does a lot of things...
            showToast("Info", `User ${name} is already selected in the list`);
            return;
        }

        const entry = {
            userId: id,
            name: name
        }
        this.entries.push(this.makeEntry(type, entry));
        this.userModal.dismiss();
        super.show();   // this.show() does a lot of things...
        this.render(this.dataType, this.dataId, this.dataName, this.entries);
    }

    /**
     * Callback when a group is selected to be added into this popup
     * @param {string} type g for group
     * @param {integer} id id of the selected group
     * @param {string} name name of the selected group
     */
    selectGroup(type, id, name) {
        console.log(`selected group on click ${id}, ${name}`);
        // check if exist
        const entries = this.table.rows().data().toArray();
        const entities = this.transformToEntity(this.dataType, this.dataId, this.dataName, entries);
        const exist = entities.groups.map(g => g.groupId).find(gid => gid === parseInt(id));
        if (exist) {
            this.groupModal.dismiss();
            super.show();   // this.show() does a lot of things...
            showToast("Info", `Group ${name} is already selected in the list`);
            return;
        }

        const entry = {
            // should have been like this
            // groupId: id,
            userId: id,
            name: name
        }
        this.entries.push(this.makeEntry(type, entry));
        this.groupModal.dismiss();
        super.show();   // this.show() does a lot of things...
        this.render(this.dataType, this.dataId, this.dataName, this.entries);
    }

    /**
     * Callback when user click on a delete button
     * @param {string} type u for user, g for group
     * @param {integer} id id of the selected group/user
     * @param {string} name name of the selected group/user
     * @param {DOM} button button element of the clicked row
     */
    delete(type, id, name, button) {
        // delete both from UI and from data
        const row = $(button).parents("tr");
        const data = this.table.row(row).data();
        data.read = false;
        data.write = false;
        data.execute = false;
        data.add = false;
        data.delete = false;
        row.find("input[type=checkbox]").prop("checked", false);
    }

    /**
     * Callback when user changes permission on a group/user
     * @param {string} type u for user, g for group
     * @param {integer} id id of the selected group/user
     * @param {string} name name of the selected group/user
     * @param {DOM} button button element of the clicked row
     */
     checkChange(type, id, name, button) {
        var $button = $(button);
        const row = $button.parents("tr");
        const data = this.table.row(row).data();
        const field = $button.attr("data-field");
        data[field] = $button.prop("checked");
        console.log(data);
    }
}
