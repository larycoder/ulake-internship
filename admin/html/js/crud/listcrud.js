import { CRUD } from './crud.js';

/**
 * A item list CRUD controller
 */
export class ListCRUD extends CRUD {
    /**
     * Show a delete item modal
     * @param {string} id Entity to be deleted
     */
    confirm(id) {
        const entity = this.entities.filter(e => e.id === id);
        if (entity.length === 0) {
            showModal("Error", `Weird, cannot find ${this.name} with id ${id}`);
        }
        else {
            showModal("Error", `Are you sure to delete ${entity[0][this.nameField]}?`, () => {
                this.api.deleteOne(entity[0].id);
            });
        }
    }

    async detail() {
        const data = await this.join(this.entities);
        const _this = this;
        this.table = $('#table').DataTable(  {
            data: data,
            paging: true,
            aoColumns: this.listFieldRenderer
        });
    }

    async ready() {
        this.entities = await this.api.all();
        this.detail();
    }
 }
