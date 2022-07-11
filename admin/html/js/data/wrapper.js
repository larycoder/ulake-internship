
/**
 * A generic wrapper for data on the backend
 */
export class DataWrapper {
    constructor (config) {
        this.api = config.api;
    }

    async fetch(){
        this.data = await this.api.all();
        return this.data;
    }

    /**
     * Getter, for later polymorphism
     */
    getData() {
        return this.data;
    }
}