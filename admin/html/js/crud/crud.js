
/**
 * A generalized CRUD controller
 */
export class CRUD {
    /**
     *
     * @param {Api} api Api to the backend
     * @param {string} listUrl Url to the UI's list view, e.g. /users
     * @param {string} name Name of the managed object, e.g. User
     * @param {string} nameField Name of the field that will be used for showing name
     * @param {string} listFieldRenderer Renderer for fields in the list. Ref <a href="https://legacy.datatables.net/usage/columns">DataTable aoColumns</a>
     * @param {string} hidden [optional] Fields that will be hidden from the UI, e.g. "department, failedLogins, groups"
     * @param {string} readonly [optional] Fields that will be read-only from the UI, e.g. "id,registerTime,userName,isAdmin"
     * @param {*} joins [optional] joining options, can be an array for multiple joins. apiMethod, fkField, targetId, targetField.
     */
    constructor (config) {
        this.api = config.api;
        this.listUrl = config.listUrl;
        this.name = config.name;
        this.nameField = config.nameField;
        this.listFieldRenderer = config.listFieldRenderer;
        this.hidden = config.hidden? config.hidden.split(",").map((field) => field.trim()) : [];
        this.readonly = config.readonly? config.readonly.split(",").map((field) => field.trim()) : [];
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
     * Page ready
     */
    async ready() {
        await this.fetch();
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