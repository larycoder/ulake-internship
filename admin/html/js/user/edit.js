// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const userCrud = new CRUD({
    api: userApi,
    listUrl: "/users",
    name: "User",
    nameField: "userName",
    hidden: "department, failedLogins, groups",
    readonly: "id, registerTime, userName, isAdmin"
});

$(document).ready(() => userCrud.editReady());  // to keep proper 'this' context