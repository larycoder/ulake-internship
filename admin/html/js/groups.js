// SSI CRUD: <!--# include virtual="/js/crud.js" -->

class GroupListCRUD extends CRUD {
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
                         <a href="#"><i class="fas fa-users-slash" onclick="crud.listDeleteItem(${data})"></i></a>`
                }
            ]
        })
    }

    async confirm() {
        const name = this.modal.find("input").val();
        const group = {
            name: name,
            users: []
        }
        const resp = this.api.create(group);
        if (resp && resp.code === 200) {
            this.modal.modal('hide');
        }
    }

    async showModal() {
        this.modal.find("input").val("");
    }

    async listReady() {
        // prepare modal events
        this.modal = $("#add-modal");
        this.modal.on("show.bs.modal", () => this.showModal());
        this.modal.find(".btn-primary").on("click", () => this.confirm());
        await super.listReady();
    }

}
const groupListCrud = new GroupListCRUD();

$(document).ready(() => groupListCrud.listReady());