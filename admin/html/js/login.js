function login() {
    const uid = $("#uid").val(),
        pwd = $("#pwd").val();
    console.log(`Logging in as ${uid}, ${pwd}, domain ${getBaseDomain()}`);
    $.post({
        url: getUserUrl() + "/api/auth/login",
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({
            userName: uid,
            password: pwd
        }),
        success: (data) => {
            if (data.code === 200) {
                console.log(`Login ok, token=${data.resp}`);
                setToken(data.resp);
            }
            window.location = "/";
        },
        error: (e) => {
            window.alert(`Login error: ${JSON.stringify(e)}`);
        }
    });
}