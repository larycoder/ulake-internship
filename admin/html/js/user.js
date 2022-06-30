// SSI: <!--# include file="../user.js" -->

async function userReady() {
    const userName = await user.getName(parseInt(getUid()))
    $("#userName").text(userName);
}

$(document).ready(userReady);