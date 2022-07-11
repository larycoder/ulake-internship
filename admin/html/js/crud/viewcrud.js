import { CRUD } from './crud.js';

/**
 * A item detail view CRUD controller
 */
export class ViewCRUD extends CRUD {
    async detail() {
        $("#name-detail").text(`${this.name} Detail for ${this.data[this.nameField]}`);
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

    async fetch() {
        const params = parseParam("id", this.listUrl);
        this.id = parseInt(params.id);
        this.data = await this.api.one(this.id);
        this.keyPairs = toTable(this.data, this.hidden);
    }
 }
