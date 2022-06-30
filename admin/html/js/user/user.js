// SSI: <!--# include file="../user.js" -->

function userReady() {
    user.getUserName(parseInt(getUid()), (userName) => {
        $("#userName").text(userName);
    })
}
