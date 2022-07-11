import { CRUD } from './crud.js';

/**
 * A item detail view CRUD controller
 */
export class ViewCRUD extends CRUD {
    /**
     * @param {string} listUrl Url to the UI's list view, e.g. /users
     * @param {string} hidden [optional] Fields that will be hidden from the UI, e.g. "department, failedLogins, groups"
     * @param {string} readonly [optional] Fields that will be read-only from the UI, e.g. "id,registerTime,userName,isAdmin"
     */
    constructor (config) {
        super(config);
        this.listUrl = config.listUrl;
        this.hidden = config.hidden? config.hidden.split(",").map((field) => field.trim()) : [];
        this.readonly = config.readonly? config.readonly.split(",").map((field) => field.trim()) : [];
    }

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
