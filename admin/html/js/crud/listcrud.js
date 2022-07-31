import { CRUD } from './crud.js';

/**
 * A item list CRUD controller
 */
export class ListCRUD extends CRUD {
    /**
     *
     * @param {string} listFieldRenderer Renderer for fields in the list. Ref <a href="https://legacy.datatables.net/usage/columns">DataTable aoColumns</a>
     * @param {*} joins [optional] joining options, can be an array for multiple joins. apiMethod, fkField/fkMapper, targetId, targetField.
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
        const uniqKeys = data.map(joinOptions.fkMapper ?
                    entry => joinOptions.fkMapper(entry) :  // use provided foreign key mapper
                    entry => entry[joinOptions.fkField])    // nah, map using fkField by default
            .filter((value, index, self) => self.indexOf(value) === index && value);
        console.log("uniq keys", uniqKeys);
        let others = await joinOptions.apiMethod(uniqKeys);
        if (!Array.isArray(others)) others = [ others ];

        // join on client side
        data = data.map(entry => {
            // get the value on other object
            const other = others.filter(o => o[joinOptions.targetId] == entry[joinOptions.fkField]);

            // join, if valid
            if (!Array.isArray(joinOptions.targetField)) joinOptions.targetField = [ joinOptions.targetField ];
            for (const f of joinOptions.targetField) {
                if (other && other.length && other[0][f]) {
                    entry[f] = other[0][f];
                }
                else entry[f]=entry[f] || "";
            }
            return entry;
        });
        console.log("joined: ", JSON.parse(JSON.stringify(data)));
        return data;
    }

    /**
     * Join one or multiple tables into the final data
     * @param {*} data
     * @returns Joined data
     */
    async join(data) {
        if (this.joins)  {
            if (!Array.isArray(this.joins)) this.joins = [ this.joins ];
            for (const joinOptions of this.joins) {
                data = await this.joinOne(joinOptions, data);
            }
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
        if (!this.table && data) {
            this.table = $('#table').DataTable(  {
                data: data,
                paging: true,
                columns: this.listFieldRenderer,
                buttons: [ 'csv' ],
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
