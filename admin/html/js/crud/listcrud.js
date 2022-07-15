import { CRUD } from './crud.js';

/**
 * A item list CRUD controller
 */
export class ListCRUD extends CRUD {
    /**
     *
     * @param {string} listFieldRenderer Renderer for fields in the list. Ref <a href="https://legacy.datatables.net/usage/columns">DataTable aoColumns</a>
     * @param {*} joins [optional] joining options, can be an array for multiple joins. apiMethod, fkField, targetId, targetField.
     */
    constructor (config) {
        super(config);
        this.listFieldRenderer = config.listFieldRenderer;
        this.joins = config.joins;
    }

    /**
     *
     * @param {*} data
     * @returns Joined data on the client side
     */
     async joinOne(joinOptions, data) {
        // extract unique foreign keys from data
        const uniqIds = data.map(entry => entry[joinOptions.fkField])
            .filter((value, index, self) => self.indexOf(value) === index && value);
        let others = await joinOptions.apiMethod(uniqIds);
        if (!Array.isArray(others)) others = [ others ];

        // join on client side
        data = data.map(entry => {
            // get the value on other object
            const other = others.filter(o => o[joinOptions.targetId] == entry[joinOptions.fkField]);

            // join, if valid
            if (other && other.length && other[0][joinOptions.targetField]) {
                entry[joinOptions.targetField] = other[0][joinOptions.targetField];
            }
            else entry[joinOptions.targetField]="";
            return entry;
        });
        return data;
    }

    /**
     * Join one or multiple tables into the final data
     * @param {*} data
     * @returns Joined data
     */
    async join(data) {
        if (this.joins)  {
            if (Array.isArray(this.joins)) {
                for (const joinOptions of this.joins) {
                    data = await this.joinOne(joinOptions, data);
                }
            }
            else data = await this.joinOne(this.joins, data);
        }
        return data;
    }

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
            showModal("Confirm", `Are you sure to delete ${entity[0][this.nameField]}?`, async () => {
                await this.api.deleteOne(entity[0].id);
                await this.fetch();
                await this.detail();
            });
        }
    }

    async detail() {
        const data = await this.join(this.data);
        if (!this.table) {
            this.table = $('#table').DataTable(  {
                data: data,
                paging: true,
                columns: this.listFieldRenderer,
                order: []
            });
        }
        else this.reloadTable(data);
    }

    async fetch() {
        this.data = await this.api.all();
    }

    getHeader() {
        return $('#table').parents("div[class*=card]").find("div h6");
    }
 }
