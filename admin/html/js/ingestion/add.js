import { ingestionTemplateApi, ingestionApi } from "../api.js";

class AddIngestionRequest {
    constructor () {
        this.api = ingestionApi;
        this.templateApi = ingestionTemplateApi;
    }

    async ready() {
        // get template query from server
        const params = parseParam("id", "/ingestion/template");
        this.id = parseInt(params.id);
        this.template = await this.templateApi.one(this.id);
        $("#name-detail").text(`Parameters for "${this.template.description}"`);
        console.log(this.template);

        // parse to table model
        const declare = Object.assign({}, this.template.query.declare);
        for (var key in declare) {
            declare[key] = "";
        }
        declare.description = this.template.description;
        declare.folder = "";
        const keyPairs = toTable(declare);

        // show the table
        this.table = $('#param-table').DataTable(  {
            data: keyPairs,
            paging: false,
            searching: false,
            info: false,
            columns: [
                { data: "key" },
                { data: "value", render: (data, type, row) =>
                            `<input class="form-control border-1 small" type="text" value="${data}"}>` }
            ]
        });
    }

    async add() {
        const post = {};
        const rows = $("#param-table tbody tr");
        const param = {};
        rows.each(function () {
            const _this = $(this);
            const key = _this.find("td:first-child").text();
            var value = _this.find("td:last-child input").val();
            if ((!isEmpty(value)) || (key === "id")) {
                if (key.startsWith("$")) {
                    value = value.split(",").map(v => v.trim());
                    post[key] = value;
                }
                else {
                    param[key] = value;
                }
            }
        });
        for (const k in post) {
            console.log(k, post[k]);
            this.template.query.declare[k] = post[k];
        }
        console.log(param);
        const resp = await this.api.crawl(this.template.query, param.folder, param.description);
        if (resp != null) {
            showModal("Info", "Ingestion request submitted successfully.", () => {
                //
            })
        }
    }
}

window.crud = new AddIngestionRequest();

$(document).ready(() => window.crud.ready());