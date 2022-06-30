// SSI: <!--# include file="stats.js" -->

async function adminReady() {
    const userName = await user.getName(parseInt(getUid()));
    $("#userName").text(userName);
}

$(document).ready(adminReady);