import { BaseModal } from "./base.js";
import { UserAdapter } from "../adapter/user.js";

/**
 * Simple user name, no need of full name
 */
class UserBasicAdapter extends UserAdapter {
    constructor() {
        super({
            itemClick: "window.crud.aclModal.userModal.click",
        });
    }
    getAllRenderers () {
        let ret = super.getAllRenderers();
        ret.name = (data, type, row) => {
            return `<a href="#" onclick="${this.itemClick}('u', '${row.id}', '${data}')">${data}</a>`
        };
        return ret;
    }
}

export class UserModal extends BaseModal {
    constructor(callback) {
        super(() => {
            this.modal.modal("hide");
            callback(this.id);
        }, "#user-modal");
        this.adapter = new UserBasicAdapter();
    }

    async detail() {
        const raw = await this.adapter.fetch();
        const entries = this.adapter.transform(raw);
        console.log("users" ,entries);

        // only show these two fields
        const fields = [ "id", "name", "firstName", "lastName" ];
        this.listFieldRenderer = fields.map(f => {return {
            data: f,
            render: this.adapter.getRenderer(f)
        }});
        this.table = $('#user-table').DataTable({
            data: entries,
            paging: false,
            searching: false,
            info: false,
            columns: this.listFieldRenderer,
            order: []
        });
    }

    show() {
        this.detail();
        super.show();
    }

    click(type, id, name) {
        console.log(`clicked ${type}, ${id}, ${name}`);
    }
}