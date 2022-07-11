import { CRUD } from './crud.js';

/**
 * A item detail view CRUD controller
 */
export class ViewCRUD extends CRUD {
    detail() {
        $("#name-detail").text(`${this.name} Detail for ${this.data[this.nameField]}`);
        if (!this.table) {
            this.table = $('#table').DataTable(  {
                data: this.keyPairs,
                paging: false,
                searching: false,
                info: false,
                columns: [
                    { data: "key" },
                    { data: "value" }
                ]
            });
        }
        else this.reloadTable(this.keyPairs);
    }

    async fetch() {
        const params = parseParam("id", this.listUrl);
        this.id = parseInt(params.id);
        this.data = await this.api.one(this.id);
        this.keyPairs = toTable(this.data, this.hidden);
    }

    getHeader() {
        return $('#table').parents("div[class*=card]").find("div h6");
    }
 }
