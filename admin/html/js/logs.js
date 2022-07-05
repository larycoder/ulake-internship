// SSI CRUD: <!--# include virtual="/js/crud.js" -->

// add user name into view
class LogCRUD extends CRUD {
    async listDetail(data) {
        // extract unique user ids from data
        const uniqIds = data.map(entry => entry.ownerId)
            .filter((value, index, self) => self.indexOf(value) === index && value);
        let users = await userApi.many(uniqIds);
        if (uniqIds.length < 2) users = [ users ];

        // join on client side
        data = data.map(entry => {
            const user = users.filter(user => user.id == entry.ownerId);
            if (user && user.length && user[0].userName) entry.ownerId = user[0].userName;
            return entry;
        });
        super.listDetail(data);
    }
}

const logCrud = new LogCRUD({
    api: logApi,
    listUrl: "/tables",
    name: "Table",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "ownerId" },
        { mData: "content" },
        { mData: "service" },
        { mData: "tag" },
        { mData: "timestamp", render: (data, type, row) => new Date(data).toLocaleDateString() }

    ]
});

// TODO: add user - usergroup - group relation
$(document).ready(() => logCrud.listReady());