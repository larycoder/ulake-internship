
/**
 * A generic wrapper for data on the backend
 * For showing all entities in the same data table
 */
export class DataWrapper {
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
    transform() {
        return this.data;
    }
}