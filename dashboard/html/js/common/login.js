/**
 * Support login action
 */

/**
 * login method compatible to login-modal and navigation bar login (home.html)
 * @param 
 */
function login() {
    let loginProcess = new ProgressController("progress-modal-button");
    let user = new UserModel();
    user.username = document.getElementById("login-modal-username").value;
    user.password = document.getElementById("login-modal-password").value;
    query = new ULakeQueryClient();

    /* start pushing query */
    loginProcess.start();

    query.login(user, (user) => {
        localStorage.setItem(globalObject.tokenId, user.getToken());
        loadGlobal();

        loginProcess.end();

        document.getElementById("login-modal-close").click();
        document.getElementById("navbar-login-refresh").text = "Refresh";
    });

    /* done progressing */
    loginProcess.waitUntilEnd();
}

/**
 * this script allow update login icons depending on token
 */
function updateLoginInfo() {
    loginNavbar = document.getElementById("navbar-login-refresh");
    if (loginNavbar != undefined) {
        let token = globalObject.token;
        if (token != undefined && token.length > 0) {
            loginNavbar.text = "Refresh";
        }
    }
}
