// SSI: <!--# include file="stats.js" -->

function adminReady() {
    user.getUserName(parseInt(getUid()), (userName) => {
        $("#userName").text(userName);
    })
}

$(document).ready(adminReady);