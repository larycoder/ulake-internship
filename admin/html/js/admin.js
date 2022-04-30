class User {
    getUserName(uid, callback) {
        ajax({
            url: getUserUrl() + `/api/user/${uid}`,
            success: (data) => {
                if (data && data.code === 200) {
                    callback(data.resp.userName);
                }
            }
        });
    }
}

class Admin {
    user = new User();

    getUserName(uid, callback) {
        this.user.getUserName(uid, callback)
    }
}

admin = new Admin();

function adminReady() {
    admin.getUserName(parseInt(getUid()), (userName) => {
        $("#userName").text(userName);
    })
}
