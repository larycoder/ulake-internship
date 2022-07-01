function detail(data) {
    return $('#table').DataTable(  {
        data: data,
        bProcessing: false,
        paging: false,
        searching: false,
        info: false,
        aoColumns: [
            { mData: "key" },
            { mData: "value", render: (data, type, row) =>
                `<input class="form-control bg-light border-0 small" type="text" value="${data}" data-for="${row.key}" ${row.readonly ? "readonly" : ""}>` }
        ]
    });
}

async function userReady() {
    const params = parseParam("uid", "/users");
    const uid = parseInt(params.uid);
    var userInfo = await userApi.one(uid);
    $("#username-detail").text(`Update User Detail for ${userInfo.userName}`);
    const table = toTable(userInfo, "department, failedLogins, groups");
    const readonly = "id,registerTime,userName,isAdmin".split(",");
    table.forEach((data, index) => {
        if (readonly.includes(data.key)) {
            table[index].readonly = true;
        }
    });
    detail(table);
    $("#save").click(saveUser);
}

async function saveUser() {
    showModal("Info", "Saving users");
}

$(document).ready(userReady);