async function login() {
    const uid = document.querySelector("#uid").value,
          pwd = document.querySelector("#pwd").value;

    console.log(`Logging in as ${uid}, ${pwd}, domain ${getBaseDomain()}`);
    const button = $("form button");
    button.text("").append($(`<i class="fas fa-spinner fa-spin"></i>`));
    //const data = await userApi.login(uid, pwd);
    data = {};
    if (data && Object.keys(data).length > 0) {
        console.log(`Login ok, token=${data.resp}`);
        setToken(data.resp);
        const name = await userApi.getName(getUid());
        setUserName(name);
        if (!getGroups().includes("Admin")) {
            showModal("Error", "Sorry, you are not an admin.");
            setToken(null);
            return;
        }
        // window.location = "/";
    }
    else {
        showModal("Error", "Incorrect username/password.");
        button.text("Login")
    }
}