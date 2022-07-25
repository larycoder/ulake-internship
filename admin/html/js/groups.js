import { ListCRUD } from "./crud/listcrud.js";
import { groupApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";
import { AddGroupModal } from "./group/add.js";

class GroupListCRUD extends ListCRUD {
    constructor () {
        super({
            api: groupApi,
            name: "Group",
            nameField: "name",
            listFieldRenderer: [
                { data: "id" },
                { data: "name", render: (data, type, row) => `<a href="/group/view?id=${row.id}">${data}</a>` },
                { data: "id",
                    render: (data, type, row) =>
                        `<a href="/group/edit?id=${data}"><i class="fas fa-user-cog"></i></a>
                         <a href="#"><i class="fas fa-users-slash" onclick="window.crud.confirm(${data})"></i></a>`
                }
            ]
        })
    }

    async create() {
        const name = this.modal.modal.find("input").val();
        const group = {
            name: name,
            users: []
        }
        const resp = await this.api.create(group);
        if (resp && resp.id) {
            this.modal.modal.modal('hide');
            showToast("Info", `Group "${name}" created.`);
            await this.fetch();
            await this.detail();
        }
    }

    async showModal() {
        this.modal.modal.find("input").val("");
    }

    async ready() {
        // prepare modal events
        this.modal = new AddGroupModal(() => this.create());
        await super.ready();
    }
}

window.crud = new GroupListCRUD();
$(document).ready(() => window.crud.ready());