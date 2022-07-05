// SSI CRUD: <!--# include virtual="/js/crud.js" -->

// add user name into view
class LogCRUD extends CRUD {
    async listDetail(data) {
        const uniqIds = data.map(log => log.ownerId)
            .filter((value, index, self) => self.indexOf(value) === index && value);
        console.log(uniqIds);
        const users = await userApi.one(uniqIds);
        console.log(users);

        // join on client side
        data = data.map(log => {
            const user = users.filter(user => user.id == log.ownerId);
            if (user && user.length && user[0].userName) log.ownerId = user[0].userName;
            return log;
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