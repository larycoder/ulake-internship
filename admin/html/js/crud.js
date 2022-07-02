
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
     * @param {string} hidden Fields that will be hidden from the UI, e.g. "department, failedLogins, groups"
     * @param {string} readonly Fields that will be read-only from the UI, e.g. "id,registerTime,userName,isAdmin"
     */
    constructor (api, listUrl, name, nameField, hidden, readonly) {
        if (typeof api === "string") {
            this.api = api;
            this.listUrl = listUrl;
            this.name = name;
            this.nameField = nameField;
            this.blacklist = hidden? hidden.split(",").map((field) => field.trim()) : [];
            this.readonly = readonly? readonly.split(",").map((field) => field.trim()) : [];
        }
        else {
            // that's an object
            this.api = api.api;
            this.listUrl = api.listUrl;
            this.name = api.name;
            this.nameField = api.nameField;
            this.blacklist = api.hidden? api.hidden.split(",").map((field) => field.trim()) : [];
            this.readonly = api.readonly? api.readonly.split(",").map((field) => field.trim()) : [];
        }
    }
    detail(data) {
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

    async editReady() {
        const params = parseParam("id", this.listUrl);
        const id = parseInt(params.id);
        var info = await this.api.one(id);
        $("#name-detail").text(`Update ${this.name} Detail for ${info[this.nameField]}`);
        const table = toTable(info, this.hidden);
        const readonly = this.readonly;
        table.forEach((data, index) => {
            if (readonly.includes(data.key)) {
                table[index].readonly = true;
            }
        });
        this.detail(table);
        $("#save").click(() => this.save());
    }

    async save() {
        // TODO: validate form input.
        const ret = {};
        const rows = $("#table tbody tr");
        rows.each(function () {
            const _this = $(this);
            const key = _this.find("td:first-child").text();
            const value = _this.find("td:last-child input").val();
            if (!isEmpty(value)) ret[key] = value;
        });
        const resp = await this.api.save(ret.id, JSON.stringify(ret));
        if (resp == null) { // no error
            showModal("Info", "Successfully saved", () => {
                window.location = this.listUrl;
            })
        }
    }
}