
user = {
    getUserName: (uid, callback) => {
        ajax({
            url: getUserUrl() + `/api/user/${uid}`,
            success: (data) => {
                if (data && data.code === 200) {
                    callback(data.resp.userName);
                }
            }
        });
    }
};