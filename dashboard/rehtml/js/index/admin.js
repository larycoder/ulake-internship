// SSI: <!--# include file="user.js" -->

admin = {
    getUserName: (uid, callback) => {
        user.getUserName(uid, callback)
    }
};

// SSI: <!--# include file="stats.js" -->

function adminReady() {
    admin.getUserName(parseInt(getUid()), (userName) => {
        $("#userName").text(userName);
    })
}
