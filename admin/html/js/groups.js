let users;

function deleteItem(id) {
    const user = groups.filter(u => u.id === id);
    if (user.length === 0) {
        showModal("Error", `Weird, cannot find user with id ${id}`);
    }
    else {
        showModal("Error", `Are you sure to delete group "${user[0].name}"?`, () => {
            userApi.deleteOne(user[0].userName);
        });
    }
}

function detail(data) {
    return $('#table').DataTable(  {
        data: data,
        bProcessing: true,
        paging: true,
        aoColumns: [
            { mData: "id" },
            { mData: "name", render: (data, type, row) => `<a href="/group/show?uid=${row.id}">${data}</a>` },
            { mData: "id",
                render: (data, type, row) =>
                    `<a href="/user/edit?uid=${data}"><i class="fas fa-users-cog"></i></a>
                     <a href="#"><i class="fas fa-users-slash" onclick="deleteItem(${data})"></i></a>`
            }
        ]
    });
}
async function groupsReady() {
    groups = await groupApi.all();
    const table = detail(groups);
}

$(document).ready(groupsReady);