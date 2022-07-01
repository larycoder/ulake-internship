let users;

function deleteItem(id) {
    const user = users.filter(u => u.id === id);
    if (user.length === 0) {
        showModal("Error", `Weird, cannot find user with id ${id}`);
    }
    else {
        showModal("Error", `Are you sure to delete ${user[0].userName}?`, () => {
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
            { mData: "userName", render: (data, type, row) => `<a href="/user/show?uid=${row.id}">${data}</a>` },
            { mData: "registerTime", render: (data, type, row) => new Date(data*1000).toLocaleDateString() },
            { mData: "isAdmin", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""}>` },
            { mData: "id",
                render: (data, type, row) =>
                    `<a href="/user/edit?uid=${data}"><i class="fas fa-user-edit"></i></a>
                     <a href="#"><i class="fas fa-user-slash" onclick="deleteItem(${data})"></i></a>`
            }
        ]
    });
}
async function usersReady() {
    users = await userApi.all();
    const table = detail(users);

}

$(document).ready(usersReady);