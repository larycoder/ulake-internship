// SSI CRUD: <!--# include virtual="/js/crud.js" -->

// add user name into view
class TableCRUD extends CRUD {
    async listDetail(data) {
        // extract unique user ids from data
        const uniqIds = data.map(entry => entry.ownerId)
            .filter((value, index, self) => self.indexOf(value) === index && value);
        console.log(uniqIds);
        let users = await userApi.many(uniqIds);
        if (uniqIds.length < 2) users = [ users ];
        console.log(users);

        // join on client side
        data = data.map(entry => {
            const user = users.filter(user => user.id == entry.ownerId);
            if (user && user.length && user[0].userName) entry.user = user[0].userName;
            else entry.user = "";
            return entry;
        });
        console.log(data);
        super.listDetail(data);
    }
}


const tableCrud = new TableCRUD({
    api: tableApi,
    listUrl: "/tables",
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "user" },
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