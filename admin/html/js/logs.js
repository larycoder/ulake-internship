// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const compressCrud = new CRUD({
    api: logApi,
    listUrl: "/tables",
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userName" },
        { mData: "content" },
        { mData: "service" },
        { mData: "tag" },
        { mData: "timestamp", render: (data, type, row) => new Date(data).toLocaleDateString() }
    ],
    joins: {
        apiMethod: (a) => userApi.many(a),
        fkField: "ownerId",
        targetId: "id",
        targetField: "userName"
    }
});

$(document).ready(() => compressCrud.listReady());