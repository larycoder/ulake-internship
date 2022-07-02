// $(document).ready(userReady);

// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const crud = new CRUD({
    api: groupApi,
    listUrl: "/groups",
    name: "Group",
    nameField: "name"
});

$(document).ready(() => crud.viewReady());  // to keep proper 'this' context