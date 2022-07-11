import { ListCRUD } from "./crud/listcrud.js";
import { groupApi } from "./api.js";

class GroupListCRUD extends ListCRUD {
    constructor () {
        super({
            api: groupApi,
            listUrl: "/groups",
            name: "Group",
            nameField: "name",
            listFieldRenderer: [
                { mData: "id" },
                { mData: "name", render: (data, type, row) => `<a href="/group/view?id=${row.id}">${data}</a>` },
                { mData: "id",
                    render: (data, type, row) =>
                        `<a href="/group/edit?id=${data}"><i class="fas fa-user-cog"></i></a>
                         <a href="#"><i class="fas fa-users-slash" onclick="window.crud.confirm(${data})"></i></a>`
                }
            ]
        })
    }

    async create() {
        const name = this.modal.find("input").val();
        const group = {
            name: name,
            users: []
        }
        const resp = await this.api.create(group);
        if (resp && resp.id) {
            this.modal.modal('hide');
            showToast("Info", `Group "${name}" created.`);
        }
    }

    async showModal() {
        this.modal.find("input").val("");
    }

    async ready() {
        // prepare modal events
        this.modal = $("#add-modal");
        this.modal.on("show.bs.modal", () => this.showModal());
        this.modal.find(".btn-primary").on("click", () => this.create());
        // prepare toast
        await super.ready();
    }
}

window.crud = new GroupListCRUD();
$(document).ready(() => window.crud.ready());