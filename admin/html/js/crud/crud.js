
/**
 * A generalized CRUD controller
 */
export class CRUD {
    /**
     *
     * @param {Api} api Api to the backend
     * @param {string} name Name of the managed object, e.g. User
     * @param {string} nameField Name of the field that will be used for showing name
    */
    constructor (config) {
        this.api = config.api;
        this.name = config.name;
        this.nameField = config.nameField;
    }

    reloadTable(data) {
        if (this.table) {
            this.table.clear().draw();
            this.table.rows.add(data);
            this.table.columns.adjust().draw();
        }
    }

    /**
     * Get the header element for this UI
     */
    getHeader() {
        return $();
    }

    /**
     * Start spinner in the UI
     */
    startSpinner() {
        const header = this.getHeader();
        this.headerTitle = header.text();
        header.text("").append('<i class="fas fa-spinner fa-spin"></i>');
    }

    /**
     * Stop spinner in the UI
     */
    stopSpinner() {
        this.getHeader().text(this.headerTitle);
    }

    /**
     * Do something before fetch()
     */
    prefetch() {
        this.startSpinner();
    }

    /**
     * Do something after fetch()
     */
     postfetch() {
        this.stopSpinner();
    }

    /**
     * Page ready
     */
    async ready() {
        this.prefetch();
        await this.fetch();
        this.postfetch();
        await this.detail();
    }

    /**
     * Fetch info from server, to be overridden by subclasses
     */
    async fetch() {

    }

    /**
     * Show detail info, to be overridden by subclasses
     */
    async detail() {

    }

    /**
     * Confirmation, to be overridden by subclasses
     */
    async confirm() {

    }
}