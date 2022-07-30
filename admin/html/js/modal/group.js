import { BaseModal } from "./base.js";
import { GroupAdapter } from "../adapter/group.js";

export class GroupModal extends BaseModal {
    /**
     * @param {*} adapterConfig .itemClick is required..
     */
    constructor (adapterConfig) {
        super(null, "#group-modal");
        this.adapter = new GroupAdapter(adapterConfig);
    }

    async detail() {
        const raw = await this.adapter.fetch();
        const entries = this.adapter.transform(raw);
        this.table = $('#group-table').DataTable({
            data: entries,
            paging: false,
            searching: false,
            info: false,
            columns: [
                { data: "id" },
                { data: "name", render: this.adapter.getRenderer("name") }
            ],
            order: []
        });
    }

    show() {
        this.detail();
        super.show();
    }
}