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

stats = {
    updateStats: ()=>{
        ajax({
            url: `/api/admin/users/stats`,
            success: (data) => {
                if (data && data.code === 200) {
                    console.log(data);
                }
            }
        });
    }
}

admin = new Admin();

function adminReady() {
    admin.getUserName(parseInt(getUid()), (userName) => {
        $("#userName").text(userName);
    })
}
