// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const userCrud = new CRUD({
    api: userApi,
    listUrl: "/users",
    name: "User",
    nameField: "userName",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "userName", render: (data, type, row) => `<a href="/user/view?id=${row.id}">${data}</a>` },
        { mData: "registerTime", render: (data, type, row) => new Date(data*1000).toLocaleDateString() },
        { mData: "isAdmin", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""} disabled >` },
        { mData: "id",
            render: (data, type, row) =>
                `<a href="/user/edit?id=${data}"><i class="fas fa-user-edit"></i></a>
                 <a href="#"><i class="fas fa-user-slash" onclick="userCrud.listDeleteItem(${data})"></i></a>`
        }
    ]
});

$(document).ready(() => userCrud.listReady());