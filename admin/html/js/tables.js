// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const tableCrud = new CRUD({
    api: tableApi,
    listUrl: "/tables",
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "name", render: (data, type, row) => `<a href="/table/view?id=${row.id}">${data}</a>` },
        { mData: "format" },
        { mData: "creationTime", render: (data, type, row) => new Date(data).toLocaleDateString() },
        { mData: "id",
            render: (data, type, row) => `<a href="#"><i class="fas fa-trash" onclick="tableCrud.listDeleteItem(${data})"></i></a>`
        }
    ]
});

// TODO: add user - usergroup - group relation
$(document).ready(() => tableCrud.listReady());