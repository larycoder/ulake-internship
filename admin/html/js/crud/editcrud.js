import { CRUD } from './crud.js';

/**
 * A item detail edit CRUD controller
 */
export class EditCRUD extends CRUD {
    detail() {
        $("#name-detail").text(`Update ${this.name} Detail for ${this.data[this.nameField]}`);
        this.table = $('#table').DataTable(  {
            data: this.keyPairs,
            paging: false,
            searching: false,
            info: false,
            aoColumns: [
                { mData: "key" },
                { mData: "value", render: (data, type, row) =>
                    `<input class="form-control border-1 small" type="text" value="${data}" data-for="${row.key}" ${row.readonly ? "readonly" : ""}>` }
            ]
        });
    }

    async fetch() {
        const params = parseParam("id", this.listUrl);
        this.id = parseInt(params.id);
        this.data = await this.api.one(this.id);
        this.keyPairs = toTable(this.data, this.hidden);
        this.keyPairs.forEach((data, index) => {
            if (this.readonly.includes(data.key)) {
                this.keyPairs[index].readonly = true;
            }
        });
    }

    async ready() {
        super().ready();
        $("#save").click(() => this.save());
    }

    async save() {
        // TODO: validate form input.
        const _crud = this;
        const ret = {};
        const rows = $("#table tbody tr");
        rows.each(function () {
            const _this = $(this);
            const key = _this.find("td:first-child").text();
            const value = _this.find("td:last-child input").val();
            if ((/*!_crud.readonly.includes(key) &&
                 !_crud.hidden.includes(key) &&*/
                !isEmpty(value)) || (key === "id")) {
                    ret[key] = value;
            }
        });
        const resp = await this.api.save(ret.id, JSON.stringify(ret));
        if (resp == null) { // no error
            showModal("Info", "Successfully saved", () => {
                window.location = this.listUrl;
            })
        }
    }
}
