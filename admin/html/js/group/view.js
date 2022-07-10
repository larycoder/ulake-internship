// SSI CRUD: <!--# include virtual="/js/crud.js" -->

class ViewCRUD extends CRUD {
    constructor () {
        super({
            api: groupApi,
            listUrl: "/groups",
            name: "Group",
            nameField: "name",
            hidden: "users"
        });
    }

    async confirm() {
        const select = this.modalTable.rows( { selected: true } ).data();
        let entity = this.info;
        entity.users = [];
        select.each(u => { entity.users.push({ id: u.id, userName: u.userName }); });
        this.api.save(crud.id, entity);
        this.modal.modal('hide');
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
        const users = this.info.users;
        const table = $("#user-table")
        table.DataTable({data: users,
            paging: false,
            columns: [
                { data: "id" },
                { data: "userName" }
            ]
        });
    }

    async viewReady() {
        // prepare modal events
        this.modal = $("#add-modal");
        this.modal.on("show.bs.modal", () => this.showModal());
        this.modal.find(".btn-primary").on("click", () => this.confirm());
        await super.viewReady();
        this.listUsers();
    }
};

const viewCrud = new ViewCRUD();
$(document).ready(() => viewCrud.viewReady());