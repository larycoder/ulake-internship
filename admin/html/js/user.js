// SSI: <!--# include file="../user.js" -->

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
    let urlParams = new URLSearchParams(location.search);
    if (!urlParams.has('uid')) {
        window.location = "/users";
        return;
    }
    const uid = parseInt(urlParams.get("uid"));
    var userInfo = await user.one(uid);
    $("#username-detail").text(`User Detail for ${userInfo.userName}`);
    detail(toTable(userInfo, "department, failedLogins, groups, firstName, lastName"));
}

$(document).ready(userReady);