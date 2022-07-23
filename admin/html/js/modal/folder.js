import { AddModal } from "./add.js";
import { FolderAdapter } from "../adapter/folder.js";
import { Breadcrumb } from "../breadcrumb.js";

export class FolderModal extends AddModal {
    constructor(callback) {
        super(callback, "#folder-modal");
        this.modal.on("show.bs.modal", () => {
            this.id = 0;
            this.detail();
        });
        this.adapter = new FolderAdapter({
            itemClick: "window.crud.folderModal.click",
            itemTypes: ["folders"]
        });
        this.fields = [ "id", "name" ];
        this.path = [ { type: "u", id: 0 } ];
        this.breadcrumb = new Breadcrumb({
            name: "Root",
            click: "window.crud.folderModal.click('u', '0', 'Root')",
        });
        this.breadcrumb.render();
    }

    updateBreadcrumb(type, id, name) {
        // check if user clicked on an item on the existing path
        id = parseInt(id);
        const itemPos = this.path.findIndex(i => i.id === id);
        if (itemPos >= 0) {
            // yes, strip the remaining
            this.path = this.path.slice(0, itemPos + 1);
            this.breadcrumb.keep(itemPos);
        }
        else {
            // that's a new entry
            this.path.push({
                type: this.type,
                id: this.id
            });
            this.breadcrumb.append({
                name: name,
                click: `window.crud.folderModal.click('${this.type}', '${this.id}', '${this.name}')`,
            });
        }
        this.breadcrumb.render();
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
                paging: false,
                searching: false,
                info: false,
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
        if (typeof id === "string") id = parseInt(id);
        this.path.push(id);
        this.id = id;
        this.updateBreadcrumb(type, id, name);
        this.detail();
    }
}