
/**
 * A generalized CRUD controller
 */
class CRUD {
    /**
     *
     * @param {Api} api Api to the backend
     * @param {string} listUrl Url to the UI's list view, e.g. /users
     * @param {string} name Name of the managed object, e.g. User
     * @param {string} nameField Name of the field that will be used for showing name
     * @param {string} listFieldRenderer Renderer for fields in the list. Ref <a href="https://legacy.datatables.net/usage/columns">DataTable aoColumns</a>
     * @param {string} hidden [optional] Fields that will be hidden from the UI, e.g. "department, failedLogins, groups"
     * @param {string} readonly [optional] Fields that will be read-only from the UI, e.g. "id,registerTime,userName,isAdmin"
     */
    constructor (config) {
        this.api = config.api;
        this.listUrl = config.listUrl;
        this.name = config.name;
        this.nameField = config.nameField;
        this.listFieldRenderer = config.listFieldRenderer;
        this.hidden = config.hidden? config.hidden.split(",").map((field) => field.trim()) : [];
        this.readonly = config.readonly? config.readonly.split(",").map((field) => field.trim()) : [];
    }

    /**
     * Show a delete item modal
     * @param {string} id Entity to be deleted
     */
    listDeleteItem(id) {
        const entity = this.entities.filter(e => e.id === id);
        if (entity.length === 0) {
            showModal("Error", `Weird, cannot find ${this.name} with id ${id}`);
        }
        else {
            showModal("Error", `Are you sure to delete ${entity[0][this.nameField]}?`, () => {
                console.log(`this.api.deleteOne(${entity[0].id})`);
            });
        }
    }

    /**
     *
     * @param {*} data Data to be shown on list
     * @returns
     */
    listDetail(data) {
        const _this = this;
        return $('#table').DataTable(  {
            data: data,
            bProcessing: true,
            paging: true,
            aoColumns: this.listFieldRenderer
        });
    }

    /**
     * Callback when the list UI is ready to be rendered
     */
    async listReady() {
        this.entities = await this.api.all();
        this.listDetail(this.entities);
    }


    /**
     *
     * @param {*} data Object for viewing
     * @returns
     */
    viewDetail(data) {
        return $('#table').DataTable(  {
            data: data,
            bProcessing: false,
            paging: false,
            searching: false,
            info: false,
            columns: [
                { data: "key" },
                { data: "value" }
            ]
        });
    }

    /**
     * Callback when the view UI is ready to be rendered
     */
     async viewReady() {
        const params = parseParam("id", this.listUrl);
        const id = parseInt(params.id);
        var info = await this.api.one(id);
        $("#name-detail").text(`Update ${this.name} Detail for ${info[this.nameField]}`);
        const table = toTable(info, this.hidden);
        this.viewDetail(table);
    }


    /**
     *
     * @param {*} data Object for editting
     * @returns
     */
    editDetail(data) {
        return $('#table').DataTable(  {
            data: data,
            bProcessing: false,
            paging: false,
            searching: false,
            info: false,
            aoColumns: [
                { mData: "key" },
                { mData: "value", render: (data, type, row) =>
                    `<input class="form-control border-1 small" type="text" value="${data}" data-for="${row.key}" ${row.readonly ? "readonly" : ""}>` }
            ]
        });
    }

    /**
     * Callback when the edit UI is ready to be rendered
     */
    async editReady() {
        const params = parseParam("id", this.listUrl);
        const id = parseInt(params.id);
        var info = await this.api.one(id);
        $("#name-detail").text(`Update ${this.name} Detail for ${info[this.nameField]}`);
        const table = toTable(info, this.hidden);
        table.forEach((data, index) => {
            if (this.readonly.includes(data.key)) {
                table[index].readonly = true;
            }
        });
        this.editDetail(table);
        $("#save").click(() => this.editSave());
    }

    async editSave() {
        // TODO: validate form input.
        const _crud = this;
        const ret = {};
        const rows = $("#table tbody tr");
        rows.each(function () {
            const _this = $(this);
            const key = _this.find("td:first-child").text();
            const value = _this.find("td:last-child input").val();
            if ((/*!_crud.readonly.includes(key) &&
                 !_crud.hidden.includes(key) &&*/
                !isEmpty(value)) || (key === "id")) {
                    ret[key] = value;
            }
        });
        const resp = await this.api.save(ret.id, JSON.stringify(ret));
        if (resp == null) { // no error
            showModal("Info", "Successfully saved", () => {
                window.location = this.listUrl;
            })
        }
    }
}