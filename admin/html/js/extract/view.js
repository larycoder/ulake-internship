import { ListCRUD } from "../crud/listcrud.js";
import { Api } from "http://common.dev.ulake.usth.edu.vn/js/api.js";

/**
 * Specific API for Extract Task management
 * We do it here since we don't want to mess with the all() method
 */
 class ExtractTaskApi extends Api {
    constructor (extractReqId) {
        super(getExtractUrl(), "/api/extract")
        this.extractReqId = extractReqId;
    }

    async all() {
        return await this.get(`/${this.extractReqId}/files`);
    }
}

function ready() {
    const params = parseParam("id", "/extracts");
    const extractReqId = parseInt(params.id);
    const extractTaskApi = new ExtractTaskApi(extractReqId);
    const fileCrud = new ListCRUD({
        api: extractTaskApi,
        listUrl: "/extracts",
        name: "Files",
        nameField: "id"
    });
    console.log('ready');
    fileCrud.ready();
}

$(document).ready(ready);