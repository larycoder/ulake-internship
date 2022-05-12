/**
 * Support login action
 */

/**
 * login method compatible to login-modal and navigation bar login (home.html)
 */
function login() {
    let loginProcess = new ProgressController("progress-modal");
    let user = new UserModel();
    user.username = document.getElementById("login-modal-username").value;
    user.password = document.getElementById("login-modal-password").value;
    query = new ULakeQueryClient();

    /* start pushing query */
    loginProcess.start();

    query.login(user, (user) => {
        localStorage.setItem(globalObject.tokenId, user.getToken());
        loadGlobal();
        closeModal("login-modal");
        loginProcess.end();
    });

    /* done progressing */
    loginProcess.waitUntilEnd();
}
