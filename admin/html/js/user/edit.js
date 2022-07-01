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
                `<input class="form-control border-1 small" type="text" value="${data}" data-for="${row.key}" ${row.readonly ? "readonly" : ""}>` }
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
    // TODO: validate form input.
    const ret = {};
    const rows = $("#table tbody tr");
    rows.each(function () {
        const _this = $(this);
        const key = _this.find("td:first-child").text();
        const value = _this.find("td:last-child input").val();
        ret[key] = value;
    });
    userApi.save(ret.id, ret, {contentType: "application/json; charset=utf-8"});
}

$(document).ready(userReady);