// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const logCrud = new CRUD({
    api: logApi,
    listUrl: "/tables",
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "content" },
        { mData: "service" },
        { mData: "tag" },
        { mData: "timestamp", render: (data, type, row) => new Date(data).toLocaleDateString() }

    ]
});

// TODO: add user - usergroup - group relation
$(document).ready(() => logCrud.listReady());