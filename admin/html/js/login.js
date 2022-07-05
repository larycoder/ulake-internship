async function login() {
    const uid = $("#uid").val(),
        pwd = $("#pwd").val();
    console.log(`Logging in as ${uid}, ${pwd}, domain ${getBaseDomain()}`);
    const resp = await fetch(getUserUrl() + "/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json; charset=utf-8" },
        body: JSON.stringify({
            userName: uid,
            password: pwd
        })
    });
    const data = await resp.json();
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
        window.location = "/";
    }
}