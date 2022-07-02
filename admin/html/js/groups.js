// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const crud = new CRUD({
    api: groupApi,
    listUrl: "/groups",
    name: "Group",
    nameField: "name",
    listFieldRenderer: [
        { mData: "id" },
        { mData: "name", render: (data, type, row) => `<a href="/group/view?id=${row.id}">${data}</a>` },
        { mData: "id",
            render: (data, type, row) =>
                `<a href="/group/edit?id=${data}"><i class="fas fa-user-cog"></i></a>
                 <a href="#"><i class="fas fa-users-slash" onclick="crud.listDeleteItem(${data})"></i></a>`
        }
    ]
});

$(document).ready(() => crud.listReady());