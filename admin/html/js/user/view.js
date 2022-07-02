// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const userCrud = new CRUD({
    api: userApi,
    listUrl: "/users",
    name: "User",
    nameField: "userName",
    hidden: "department, failedLogins, groups"
});

$(document).ready(() => userCrud.viewReady());  // to keep proper 'this' context