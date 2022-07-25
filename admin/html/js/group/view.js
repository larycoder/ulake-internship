import { ViewCRUD } from '../crud/viewcrud.js';
import { userApi, groupApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

class GroupViewCRUD extends ViewCRUD {
    constructor () {
        super({
            api: groupApi,
            name: "Group",
            nameField: "name",
            listUrl: "/groups",
            hidden: "users"
        });
    }

    async confirm() {
        const select = this.modalTable.rows( { selected: true } ).data();
        let entity = this.data;
        entity.users = [];
        select.each(u => { entity.users.push({ id: u.id, userName: u.userName }); });
        await this.api.save(crud.id, entity);
        this.modal.modal('hide');
        await this.fetch();
        this.detail();
    }

    async initModalTable() {
        const data = await userApi.all();
        const table = this.modal.find("#add-table");
        this.modalTable = table.DataTable({data: data,
            select: { style: 'multi' },
            columns: [
                { mData: "id" },
                { mData: "userName" }
            ]
        });
    }

    async showModal() {
        if (this.modalTable) this.modalTable.rows().deselect();
        else this.initModalTable();
    }

    listUsers() {
        const users = this.data.users;
        const table = $("#user-table")
        table.DataTable({data: users,
            paging: false,
            columns: [
                { data: "id" },
                { data: "userName" }
            ]
        });
    }

    async ready() {
        // prepare modal events
        this.modal = $("#add-modal");
        this.modal.on("show.bs.modal", () => this.showModal());
        this.modal.find(".btn-primary").on("click", () => this.confirm());
        await super.ready();
        this.listUsers();
    }
};

const crud = new GroupViewCRUD();
$(document).ready(() => crud.ready());
