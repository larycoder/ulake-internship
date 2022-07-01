function detail(data) {
    return $('#table').DataTable(  {
        data: data,
        bProcessing: false,
        paging: false,
        searching: false,
        info: false,
        aoColumns: [
            { mData: "key" },
            { mData: "value" }
        ]
    });
}

async function userReady() {
    const params = parseParam(uid, "/users");
    const uid = parseInt(params.uid);
    var userInfo = await userApi.one(uid);
    $("#username-detail").text(`User Detail for ${userInfo.userName}`);
    detail(toTable(userInfo, "department, failedLogins, groups, firstName, lastName"));
}

$(document).ready(userReady);