// $(document).ready(userReady);

// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const crud = new CRUD({
    api: groupApi,
    listUrl: "/groups",
    name: "Group",
    nameField: "name"
});

async function confirm(modal) {
    let table = modal.find("#add-table").DataTable();
    const selected = table.rows( { selected: true } ).data();
    // TODO: POST to server
    modal.modal('hide');
}

async function initTable(table) {
    const data = await userApi.all();
    table = table.DataTable({data: data,
        select: { style: 'multi' },
        columns: [
            { mData: "id" },
            { mData: "userName" }
        ]
    });
}

async function showAddModal() {
    const modal = $(this);
    let table = modal.find("#add-table")
    if (!$.fn.DataTable.isDataTable("#add-table")) table = initTable(table);
}

function viewReady() {
    // prepare modal events
    const modal = $("#add-modal");
    modal.on("show.bs.modal", showAddModal);
    modal.find(".btn-primary").on("click", () => confirm(modal));
    crud.viewReady();
}

$(document).ready(viewReady);  // to keep proper 'this' context