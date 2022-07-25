import { ListCRUD } from "../crud/listcrud.js";
import { fileApi, IngestionApi } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

/**
 * Specific API for Ingestion File management
 * We do it here since we don't want to mess with the IngestionApi.all() method
 */
class IngestionFileApi extends IngestionApi {
    constructor (ingestionId) {
        super(getIngestionUrl(), "/api/extract");
        this.ingestionId = ingestionId;
    }

    async all() {
        return await this.files(this.ingestionId);
    }
}

async function ready() {
    const params = parseParam("id", "/ingestions");
    const id = parseInt(params.id);

    // initialize the list of ingested files
    const ingestionFileApi = new IngestionFileApi(id);
    window.crud = new ListCRUD({
        api: ingestionFileApi,
        name: "Ingested Files",
        nameField: "id",
        listFieldRenderer: [
            { data: "fileId" },
            { data: "name", render: (data, type, row) => `<a href="#" ${row.data ? "onclick=\"downloadFile(${row.fileId}, '${data}')\"" : ""}>${data}</a>` },
            { data: "mime" },
            { data: "uploadTime", render: (data, type, row) => formatTime(data) },
            { data: "status", render: (data, type, row) => data ? "Done" : "Failed" }
        ],
        joins: {
            apiMethod: a => fileApi.many(a),
            fkField: "fileId",
            targetId: "id",
            targetField: ["name", "mime"]
        }
    });

    // show current ingestion request info
    const ingestion = await window.crud.api.one(id);
    const keyPairs = toTable(ingestion, ["query"], {
        creationTime: (d) => formatTime(d),
        endTime: (d) => formatTime(d)
    });
    $('#param-table').DataTable(  {
        data: keyPairs,
        paging: false,
        searching: false,
        info: false,
        columns: [
            { data: "key" },
            { data: "value" }
        ]
    });

    // go.
    window.crud.ready();
}


$(document).ready(() => ready());
