import { CRUD } from './crud.js';

/**
 * A item list CRUD controller
 */
export class ListCRUD extends CRUD {
    /**
     * Show a delete item modal for confirmation
     * @param {string} id Entity to be deleted
     */
    confirm(id) {
        const entity = this.data.filter(e => e.id === id);
        if (entity.length === 0) {
            showModal("Error", `Weird, cannot find ${this.name} with id ${id}`);
        }
        else {
            showModal("Confirm", `Are you sure to delete ${entity[0][this.nameField]}?`, () => {
                this.api.deleteOne(entity[0].id);
            });
        }
    }

    async detail() {
        const data = await this.join(this.data);
        const _this = this;
        const header = $('#table').parents("div h6");
        const title = header.text();
        header.text()
        this.table = $('#table').DataTable(  {
            data: data,
            paging: true,
            aoColumns: this.listFieldRenderer
        });
    }

    async fetch() {
        this.data = await this.api.all();
    }
 }
