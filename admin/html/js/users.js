// SSI: <!--# include file="../user.js" -->

function list(data) {
    return $('#table').DataTable(  {
        data: data,
        bProcessing: true,
        paging: true,
        aoColumns: [
            { mData: "id" },
            { mData: "userName" },
            { mData: "registerTime", render: (data, type, row) => new Date(data*1000).toLocaleDateString() },
            { mData: "isAdmin", render: (data, type, row) => `<input type="checkbox" ${data === true? "checked" : ""}>` },
            { mData: null }
        ],
        "columnDefs": [
            {
                "targets": -1,
                "data": null,
                "defaultContent": `<i id="edit" style="font-size: 1rem" class="fas fa-user-edit"></i> <i id="delete" style="font-size: 1rem" class="fas fa-user-slash"></i>`
             }
        ]
    });
}
async function userReady() {
    const users = await user.all();
    console.log(users);
    list(users);
}

$(document).ready(userReady);