// SSI CRUD: <!--# include virtual="/js/crud.js" -->

const crud = new CRUD({
    api: groupApi,
    listUrl: "/groups",
    name: "Group",
    nameField: "name"
});

async function confirm(modal) {
    let table = modal.find("#add-table").DataTable();
    const select = table.rows( { selected: true } ).data();
    let entity = crud.info;
    entity.users = [];
    select.each(u => {
        entity.users.push({ id: u.id, userName: u.userName });
    });
    console.log(entity);
    crud.api.save(crud.id, entity);
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

async function showModal() {
    const modal = $(this);
    let table = modal.find("#add-table")
    if (!$.fn.DataTable.isDataTable("#add-table")) table = initTable(table);
    else table.DataTable().rows().deselect();

}

function viewReady() {
    // prepare modal events
    const modal = $("#add-modal");
    modal.on("show.bs.modal", showModal);
    modal.find(".btn-primary").on("click", () => confirm(modal));
    crud.viewReady();
}

$(document).ready(viewReady);