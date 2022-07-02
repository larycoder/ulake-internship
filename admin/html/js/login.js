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
        success: async (data) => {
            if (data.code === 200) {
                console.log(`Login ok, token=${data.resp}`);
                setToken(data.resp);
                const name = await userApi.getName(getUid());
                setUserName(name);
                if (!getGroups().includes("Admin")) {
                    showModal("Error", "Sorry, you are not an admin.");
                    setToken(null);
                    return;
                }
            }
            window.location = "/";
        },
        error: (e) => {
            window.alert(`Login error: ${JSON.stringify(e)}`);
        }
    });
}