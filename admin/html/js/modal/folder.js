import { AddModal } from "./add.js";
import { FolderAdapter } from "../adapter/folder.js";

export class FolderModal extends AddModal {
    constructor(callback) {
        super(callback, "#folder-modal");
        this.modal.on("show.bs.modal", () => this.detail());
        this.id = 0;
        this.adapter = new FolderAdapter({
            itemClick: "window.crud.folderModal.click",
            itemTypes: ["folders"]
        });
        this.fields = [ "id", "name" ];
        this.path = [ 0 ];
    }

    async detail() {
        let raw;
        if (this.id === 0) raw = await this.adapter.fetch("u", getUid());
        else raw = await this.adapter.fetch("F", this.id);
        const entries = this.adapter.transform(raw);
        console.log(entries);
        if (!this.table) {
            this.listFieldRenderer = this.fields.map(f => {return {
                data: f,
                render: this.adapter.getRenderer(f)
            }});
            console.log("preparing data table");
            this.table = $('#folder-table').DataTable(  {
                data: entries,
                paging: true,
                columns: this.listFieldRenderer,
                order: []
            });
        }
        else this.reload(entries);
    }

    reload(data) {
        if (this.table) {
            this.table.clear().draw();
            this.table.rows.add(data);
            this.table.columns.adjust().draw();
        }
    }

    click(type, id, name) {
        console.log(`clicked ${type}, ${id}, ${name}`);
        this.path.push(id);
        this.id = id;
        this.detail();
    }
}