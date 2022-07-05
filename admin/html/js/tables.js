// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const tableCrud = new CRUD({
    api: tableApi,
    listUrl: "/tables",
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userName" },
        { mData: "name", render: (data, type, row) => `<a href="/table/view?id=${row.id}">${data}</a>` },
        { mData: "format" },
        { mData: "creationTime", render: (data, type, row) => new Date(data).toLocaleDateString() },
        { mData: "id",
            render: (data, type, row) => `<a href="#"><i class="fas fa-trash" onclick="tableCrud.listDeleteItem(${data})"></i></a>`
        }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "ownerId",
        targetId: "id",
        targetField: "userName"
    }
});

// TODO: add user - usergroup - group relation
$(document).ready(() => tableCrud.listReady());