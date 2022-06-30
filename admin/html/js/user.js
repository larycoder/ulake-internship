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
    var transform = [];
    for (var key in userInfo) {
        transform.push( {
           key: key,
           value: userInfo[key]
        });
    }
    $("#username-detail").text(`User Detail for ${userInfo.userName}`);

    detail(transform);
}

$(document).ready(userReady);