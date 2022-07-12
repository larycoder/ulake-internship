
/**
 * A generic wrapper for data on the backend
 * For showing all entities in the same data table
 */
export class BaseWrapper {
    constructor (config) {
        this.api = config.api;
    }

    /**
     * @param {parent} parent of the current object
     * @returns list of objects belonging to the parent
     */
    async fetch(parent) {
        this.data = await this.api.all();
        return this.data;
    }

    /**
     * Transform object to a view-friendly object, for later polymorphs
     */
    transform(raw) {
        return raw;
    }

    /**
     * Default DataTable renderer
     * @param {*} data
     * @param {*} type
     * @param {*} row
     * @returns
     */
    defaultRenderer(data, type, row) {
        return data;
    }

    /**
     * @returns DataTable renderers for all fields
     */
    getAllRenderers() {
        return {
            id: this.defaultRenderer,
            name: this.defaultRenderer,
            type: this.defaultRenderer,
            size: this.defaultRenderer,
            action: this.defaultRenderer
        }
    }

    /**
     *
     * @param {string} field
     * @returns DataTable renderers for a specific field
     */
    getRenderer(field) {
        console.log("getting renderer for ", field, "out", this.getAllRenderers()[field]);
        return this.getAllRenderers()[field];
    }
}