// SSI CRUD: <!--# include virtual="/js/crud.js" -->


/**
 * Specific API for Compress Task management
 */
 class CompressTaskApi extends Api {
    constructor (compressReqId) {
        super(getCompressUrl(), "/api/compress")
        this.compressReqId = compressReqId;
    }

    async all() {
        return await this.get(`/${this.compressReqId}/files`);
    }
}

function ready() {
    const params = parseParam("id", "/compresses");
    const compressReqid = parseInt(params.id);
    const compressTaskApi = new CompressTaskApi(compressReqid);
    const fileCrud = new CRUD({
        api: compressTaskApi,
        listUrl: "/compresses",
        name: "Files",
        nameField: "id",
        listFieldRenderer: [
            { mData: "id" },
            { mData: "name" },
            { mData: "size" },
            { mData: "mime" }
        ],
        joins: {
            apiMethod: (a) => fileApi.many(a),
            fkField: "fileId",
            targetId: "id",
            targetField: "name"
        }
    });

    fileCrud.listReady();
}

$(document).ready(ready);