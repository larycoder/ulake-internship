// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const fileCrud = new CRUD({
    api: fileApi,
    listUrl: "/compress",
    name: "Files",
    nameField: "id",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "name" },
        { mData: "size" },
        { mData: "mime" }
    ]
});

$(document).ready(() => fileCrud.listReady());